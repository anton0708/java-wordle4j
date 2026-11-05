package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {

        try (PrintWriter log = new PrintWriter(
                new FileWriter(LOG_FILE, StandardCharsets.UTF_8), true)) {

            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = loader.load(DICTIONARY_FILE);

                WordleGame game = new WordleGame(dictionary, log);
                play(game);

            } catch (Exception e) {

                e.printStackTrace(log);
                System.out.println("Произошла ошибка, подробности смотрите в файле " + LOG_FILE);
            }

        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    static void play(WordleGame game) {

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("Угадайте слово из " + WordleDictionary.WORD_LENGHT
                + " букв. Попыток: " + WordleGame.MAX_STEPS);
        System.out.println("Подсказка: + буква на месте, ^ буква есть, но в другом месте, - буквы нет");

        while (!game.isFinished()) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.isBlank()) {
                System.out.println("Подсказка: " + game.suggestWord());
                continue;
            }

            try {
                String hint = game.makeMove(input);
                System.out.println("> " + hint);

            } catch (WordleGameException e) {
                System.out.println(e.getMessage());
            }
        }

        if (game.isWon()) {
            System.out.println("Победа!");
        } else {
            System.out.println("Попытки закончились.");
        }
        System.out.println("Загаданное слово: " + game.getAnswer());
    }
}
