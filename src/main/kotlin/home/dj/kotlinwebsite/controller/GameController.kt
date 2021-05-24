package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.*
import home.dj.kotlinwebsite.model.EventType.PLAYER_JOINED
import home.dj.kotlinwebsite.model.EventType.PLAYER_LEFT
import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.GameStatus
import home.dj.kotlinwebsite.persistence.document.GameStatus.FINISHED
import home.dj.kotlinwebsite.persistence.document.GameStatus.READY
import home.dj.kotlinwebsite.persistence.document.Player
import home.dj.kotlinwebsite.repository.GameRepository
import home.dj.kotlinwebsite.service.GameEventManager
import home.dj.kotlinwebsite.service.GameService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@RequestMapping("/api")
class GameController(
    private val gameService: GameService
) {

    @PostMapping("v1/new-game", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun newGame(principal: Principal, @RequestBody request: Mono<NewGameRequestDTO>): Mono<GameDTO> {
        return gameService.createNewGame(request)
    }

    @PostMapping("v1/join-game", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun joinGame(principal: Principal, @RequestBody request: Mono<JoinGameRequestDTO>): Mono<JoinGameResponseDTO> {
        return gameService.joinGame(request)
    }

    @GetMapping("v1/players/{gameCode}")
    @ResponseStatus(HttpStatus.OK)
    fun getPlayers(@PathVariable gameCode: String): Flux<PlayerDTO> {
        return gameService.getPlayersForGame(gameCode)
    }

    @PostMapping("v1/quit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun quitGame(@RequestBody request: Mono<QuitGameRequestDTO>): Mono<GameDTO> {
        return gameService.quitGame(request)
    }

    @PostMapping("v1/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startGame(@RequestBody request: Mono<StartGameRequestDTO>): Mono<GameDTO> {
        return gameService.startGame(request)
    }

    @PostMapping("v1/finish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun finishGame(@RequestBody request: Mono<FinishGameRequestDTO>): Mono<GameDTO> {
        return gameService.finishGame(request)
    }
}
