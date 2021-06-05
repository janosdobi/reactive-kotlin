package home.dj.kotlinwebsite.model

sealed interface GameEvent {
    val eventType: String
    val gameCode: String
}

data class PlayerJoinedEvent(
    val player: PlayerDTO,
    override val gameCode: String,
    override val eventType: String = PlayerJoinedEvent::class.simpleName!!
) : GameEvent

data class PlayerLeftEvent(
    val player: PlayerDTO,
    override val gameCode: String,
    override val eventType: String = PlayerLeftEvent::class.simpleName!!
) : GameEvent

data class GameStartedEvent(
    val game: GameDTO,
    override val gameCode: String,
    override val eventType: String = GameStartedEvent::class.simpleName!!
) : GameEvent

data class GameFinishedEvent(
    val game: GameDTO,
    override val gameCode: String,
    override val eventType: String = GameFinishedEvent::class.simpleName!!
) : GameEvent
