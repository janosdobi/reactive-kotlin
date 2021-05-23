package home.dj.kotlinwebsite.persistence.repo

import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.Player
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface GameRepository : ReactiveMongoRepository<Game, String> {
    fun findGameByCode(gameCode: String): Mono<Game>
    @Query("{\"players.name\": ?0}")
    fun findGameByPlayersContaining(playerName: String): Mono<Game>
}