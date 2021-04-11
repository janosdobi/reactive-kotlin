package home.dj.kotlinwebsite.persistence

import home.dj.kotlinwebsite.model.Player
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PlayerRepository : ReactiveMongoRepository<Player, String> {

}