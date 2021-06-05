package home.dj.kotlinwebsite.model

import home.dj.kotlinwebsite.persistence.document.GameStatus

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
    val lengthOfRounds: Int
)

data class PlayerDTO(
    val name: String
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