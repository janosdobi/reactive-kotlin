package home.dj.kotlinwebsite.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

abstract class EntityBase {
    @Id
    @JsonProperty("id")
    lateinit var id: String

    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var createdAt: LocalDateTime = LocalDateTime.now()
}

@Document("games")
data class Game @JsonCreator constructor(
    @JsonProperty("code") var code: String,
    @JsonProperty("players") var players: Collection<Player>,
    @JsonProperty("status") var status: GameStatus,
    @JsonProperty("numberOfRounds") var numberOfRounds: Int,
    @JsonProperty("lengthOfRounds") var lengthOfRounds: Int,
    @JsonProperty("actualRound") var actualRound: Int = 1,
) : Serializable, EntityBase()

enum class GameStatus {
    READY,
    STARTED,
    FINISHED
}

data class Player @JsonCreator constructor(
    @JsonProperty("name") var name: String,
    @JsonProperty("score") var score: Int = 0
) : Serializable, EntityBase()