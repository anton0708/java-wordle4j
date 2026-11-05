package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WordleAutoPlayTest {

    private static final String DICTIONARY_FILE = "words_ru.txt";

    private static WordleDictionary dictionary;

    @BeforeAll
    static void loadDictionary() throws Exception {
        assumeTrue(new File(DICTIONARY_FILE).exists(), "Нет файла " + DICTIONARY_FILE);
        dictionary = new WordleDictionaryLoader(new PrintWriter(new StringWriter()))
                .load(DICTIONARY_FILE);
    }

    @Test
    void computerShouldNeverLoseTheAnswerAndAlwaysFinishGame() throws Exception {
        List<String> words = dictionary.getWords();
        PrintWriter quietLog = new PrintWriter(new StringWriter());

        for (int i = 0; i < words.size(); i += 20) {
            WordleGame game = new WordleGame(dictionary, quietLog, words.get(i));
            int moves = 0;

            while (!game.isFinished()) {
                game.makeMove(game.suggestWord());
                moves++;
                assertTrue(moves <= WordleGame.MAX_STEPS, "игра не закончилась за 6 ходов");
            }
        }
    }
}
