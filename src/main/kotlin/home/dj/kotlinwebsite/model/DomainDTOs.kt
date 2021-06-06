package home.dj.kotlinwebsite.model

data class GameDTO(
    val code: String,
    val players: Collection<PlayerDTO>,
    val status: GameStatus,
    val numberOfRounds: Int,
    val lengthOfRounds: Int,
    val actualRound: Int,
    val questions: Collection<QuestionDTO> = emptyList()
)

data class PlayerDTO(
    val name: String,
    val score: Int = 0
)

data class QuestionDTO(
    val category: String,
    val correct_answer: String,
    val difficulty: String,
    val incorrect_answers: Collection<String>,
    val question: String,
    val type: String
)