package home.dj.kotlinwebsite.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

@Document("persistent-object")
data class PersistentObject @JsonCreator constructor(
    @Id
    @JsonProperty("id") var id: String?,
    @Version
    @JsonProperty("version") var version: Int = 0,
    @JsonProperty("name") var name: String?,
    @JsonProperty("type") var type: String?,
    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) : Serializable
