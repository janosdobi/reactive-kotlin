package home.dj.kotlinwebsite.model

data class AuthRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String
)

data class GameDTO(
    val code: String,
    val players: Collection<PlayerDTO>
)

data class PlayerDTO(
    val uid: String,
    val name: String
)

data class NewGameRequestDTO (
    val playerName: String
)