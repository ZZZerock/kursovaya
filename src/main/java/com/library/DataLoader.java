package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        // Добавляем тестовые книги если база пустая
        if (bookRepository.count() == 0) {
            Book book1 = new Book();
            book1.setTitle("Война и мир");
            book1.setAuthor("Лев Толстой");
            book1.setIsbn("978-5-699-12014-7");
            book1.setPublicationYear(1869);
            book1.setGenre("Роман");
            book1.setTotalCount(5);
            book1.setAvailableCount(3);

            Book book2 = new Book();
            book2.setTitle("Преступление и наказание");
            book2.setAuthor("Федор Достоевский");
            book2.setIsbn("978-5-389-08235-2");
            book2.setPublicationYear(1866);
            book2.setGenre("Роман");
            book2.setTotalCount(3);
            book2.setAvailableCount(1);

            Book book3 = new Book();
            book3.setTitle("Мастер и Маргарита");
            book3.setAuthor("Михаил Булгаков");
            book3.setIsbn("978-5-17-090635-8");
            book3.setPublicationYear(1967);
            book3.setGenre("Фантастика");
            book3.setTotalCount(7);
            book3.setAvailableCount(7);

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);

            System.out.println("Тестовые книги добавлены!");
        }
    }
}