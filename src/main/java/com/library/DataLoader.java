package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Добавляем тестовые книги если база пустая
//        if (bookRepository.count() == 0) {
//            Book book1 = new Book();
//            book1.setTitle("Война и мир");
//            book1.setAuthor("Лев Толстой");
//            book1.setIsbn("978-5-699-12014-7");
//            book1.setPublicationYear(1869);
//            book1.setGenre("Роман");
//            book1.setTotalCount(5);
//            book1.setAvailableCount(3);
//
//            Book book2 = new Book();
//            book2.setTitle("Преступление и наказание");
//            book2.setAuthor("Федор Достоевский");
//            book2.setIsbn("978-5-389-08235-2");
//            book2.setPublicationYear(1866);
//            book2.setGenre("Роман");
//            book2.setTotalCount(3);
//            book2.setAvailableCount(1);
//
//            Book book3 = new Book();
//            book3.setTitle("Мастер и Маргарита");
//            book3.setAuthor("Михаил Булгаков");
//            book3.setIsbn("978-5-17-090635-8");
//            book3.setPublicationYear(1967);
//            book3.setGenre("Фантастика");
//            book3.setTotalCount(7);
//            book3.setAvailableCount(7);
//
//            bookRepository.save(book1);
//            bookRepository.save(book2);
//            bookRepository.save(book3);
//
//            System.out.println("Тестовые книги добавлены!");
//        }
//    }
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создаем тестовых пользователей если база пустая
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setLogin("admin.test");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");

            User librarian = new User();
            librarian.setLogin("librarian.test");
            librarian.setPassword(passwordEncoder.encode("lib123"));
            librarian.setRole("LIBRARIAN");

            User reader = new User();
            reader.setLogin("reader123");
            reader.setPassword(passwordEncoder.encode("reader123"));
            reader.setRole("READER");

            userRepository.save(admin);
            userRepository.save(librarian);
            userRepository.save(reader);

            System.out.println("Тестовые пользователи созданы!");
        }
    }
}