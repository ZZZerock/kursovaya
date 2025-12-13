package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRentalRepository bookRentalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataGenerator dataGenerator;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🚀 Загрузка начальных данных...");
            createTestUsers();
            createTestBooks();
            createReadersForAllUsers();
            createTestRentals();
            System.out.println("✅ Начальные данные загружены!");
        } else {
            System.out.println("ℹ️  Данные уже есть в БД, загрузка не требуется");
            System.out.println("📊 Статистика:");
            System.out.println("   Пользователей: " + userRepository.count());
            System.out.println("   Книг: " + bookRepository.count());
        }
    }

    private void createTestUsers() {
        if (userRepository.count() == 0) {
            // Администратор
            User admin = new User();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setCreatedAt(LocalDateTime.now()); // Используйте LocalDateTime
            userRepository.save(admin);

            // Библиотекарь
            User librarian = new User();
            librarian.setLogin("librarian");
            librarian.setPassword(passwordEncoder.encode("lib123"));
            librarian.setRole("LIBRARIAN");
            librarian.setCreatedAt(LocalDateTime.now());
            userRepository.save(librarian);

            // 5 читателей с разными логинами
            String[] readerLogins = {"ivanov", "petrov", "sidorov", "smirnov", "kozlov"};
            for (String login : readerLogins) {
                User readerUser = new User();
                readerUser.setLogin(login);
                readerUser.setPassword(passwordEncoder.encode("password123"));
                readerUser.setRole("READER");
                // Уберите минус, если хотите дату создания в прошлом
                readerUser.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
                userRepository.save(readerUser);
            }

            System.out.println("✅ Создано 7 пользователей: 1 админ, 1 библиотекарь, 5 читателей");
        }
    }

    private void createTestBooks() {
        if (bookRepository.count() == 0) {
            System.out.println("📚 Создание книг...");

            // 1. Классические книги (10 шт)
            List<Book> classicBooks = List.of(
                    createBook("Война и мир", "Лев Толстой", "978-5-699-12014-7", 1869, "Роман-эпопея", 3, 5),
                    createBook("Преступление и наказание", "Федор Достоевский", "978-5-389-08235-2", 1866, "Роман", 2, 4),
                    createBook("Мастер и Маргарита", "Михаил Булгаков", "978-5-17-090635-8", 1967, "Фантастика", 1, 3),
                    createBook("Евгений Онегин", "Александр Пушкин", "978-5-17-090645-7", 1833, "Роман в стихах", 4, 6),
                    createBook("Отцы и дети", "Иван Тургенев", "978-5-17-090655-6", 1862, "Роман", 2, 3),
                    createBook("Анна Каренина", "Лев Толстой", "978-5-17-090665-5", 1877, "Роман", 3, 5),
                    createBook("Идиот", "Федор Достоевский", "978-5-17-090675-4", 1869, "Роман", 1, 2),
                    createBook("Мёртвые души", "Николай Гоголь", "978-5-17-090685-3", 1842, "Поэма", 2, 3),
                    createBook("Герой нашего времени", "Михаил Лермонтов", "978-5-17-090695-2", 1840, "Роман", 3, 4),
                    createBook("Обломов", "Иван Гончаров", "978-5-17-090705-8", 1859, "Роман", 2, 3)
            );

            // 2. Современные книги (10 шт)
            List<Book> modernBooks = List.of(
                    createBook("451° по Фаренгейту", "Рэй Брэдбери", "978-5-17-090715-7", 1953, "Антиутопия", 2, 4),
                    createBook("1984", "Джордж Оруэлл", "978-5-17-090725-6", 1949, "Антиутопия", 1, 3),
                    createBook("Убить пересмешника", "Харпер Ли", "978-5-17-090735-5", 1960, "Роман", 3, 5),
                    createBook("Властелин колец", "Дж. Р. Р. Толкин", "978-5-17-090745-4", 1954, "Фэнтези", 4, 7),
                    createBook("Гарри Поттер и философский камень", "Дж. К. Роулинг", "978-5-17-090755-3", 1997, "Фэнтези", 5, 8),
                    createBook("Маленький принц", "Антуан де Сент-Экзюпери", "978-5-17-090765-2", 1943, "Притча", 6, 10),
                    createBook("Шерлок Холмс", "Артур Конан Дойл", "978-5-17-090775-1", 1887, "Детектив", 3, 5),
                    createBook("Алиса в Стране чудес", "Льюис Кэрролл", "978-5-17-090785-0", 1865, "Сказка", 4, 6),
                    createBook("Портрет Дориана Грея", "Оскар Уайльд", "978-5-17-090795-9", 1890, "Роман", 2, 3),
                    createBook("Три товарища", "Эрих Мария Ремарк", "978-5-17-090805-5", 1936, "Роман", 1, 2)
            );

            // 3. Случайные книги (10 шт)
            for (int i = 0; i < 10; i++) {
                Book book = new Book();
                book.setTitle(dataGenerator.randomBookTitle());
                book.setAuthor(dataGenerator.randomBookAuthor());
                book.setIsbn(dataGenerator.randomISBN());
                book.setPublicationYear(1950 + random.nextInt(74)); // 1950-2024
                book.setGenre(dataGenerator.randomGenre());

                int total = 1 + random.nextInt(5); // 1-5 экз.
                int available = random.nextInt(total + 1); // 0-total

                book.setTotalCount(total);
                book.setAvailableCount(available);

                bookRepository.save(book);
            }

            System.out.println("✅ Создано 30 книг: 10 классических, 10 современных, 10 случайных");
        }
    }

    private Book createBook(String title, String author, String isbn, int year, String genre, int available, int total) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublicationYear(year);
        book.setGenre(genre);
        book.setTotalCount(total);
        book.setAvailableCount(available);
        return bookRepository.save(book);
    }

    private void createReadersForAllUsers() {
        // Временное решение до добавления findAllByRole()
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            if ("READER".equals(user.getRole())) {
                if (readerRepository.findByUserId(user.getId()).isEmpty()) {
                    Reader reader = new Reader();
                    reader.setFirstName(dataGenerator.randomFirstName());
                    reader.setLastName(dataGenerator.randomLastName());
                    reader.setPassportSeries(dataGenerator.randomPassportSeries());
                    reader.setPassportNumber(dataGenerator.randomPassportNumber());
                    reader.setPhone(dataGenerator.randomPhone());
                    reader.setUser(user);

                    // Проверьте тип поля в Reader.java:
                    // reader.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));

                    readerRepository.save(reader);
                }
            }
        }
        System.out.println("✅ Созданы читатели для всех пользователей с ролью READER");
    }

    private void createTestRentals() {
        if (bookRentalRepository.count() == 0) {
            System.out.println("📅 Создание аренд с разными статусами...");

            List<Reader> readers = readerRepository.findAll();
            List<Book> books = bookRepository.findAll();

            if (!readers.isEmpty() && !books.isEmpty()) {
                // Типы аренд: 1. Активные, 2. Скоро срок, 3. Просроченные, 4. Возвращенные

                // 1. Активные аренды (нормальные сроки)
                createRentalsForReader(readers.get(0), books.subList(0, 3),
                        LocalDate.now().minusDays(5), LocalDate.now().plusDays(10), "ACTIVE");

                // 2. Аренды со скорым сроком (< 3 дней)
                createRentalsForReader(readers.get(1), books.subList(3, 6),
                        LocalDate.now().minusDays(10), LocalDate.now().plusDays(2), "ACTIVE");

                // 3. Просроченные аренды
                createRentalsForReader(readers.get(2), books.subList(6, 9),
                        LocalDate.now().minusDays(15), LocalDate.now().minusDays(2), "ACTIVE");

                // 4. Возвращенные аренды (в прошлом)
                createRentalsForReader(readers.get(3), books.subList(9, 12),
                        LocalDate.now().minusDays(20), LocalDate.now().minusDays(5), "RETURNED");

                // 5. Еще активные для другого читателя
                createRentalsForReader(readers.get(4), books.subList(12, 15),
                        LocalDate.now().minusDays(3), LocalDate.now().plusDays(14), "ACTIVE");

                System.out.println("✅ Созданы разнообразные аренды с разными статусами");
            }
        }
    }

    private void createRentalsForReader(Reader reader, List<Book> books,
                                        LocalDate startDate, LocalDate endDate, String status) {
        for (Book book : books) {
            if (book.getAvailableCount() > 0 || status.equals("RETURNED")) {
                BookRental rental = new BookRental();
                rental.setBook(book);
                rental.setReader(reader);
                rental.setRentalDate(startDate);
                rental.setDueDate(endDate);
                rental.setStatus(status);

                if (status.equals("RETURNED")) {
                    rental.setReturnDate(endDate.plusDays(1));
                    // Для возвращенных книг увеличиваем available_count
                    book.setAvailableCount(book.getAvailableCount() + 1);
                } else {
                    // Для активных аренд уменьшаем available_count
                    book.setAvailableCount(book.getAvailableCount() - 1);
                }

                bookRepository.save(book);
                bookRentalRepository.save(rental);

                String statusText = status.equals("ACTIVE") ?
                        (endDate.isBefore(LocalDate.now()) ? "ПРОСРОЧЕНА" :
                                endDate.isBefore(LocalDate.now().plusDays(3)) ? "СКОРО СРОК" : "АКТИВНА") :
                        "ВОЗВРАЩЕНА";

                System.out.println("   📖 " + book.getTitle() +
                        " → " + reader.getFirstName() + " " + reader.getLastName() +
                        " [" + statusText + ", до: " + endDate + "]");
            }
        }
    }
}