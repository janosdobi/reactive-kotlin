package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.GameDTO
import home.dj.kotlinwebsite.model.PlayerDTO
import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.repo.GameRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

@RestController
@RequestMapping("/api")
class RestController(
    private val gameRepository: GameRepository
) {

    @GetMapping("v1/new-game")
    fun newGame(principal: Principal): Mono<ResponseEntity<GameDTO>> {
        return gameRepository
            .save(Game(generateGameCode(), emptyList()))
            .map { game -> ResponseEntity.ok(GameDTO(game.code, game.players.map { PlayerDTO(it.name, it.sub) })) }
    }

    private fun generateGameCode() = (1..6)
        .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("");
}
