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
    val players: Collection<PlayerDTO>,
    val status: GameStatus,
    val numberOfRounds: Int,
    val lengthOfRounds: Int,
    val actualRound: Int
)

data class PlayerDTO(
    val name: String,
    val score: Int = 0
)

data class NewGameRequestDTO(
    val playerName: String
)

data class QuitGameRequestDTO(
    val gameCode: String,
    val playerName: String
)

data class StartGameRequestDTO(
    val gameCode: String,
    val numberOfRounds: Int,
    val lengthOfRounds: Int,
    val playerName: String
)

data class FinishGameRequestDTO(
    val gameCode: String
)

data class JoinGameRequestDTO(
    val gameCode: String,
    val playerName: String
)

data class JoinGameResponseDTO(
    val success: Boolean,
    val message: String,
    val gameCode: String
)