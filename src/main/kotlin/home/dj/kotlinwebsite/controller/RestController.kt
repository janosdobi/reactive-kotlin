package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.GameDTO
import home.dj.kotlinwebsite.model.JoinGameRequestDTO
import home.dj.kotlinwebsite.model.NewGameRequestDTO
import home.dj.kotlinwebsite.model.PlayerDTO
import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.Player
import home.dj.kotlinwebsite.persistence.repo.GameRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

@RestController
@RequestMapping("/api")
class RestController(
    private val gameRepository: GameRepository
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
                        listOf(Player(principal.name, it.playerName))
                    )
                )
            }
            .map { game ->
                GameDTO(
                    game.code,
                    game.players.map { PlayerDTO(it.uid, it.name) }
                )
            }
    }

    @PostMapping("v1/join-game", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun joinGame(principal: Principal, @RequestBody request: Mono<JoinGameRequestDTO>): Mono<GameDTO> {
        return request
            .flatMap {
                Mono.zip(
                    Mono.just(Player(principal.name, it.playerName)),
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
                    game.players.map { PlayerDTO(it.uid, it.name) }
                )
            }
    }
}
