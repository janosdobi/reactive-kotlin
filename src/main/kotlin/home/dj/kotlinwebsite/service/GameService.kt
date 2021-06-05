package home.dj.kotlinwebsite.service

import home.dj.kotlinwebsite.model.*
import home.dj.kotlinwebsite.model.Game
import home.dj.kotlinwebsite.model.GameStatus.*
import home.dj.kotlinwebsite.model.Player
import home.dj.kotlinwebsite.repository.GameRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
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
                        READY,
                        0,
                        0
                    )
                )
            }
            .map { game ->
                GameDTO(
                    game.code,
                    game.players.map { PlayerDTO(it.name) },
                    game.status,
                    game.numberOfRounds,
                    game.lengthOfRounds,
                    game.actualRound
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
                            PlayerJoinedEvent(
                                PlayerDTO(it.t1.name),
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
                    PlayerLeftEvent(
                        PlayerDTO(it.playerName),
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
                    gameRepository.delete(game).map { GameDTO("REMOVED", emptyList(), FINISHED, 0, 0, 0) }
                else
                    gameRepository.save(game).map { savedGame ->
                        GameDTO(
                            savedGame.code,
                            savedGame.players.map { PlayerDTO(it.name) },
                            savedGame.status,
                            savedGame.numberOfRounds,
                            savedGame.lengthOfRounds,
                            savedGame.actualRound
                        )
                    }
            }
    }

    fun startGame(request: Mono<StartGameRequestDTO>): Mono<GameDTO> {
        return request
            .flatMap {
                Mono.zip(
                    gameRepository.findGameByCode(it.gameCode),
                    Mono.just(it)
                )
            }
            .flatMap { gameRequestTuple ->
                val game = gameRequestTuple.t1
                val requestDTO = gameRequestTuple.t2
                game.status = STARTED
                game.numberOfRounds = requestDTO.numberOfRounds
                game.lengthOfRounds = requestDTO.lengthOfRounds
                gameRepository.save(game)
            }
            .doOnNext { savedGame ->
                gameEventManager.publishEvent(
                    GameStartedEvent(
                        GameDTO(
                            savedGame.code,
                            savedGame.players.map { PlayerDTO(it.name) },
                            savedGame.status,
                            savedGame.numberOfRounds,
                            savedGame.lengthOfRounds,
                            savedGame.actualRound
                        ),
                        savedGame.code
                    )
                )
            }
            .delayElement(Duration.ofMillis(2000))
            .doOnSuccess {
                gameEventManager.publishEvent(
                    RoundStartedEvent(
                        1,
                        it.lengthOfRounds,
                        it.code
                    )
                )
            }
            .delayUntil {
                Mono.delay(Duration.ofSeconds(it.lengthOfRounds.toLong()))
            }
            .doOnSuccess {
                gameEventManager.publishEvent(
                    RoundFinishedEvent(
                        1,
                        it.code
                    )
                )
            }
            .flatMap {
                it.actualRound += 1
                gameRepository.save(it)
            }
            .map { savedGame ->
                GameDTO(
                    savedGame.code,
                    savedGame.players.map { PlayerDTO(it.name) },
                    savedGame.status,
                    savedGame.numberOfRounds,
                    savedGame.lengthOfRounds,
                    savedGame.actualRound
                )
            }
    }

    fun finishGame(request: Mono<FinishGameRequestDTO>): Mono<GameDTO> {
        return request
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
                    savedGame.status,
                    savedGame.numberOfRounds,
                    savedGame.lengthOfRounds,
                    savedGame.actualRound
                )
            }
            .doOnNext {
                gameEventManager.publishEvent(GameFinishedEvent(it, it.code))
            }
    }
}
