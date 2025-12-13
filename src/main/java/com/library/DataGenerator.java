package com.library;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataGenerator {

    private final Faker faker;

    public DataGenerator() {
        this.faker = new Faker(new Locale("ru"));
    }

    public String randomFirstName() {
        return faker.name().firstName();
    }

    public String randomLastName() {
        return faker.name().lastName();
    }

    public String randomPassportSeries() {
        return faker.number().digits(4);
    }

    public String randomPassportNumber() {
        return faker.number().digits(6);
    }

    public String randomPhone() {
        // Преобразуем формат телефона под наш
        String phone = faker.phoneNumber().cellPhone();
        // Убираем скобки и дефисы, оставляем только цифры
        String digits = phone.replaceAll("[^0-9]", "");
        // Форматируем как +7-9XX-XXX-XX-XX
        if (digits.length() >= 11) {
            return String.format("+7-%s-%s-%s-%s",
                    digits.substring(1, 4),
                    digits.substring(4, 7),
                    digits.substring(7, 9),
                    digits.substring(9, Math.min(11, digits.length())));
        }
        return "+7-999-123-45-67";
    }

    // Дополнительные методы для тестовых данных
    public String randomBookTitle() {
        return faker.book().title();
    }

    public String randomBookAuthor() {
        return faker.book().author();
    }

    public String randomISBN() {
        return faker.code().isbn10();
    }

    public String randomGenre() {
        String[] genres = {"Роман", "Фантастика", "Детектив", "Фэнтези", "Научная литература",
                "Поэзия", "Биография", "Исторический", "Учебник", "Приключения"};
        return genres[faker.random().nextInt(genres.length)];
    }
}