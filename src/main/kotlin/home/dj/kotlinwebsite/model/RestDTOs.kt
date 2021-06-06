package home.dj.kotlinwebsite.model

data class AuthRequestDTO(
    val username: String,
    val password: String
)

data class AuthResponseDTO(
    val token: String
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
    val playerName: String,
    val questions: Collection<QuestionDTO>
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

data class NextRoundRequestDTO(
    val gameCode: String
)