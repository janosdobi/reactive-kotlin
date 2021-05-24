package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.model.*
import home.dj.kotlinwebsite.model.EventType.GAME_FINISHED
import home.dj.kotlinwebsite.model.EventType.GAME_STARTED
import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.GameStatus
import home.dj.kotlinwebsite.persistence.document.GameStatus.*
import home.dj.kotlinwebsite.persistence.document.Player
import home.dj.kotlinwebsite.repository.GameRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.random.Random

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val gameEventManager: GameEventManager
) {
    fun createNewGame(request: Mono<NewGameRequestDTO>): Mono<GameDTO> {
        return request
            .flatMap {
                gameRepository.save(
                    Game(
                        (1..6)
                            .map { Random.nextInt(0, charPool.size) }
                            .map(charPool::get)
                            .joinToString(""),
                        listOf(Player(it.playerName)),
                        GameStatus.READY
                    )
                )
            }
            .map { game ->
                GameDTO(
                    game.code,
                    game.players.map { PlayerDTO(it.name) },
                    game.status
                )
            }
            .doOnNext { gameEventManager.createNewPublisherForGame(it.code) }
    }

    fun joinGame(request: Mono<JoinGameRequestDTO>): Mono<JoinGameResponseDTO> {
        return request
            .flatMap {
                Mono.zip(
                    Mono.just(Player(it.playerName)),
                    gameRepository.findGameByCode(it.gameCode)
                        .switchIfEmpty(Mono.error(IllegalArgumentException("Game does not exist!")))
                )
            }
            .flatMap { playerGameTuple ->
                val game = playerGameTuple.t2
                val player = playerGameTuple.t1
                if (game.status == READY) {
                    game.players = game.players.plusElement(player)
                    Mono.zip(
                        Mono.just(player),
                        gameRepository.save(game)
                    ).doOnNext {
                        gameEventManager.publishEvent(
                            GameEventDTO(
                                EventType.PLAYER_JOINED,
                                it.t1.name,
                                it.t1.name,
                                it.t2.code
                            )
                        )
                    }.map { JoinGameResponseDTO(true, "", it.t2.code) }
                } else {
                    Mono.just(JoinGameResponseDTO(false, "Cannot join a game which has already started!", ""))
                }
            }
    }

    fun getPlayersForGame(gameCode: String): Flux<PlayerDTO> {
        return gameRepository.findGameByCode(gameCode)
            .flatMapMany { Flux.fromIterable(it.players) }
            .map { PlayerDTO(it.name) }
    }

    fun quitGame(request: Mono<QuitGameRequestDTO>): Mono<GameDTO> {
        return request
            .doOnNext {
                gameEventManager.publishEvent(
                    GameEventDTO(
                        EventType.PLAYER_LEFT,
                        it.playerName,
                        it.playerName,
                        it.gameCode
                    )
                )
            }
            .flatMap {
                val player = Player(it.playerName)
                Mono.zip(Mono.just(player), gameRepository.findGameByCode(it.gameCode))
            }
            .flatMap { playerGameTuple ->
                val player = playerGameTuple.t1
                val game = playerGameTuple.t2
                game.players = game.players.minusElement(player)
                if (game.players.isEmpty())
                    gameRepository.delete(game).map { GameDTO("REMOVED", emptyList(), GameStatus.FINISHED) }
                else
                    gameRepository.save(game).map { savedGame ->
                        GameDTO(
                            savedGame.code,
                            savedGame.players.map { PlayerDTO(it.name) },
                            savedGame.status
                        )
                    }
            }
    }

    fun startGame(request: Mono<StartGameRequestDTO>): Mono<GameDTO> {
        return request
            .doOnNext {
                gameEventManager.publishEvent(GameEventDTO(GAME_STARTED, "", "", it.gameCode))
            }
            .flatMap {
                gameRepository.findGameByCode(it.gameCode)
            }
            .flatMap { game ->
                game.status = STARTED
                gameRepository.save(game)
            }
            .map { savedGame ->
                GameDTO(
                    savedGame.code,
                    savedGame.players.map { PlayerDTO(it.name) },
                    savedGame.status
                )
            }
    }

    fun finishGame(request: Mono<FinishGameRequestDTO>): Mono<GameDTO> {
        return request
            .doOnNext {
                gameEventManager.publishEvent(GameEventDTO(GAME_FINISHED, "", "", it.gameCode))
            }
            .flatMap {
                gameRepository.findGameByCode(it.gameCode)
            }
            .flatMap { game ->
                game.status = FINISHED
                gameRepository.save(game)
            }
            .map { savedGame ->
                GameDTO(
                    savedGame.code,
                    savedGame.players.map { PlayerDTO(it.name) },
                    savedGame.status
                )
            }
    }
}
