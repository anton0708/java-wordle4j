package ru.yandex.practicum;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;


/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String filename) throws DictionaryFileException, EmptyDictionaryException {
        Set<String> result = new LinkedHashSet<>();
        int total = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                total++;

                String word = WordleDictionary.normalize(line);

                if (WordleDictionary.isValidFormat(word)) {
                    result.add(word);
                }
            }
        } catch (IOException e) {
            throw new DictionaryFileException("Не удалось прочитать словарь: " + filename, e);
        }
        log.println("Прочитано строк: " + total + ", подходящих слов: " + result.size());

        if (result.isEmpty()) {
            throw new EmptyDictionaryException("В словаре нет слов из " + WordleDictionary.WORD_LENGHT + "букв");
        }

        return new WordleDictionary(result);
    }


}
