package home.dj.kotlinwebsite.persistence.repo

import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.Player
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface GameRepository : ReactiveMongoRepository<Game, String> {
    fun findGameByCode(gameCode: String): Mono<Game>
    fun findGameByPlayersContains(player: Player)
}