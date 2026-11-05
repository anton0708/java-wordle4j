package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    @Test
    void shouldPrintWinMessageWhenPlayerGuessesWord() {
        WordleDictionary dictionary = new WordleDictionary(List.of("герой", "гонец"));
        WordleGame game = new WordleGame(dictionary, new PrintWriter(System.out, true), "герой");

        // игрок вводит правильное слово
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream("герой\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        Wordle.play(game);

        System.setIn(oldIn);
        System.setOut(oldOut);

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Победа!"));
    }

    @Test
    void shouldPrintHintWhenPlayerEntersWrongWord() {
        WordleDictionary dictionary = new WordleDictionary(List.of("герой", "гонец"));
        WordleGame game = new WordleGame(dictionary, new PrintWriter(System.out, true), "герой");

        // сначала неправильное слово, потом правильное, чтобы игра закончилась
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream("гонец\nгерой\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        Wordle.play(game);

        System.setIn(oldIn);
        System.setOut(oldOut);

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("+^-^-"));
    }
}