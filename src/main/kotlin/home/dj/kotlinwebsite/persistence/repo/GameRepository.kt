package home.dj.kotlinwebsite.persistence.repo

import home.dj.kotlinwebsite.persistence.document.Game
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface GameRepository : ReactiveMongoRepository<Game, String> {
}