package ru.yandex.practicum;

import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    public static int WORD_LENGHT = 5;

    private List<String> words;

    private final Set<String> wordSet;

    private final Random random = new Random();

    public WordleDictionary(Collection<String> words) {
        this.words = new ArrayList<>(words);
        this.wordSet = new HashSet<>(words);
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word
                .trim()
                .toLowerCase()
                .replace("ё", "е");
    }

    public static boolean isValidFormat(String word) {
        return word != null && word.matches("[а-я]{" + WORD_LENGHT + "}");
    }

    public boolean contains(String word) {
        return wordSet.contains(word);
    }

    public int size() {
        return words.size();
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }

        return words.get(random.nextInt(words.size()));
    }

}
