package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void setUp() {
        PrintWriter log = new PrintWriter(System.out, true);
        dictionary = new WordleDictionary(List.of(
                "герой", "гонец", "ребер", "трава", "мочка", "аббат", "абзац"));
        game = new WordleGame(dictionary, log, "герой");
    }

    @Test
    void hintShouldMarkExactAndMisplacedLetters() throws Exception {
        assertEquals("+^-^-", game.makeMove("гонец"));   // пример из задания
    }

    @Test
    void hintShouldNotMarkRepeatedLetterMoreTimesThanInAnswer() {
        assertEquals("^+---", WordleGame.computeHint("ребер", "герой"));
    }

    @Test
    void incorrectWordShouldThrowAndNotSpendStep() {
        assertThrows(InvalidWordException.class, () -> game.makeMove("кот"));
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeMove("абхаз"));
        assertEquals(WordleGame.MAX_STEPS, game.getStepsLeft());
    }

    @Test
    void gameShouldBeWonByAnswerAndLostAfterSixWrongWords() throws Exception {
        game.makeMove("герой");
        assertTrue(game.isWon());
        assertTrue(game.isFinished());

        WordleGame lostGame = new WordleGame(dictionary, new PrintWriter(System.out, true), "герой");
        for (String word : List.of("гонец", "ребер", "трава", "мочка", "аббат", "абзац")) {
            lostGame.makeMove(word);
        }
        assertTrue(lostGame.isLost());
        assertThrows(IllegalStateException.class, () -> lostGame.makeMove("герой"));
    }

    @Test
    void suggestedWordShouldFitPreviousHintsAndNotRepeatGuesses() throws Exception {
        game.makeMove("гонец");   // подсказка +^-^-

        String suggestion = game.suggestWord();

        assertEquals("+^-^-", WordleGame.computeHint("гонец", suggestion));
        assertFalse(game.getGuesses().contains(suggestion));
    }
}
