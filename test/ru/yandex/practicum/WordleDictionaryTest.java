package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary(List.of("герой", "гонец", "трава"));
    }

    @Test
    void normalizeShouldTrimLowerCaseAndReplaceYo() {
        assertEquals("елка", WordleDictionary.normalize("  ЁЛКА "));
    }

    @Test
    void isValidFormatShouldAcceptOnlyFiveRussianLetters() {
        assertTrue(WordleDictionary.isValidFormat("герой"));
        assertFalse(WordleDictionary.isValidFormat("кот"));     // короткое
        assertFalse(WordleDictionary.isValidFormat("ля-ля"));   // дефис
        assertFalse(WordleDictionary.isValidFormat("hello"));   // латиница
    }

    @Test
    void containsShouldFindOnlyExistingWords() {
        assertTrue(dictionary.contains("герой"));
        assertFalse(dictionary.contains("мочка"));
    }
}
