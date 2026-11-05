package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordleGame {

    public static final int MAX_STEPS = 6;

    private final String answer;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final List<String> guesses = new ArrayList<>();
    private final List<String> hints = new ArrayList<>();
    private final Set<String> suggested = new HashSet<>();
    private int stepsLeft = MAX_STEPS;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary, log, dictionary.getRandomWord());
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = answer;
        log.println("Загадано слово: " + answer);
    }

    public String getAnswer() {
        return answer;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public List<String> getGuesses() {
        return Collections.unmodifiableList(guesses);
    }

    public List<String> getHints() {
        return Collections.unmodifiableList(hints);
    }

    public boolean isWon() {
        return !guesses.isEmpty() && guesses.get(guesses.size() - 1).equals(answer);
    }

    public boolean isLost() {
        return stepsLeft == 0 && !isWon();
    }

    public boolean isFinished() {
        return isWon() || isLost();
    }

    public String makeMove(String input)
            throws InvalidWordException, WordNotFoundInDictionaryException {
        if (isFinished()) {
            throw new IllegalStateException("Игра уже завершена");
        }
        String word = WordleDictionary.normalize(input);
        if (!WordleDictionary.isValidFormat(word)) {
            throw new InvalidWordException("Нужно ввести слово из "
                    + WordleDictionary.WORD_LENGHT + " русских букв");
        }
        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionaryException(word);
        }

        String hint = computeHint(word, answer);

        stepsLeft--;
        guesses.add(word);
        hints.add(hint);
        log.println("Ход: " + word + " -> " + hint + ", осталось попыток: " + stepsLeft);

        if (stepsLeft < 0) {
            throw new IllegalStateException("Количество попыток стало отрицательным");
        }
        return hint;
    }

    static String computeHint(String guess, String answer) {
        int n = answer.length();
        char[] result = new char[n];
        Arrays.fill(result, '-');
        Map<Character, Integer> free = new HashMap<>();

        for (int i = 0; i < n; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = '+';
            } else {
                free.merge(answer.charAt(i), 1, Integer::sum);
            }
        }
        for (int i = 0; i < n; i++) {
            if (result[i] == '+') {
                continue;
            }
            char c = guess.charAt(i);
            int count = free.getOrDefault(c, 0);
            if (count > 0) {
                result[i] = '^';
                free.put(c, count - 1);
            }
        }
        return new String(result);
    }

    public String suggestWord() {
        if (isFinished()) {
            throw new IllegalStateException("Игра уже завершена");
        }

        String fresh = null;
        String anyFit = null;

        for (String candidate : dictionary.getWords()) {
            if (guesses.contains(candidate)) {
                continue;
            }
            if (!fitsAllHints(candidate)) {
                continue;
            }
            if (anyFit == null) {
                anyFit = candidate;
            }
            if (!suggested.contains(candidate)) {
                fresh = candidate;
                break;
            }
        }

        if (anyFit == null) {
            throw new IllegalStateException("Не найдено ни одного слова, подходящего к подсказкам");
        }
        if (fresh == null) {
            suggested.clear();
            fresh = anyFit;
        }

        suggested.add(fresh);
        log.println("Подсказка компьютера: " + fresh);
        return fresh;
    }

    private boolean fitsAllHints(String candidate) {
        for (int i = 0; i < guesses.size(); i++) {
            if (!computeHint(guesses.get(i), candidate).equals(hints.get(i))) {
                return false;
            }
        }
        return true;
    }
}