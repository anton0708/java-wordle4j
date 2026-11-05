package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private WordleDictionaryLoader loader;

    @BeforeEach
    void setUp() {
        // По заданию лог в тестах пишем в консоль, а не в файл
        loader = new WordleDictionaryLoader(new PrintWriter(System.out, true));
    }

    // Создаёт временный файл словаря в UTF-8 и возвращает путь к нему
    private String writeDictionaryFile(List<String> lines) throws IOException {
        File file = File.createTempFile("wordle-test-dict", ".txt");
        file.deleteOnExit();
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        return file.getPath();
    }

    @Test
    void loadShouldKeepNormalizedFiveLetterWordsWithoutDuplicates() throws Exception {
        String path = writeDictionaryFile(List.of(
                "герой", "ГЕРОЙ",   // дубль после нормализации
                "ТЁТКА",            // ё -> е, регистр
                "кот", "ля-ля"));   // не подходят

        WordleDictionary dictionary = loader.load(path);

        assertEquals(List.of("герой", "тетка"), dictionary.getWords());
    }

    @Test
    void loadMissingFileShouldThrowDictionaryFileException() {
        assertThrows(DictionaryFileException.class, () -> loader.load("no-such-file-12345.txt"));
    }

    @Test
    void loadFileWithoutSuitableWordsShouldThrowEmptyDictionaryException() throws Exception {
        String path = writeDictionaryFile(List.of("кот", "собака"));

        assertThrows(EmptyDictionaryException.class, () -> loader.load(path));
    }
}
