package bot.service
import bot.utils.UtilsKt.getRandomLineFromFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HangManGame {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(HangManGame::class.java)
        private const val FILE_PATH = "data/words_HU.txt"
        private const val alphabet = "abcdefghijklmnopqrstuvwxyzöüóőúéáűí"
        const val MAX_INCORRECT_GUESSES = 5
        var GAME_IN_PROGRESS = false
    }

    private lateinit var secretWord: String
    private lateinit var shownWord: HangManWord

    private val guessedLetters : MutableSet<Char> = mutableSetOf()
    private var incorrectGuesses = 0

    fun newGame(): String {

        secretWord = getRandomLineFromFile(FILE_PATH, true)
        LOGGER.info("The word is: $secretWord")
        shownWord = HangManWord(secretWord)

        GAME_IN_PROGRESS = true
        return """
            New game started!
            Your word is ${secretWord.length} letters long, good luck!
            $shownWord
        """.trimIndent()
    }

    fun guessLetter(char: Char): String {

        if (!alphabet.contains(char)) {
            return "That's not a valid character! Valid characters are the single letters of the hungarian alphabet!"
        }

        if (secretWord.contains(char)) {
            shownWord[char] = true

            if (shownWord.guessedLetterCount() == secretWord.length) {
                GAME_IN_PROGRESS = false
                guessedLetters.clear()
                incorrectGuesses = 0
                return "You win! The word was indeed $secretWord!"
            }
        } else {
            if (!guessedLetters.contains(char) && !secretWord.contains(char)) {
                incorrectGuesses++
            }

            if (incorrectGuesses >= MAX_INCORRECT_GUESSES) {
                GAME_IN_PROGRESS = false
                guessedLetters.clear()
                incorrectGuesses = 0
                return "You lose! The word was $secretWord!"
            }
        }

        guessedLetters.add(char)

        return """
            $shownWord
            Error count: $incorrectGuesses
            Guessed letters: ${guessedLetters.joinToString(", ")}
            Try guessing another letter!
        """.trimIndent()
    }

    class HangManWord(word: String) {

        private val letters: List<HangManLetter> = word.map { HangManLetter(it, false) }

        fun guessedLetterCount() = letters.count { it.show }

        operator fun set(letter: Char, visible: Boolean) {
            letters
                .filter { it.letter == letter }
                .forEach { it.show = visible }
        }

        override fun toString(): String {
            return letters
                .map { if (it.show) it.letter else "-" }
                .joinToString(" ")
        }

        data class HangManLetter (val letter: Char, var show: Boolean){}
    }
}