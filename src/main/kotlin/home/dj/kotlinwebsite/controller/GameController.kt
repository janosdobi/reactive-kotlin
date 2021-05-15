package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.*
import home.dj.kotlinwebsite.model.EventType.PLAYER_JOINED
import home.dj.kotlinwebsite.model.EventType.PLAYER_LEFT
import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.Player
import home.dj.kotlinwebsite.persistence.repo.GameRepository
import home.dj.kotlinwebsite.service.GameEventManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

@RestController
@RequestMapping("/api")
class GameController(
    private val gameRepository: GameRepository,
    private val gameEventManager: GameEventManager
) {

    @PostMapping("v1/new-game", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun newGame(principal: Principal, @RequestBody request: Mono<NewGameRequestDTO>): Mono<GameDTO> {
        return request
            .flatMap {
                gameRepository.save(
                    Game(
                        (1..6)
                            .map { kotlin.random.Random.nextInt(0, charPool.size) }
                            .map(charPool::get)
                            .joinToString(""),
                        listOf(Player(it.playerName))
                    )
                )
            }
            .map { game ->
                GameDTO(
                    game.code,
                    game.players.map { PlayerDTO(it.name) }
                )
            }
            .doOnNext { gameEventManager.createNewPublisherForGame(it.code) }
    }

    @PostMapping("v1/join-game", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun joinGame(principal: Principal, @RequestBody request: Mono<JoinGameRequestDTO>): Mono<GameDTO> {
        return request
            .doOnNext {
                gameEventManager.publishEvent(
                    GameEventDTO(
                        PLAYER_JOINED,
                        it.playerName,
                        it.playerName,
                        it.gameId
                    )
                )
            }
            .flatMap {
                Mono.zip(
                    Mono.just(Player(it.playerName)),
                    gameRepository.findGameByCode(it.gameId)
                )
            }
            .flatMap {
                val game = it.t2
                game.players = game.players.plusElement(it.t1)
                return@flatMap gameRepository.save(game)
            }
            .map { game ->
                GameDTO(
                    game.code,
                    game.players.map { PlayerDTO(it.name) }
                )
            }
    }

    @GetMapping("v1/players/{gameCode}")
    @ResponseStatus(HttpStatus.OK)
    fun getPlayers(@PathVariable gameCode: String): Flux<PlayerDTO> {
        return gameRepository.findGameByCode(gameCode)
            .flatMapMany { Flux.fromIterable(it.players) }
            .map { PlayerDTO(it.name) }
    }

    @GetMapping("v1/quit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun quitGame(@RequestBody request: Mono<QuitGameRequestDTO>) {
        println("request received!")
        //TODO continue from here - remove player from collection and persist
        request
            .doOnNext { gameEventManager.publishEvent(GameEventDTO(PLAYER_LEFT, it.playerName, it.playerName, it.gameId)) }
            .map { gameRepository.findGameByPlayersContains(Player(it.playerName)) }
            .block()
    }
}
