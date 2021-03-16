package home.dj.kotlinwebsite.persistence

import home.dj.kotlinwebsite.model.PersistentObject
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PersistentObjectRepository : ReactiveMongoRepository<PersistentObject, String> {

}