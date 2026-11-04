package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
 * Главный класс игры. Только он работает с консолью (System.out / System.in).
 * Остальные классы про консоль ничего не знают, и их можно тестировать отдельно.
 */
public class Wordle {

    // Имя файла словаря (лежит в корне проекта) и файла лога
    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {

        // ВНЕШНИЙ try-with-resources: открывает лог-файл и гарантированно закроет его в конце.
        // FileWriter с UTF-8 пишет русский текст без "кракозябр".
        // true во втором аргументе PrintWriter включает автосброс буфера после println.
        try (PrintWriter log = new PrintWriter(
                new FileWriter(LOG_FILE, StandardCharsets.UTF_8), true)) {

            // ВНУТРЕННИЙ try: охватывает всю работу игры.
            // Лог уже открыт, поэтому любую неожиданную ошибку можно записать в него.
            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = loader.load(DICTIONARY_FILE);

                WordleGame game = new WordleGame(dictionary, log);
                play(game);

            } catch (Exception e) {
                // Общий перехват: сюда попадают и программные исключения (нет файла словаря,
                // словарь пуст), и любые RuntimeException (баги).
                // Стектрейс уходит в лог, а игроку показываем короткое сообщение.
                e.printStackTrace(log);
                System.out.println("Произошла ошибка, подробности смотрите в файле " + LOG_FILE);
            }

        } catch (IOException e) {
            // Лог создать не удалось, писать ошибки больше некуда, поэтому единственный выход: консоль
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    // Игровой цикл. Вынесен в отдельный метод, чтобы main оставался коротким.
    // static, потому что вызывается из static main и не использует поля объекта.
    private static void play(WordleGame game) {

        // Scanner читает ввод игрока. Кодировку указываем явно, чтобы русские буквы читались правильно.
        // Закрывать Scanner над System.in не нужно: System.in закрывать не следует.
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("Угадайте слово из " + WordleDictionary.WORD_LENGHT
                + " букв. Попыток: " + WordleGame.MAX_STEPS);
        System.out.println("Подсказка: + буква на месте, ^ буква есть, но в другом месте, - буквы нет");

        // Цикл идёт, пока игра не закончена (ни победы, ни проигрыша)
        while (!game.isFinished()) {
            System.out.print("> ");
            String input = scanner.nextLine();

            // Пустая строка. Здесь позже появятся подсказки от компьютера.
            // Пока просто просим ввести слово. Ход не засчитываем и идём на новый круг.
            if (input.isBlank()) {
                System.out.println("Введите слово");
                continue;
            }

            try {
                // Подсказку печатаем на новой строке под словом игрока:
                // в консоли символы одинаковой ширины, поэтому они встанут ровно под буквами
                String hint = game.makeMove(input);
                System.out.println("> " + hint);

            } catch (WordleGameException e) {
                // Игровая ошибка (слово не из словаря, неверный формат).
                // Исключение бросается до уменьшения числа попыток, поэтому ход не засчитан.
                // Показываем игроку понятный текст и продолжаем цикл.
                System.out.println(e.getMessage());
            }
        }

        // Сюда попадаем, когда цикл закончился: победа или проигрыш
        if (game.isWon()) {
            System.out.println("Победа!");
        } else {
            System.out.println("Попытки закончились.");
        }
        System.out.println("Загаданное слово: " + game.getAnswer());
    }
}
