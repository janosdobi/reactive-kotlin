package home.dj.kotlinwebsite.repository

import home.dj.kotlinwebsite.persistence.document.Game
import home.dj.kotlinwebsite.persistence.document.Player
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GameRepository : ReactiveMongoRepository<Game, String> {

    fun findGameByCode(gameCode: String): Mono<Game>

    @Query(value="{\"players.name\": ?0}", sort="{createdAt: -1}")
    fun findGameByPlayersContaining(playerName: String): Flux<Game>
}