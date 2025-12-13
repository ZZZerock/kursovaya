package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRentalRepository bookRentalRepository;

    // Показывает все книги
    @GetMapping
    public String listBooks(Model model, Authentication authentication) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);

        // Подсчет доступных книг
        long availableBooksCount = books.stream()
                .filter(book -> book.getAvailableCount() > 0)
                .count();
        model.addAttribute("availableBooksCount", availableBooksCount);

        // Получение уникальных жанров
        List<String> genres = books.stream()
                .map(Book::getGenre)
                .filter(genre -> genre != null && !genre.trim().isEmpty())
                .map(genre -> genre.trim())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("genres", genres);

        // Инициализируем переменные ролей
        boolean isAdmin = false;
        boolean isLibrarian = false;
        boolean isReader = false;

        if (authentication != null && authentication.isAuthenticated()) {
            // Получаем все роли пользователя
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Проверяем роли
            isAdmin = authorities.contains("ROLE_ADMIN");
            isLibrarian = authorities.contains("ROLE_LIBRARIAN");
            isReader = authorities.contains("ROLE_READER");

            // Получаем информацию об арендах для текущего читателя
            if (isReader) {
                String username = authentication.getName();
                readerRepository.findByUserLogin(username).ifPresent(reader -> {
                    List<BookRental> activeRentals = bookRentalRepository
                            .findByReaderIdAndStatusAndReturnDateIsNull(reader.getId(), "ACTIVE");

                    Map<Long, BookRental> userBookRentals = new HashMap<>();
                    for (BookRental rental : activeRentals) {
                        userBookRentals.put(rental.getBook().getId(), rental);
                    }
                    model.addAttribute("userBookRentals", userBookRentals);
                });
            }
        }

        // Передаем роли в модель
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isLibrarian", isLibrarian);
        model.addAttribute("isReader", isReader);

        return "books";
    }

    // Форма для добавления новой книги
    @GetMapping("/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "new-book";
    }

    // Сохраняет новую книгу
    @PostMapping
    public String saveBook(@ModelAttribute Book book, Model model, BindingResult bindingResult) {
        System.out.println("=== СОХРАНЕНИЕ КНИГИ ===");
        System.out.println("Название: " + book.getTitle());
        System.out.println("Автор: " + book.getAuthor());
        System.out.println("Год: " + book.getPublicationYear());
        System.out.println("ISBN: " + book.getIsbn());
        try {
            // Валидация на сервере
            List<String> errors = validateBook(book);

            if (!errors.isEmpty()) {
                model.addAttribute("error", String.join("<br>", errors));
                model.addAttribute("book", book); // возвращаем введенные данные
                return "new-book";
            }

            // Дополнительные проверки
            if (book.getAvailableCount() > book.getTotalCount()) {
                model.addAttribute("error", "Доступное количество не может быть больше общего");
                model.addAttribute("book", book);
                return "new-book";
            }

            if (book.getPublicationYear() < 1000 || book.getPublicationYear() > LocalDate.now().getYear() + 1) {
                model.addAttribute("error", "Год издания должен быть от 1000 до " + (LocalDate.now().getYear() + 1));
                model.addAttribute("book", book);
                return "new-book";
            }

            // Проверка ISBN (если указан)
            if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
                String isbn = book.getIsbn().trim();

                // Проверка формата ISBN
                if (!isValidISBN(isbn)) {
                    model.addAttribute("error",
                            "Неверный формат ISBN. Разрешены только цифры и дефисы (не более 20 символов). " +
                                    "Пример: 978-5-699-12014-7 или 1974279820");
                    model.addAttribute("book", book);
                    return "new-book";
                }

                // Проверка уникальности ISBN (если есть другие книги с таким ISBN)
                List<Book> existingBooks = bookRepository.findByIsbn(isbn);
                if (!existingBooks.isEmpty()) {
                    // Проверяем, не эта ли самая книга (при редактировании)
                    boolean isSameBook = existingBooks.stream()
                            .anyMatch(b -> b.getId().equals(book.getId()));

                    if (!isSameBook) {
                        model.addAttribute("error", "Книга с таким ISBN уже существует в каталоге");
                        model.addAttribute("book", book);
                        return "new-book";
                    }
                }
            }

            // Устанавливаем ISBN как null если пустая строка
            if (book.getIsbn() != null && book.getIsbn().trim().isEmpty()) {
                book.setIsbn(null);
            }

            bookRepository.save(book);
            System.out.println("Книга сохранена с ID: " + book.getId());
            return "redirect:/books?success=true";

        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при сохранении: " + e.getMessage());
            model.addAttribute("book", book);
            return "new-book";
        }
    }

    private List<String> validateBook(Book book) {
        List<String> errors = new ArrayList<>();

        // Название
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            errors.add("Название книги обязательно");
        } else if (book.getTitle().trim().length() < 2 || book.getTitle().trim().length() > 200) {
            errors.add("Название должно быть от 2 до 200 символов");
        }

        // Автор
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            errors.add("Автор обязателен");
        } else if (book.getAuthor().trim().length() < 2 || book.getAuthor().trim().length() > 100) {
            errors.add("Имя автора должно быть от 2 до 100 символов");
        }

        // ISBN (если указан)
        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            String isbn = book.getIsbn().trim();
            if (isbn.length() > 20) {
                errors.add("ISBN не может превышать 20 символов");
            }
        }

        // Количество
        if (book.getTotalCount() == null || book.getTotalCount() < 1) {
            errors.add("Общее количество должно быть не менее 1");
        } else if (book.getTotalCount() > 1000) {
            errors.add("Общее количество не может превышать 1000");
        }

        if (book.getAvailableCount() == null || book.getAvailableCount() < 0) {
            errors.add("Доступное количество не может быть отрицательным");
        }

        // Год
        if (book.getPublicationYear() == null) {
            errors.add("Год издания обязателен");
        }

        return errors;
    }

    private boolean isValidISBN(String isbn) {
        // Проверка формата: только цифры и дефисы, не более 20 символов
        if (isbn.length() > 20) return false;

        // Разрешены только цифры и дефисы
        if (!isbn.matches("^[0-9\\-]+$")) return false;

        // Не может начинаться или заканчиваться дефисом
        if (isbn.startsWith("-") || isbn.endsWith("-")) return false;

        // Дефисы не могут идти подряд
        if (isbn.contains("--")) return false;

        return true;
    }

    // Также обнови метод для отображения формы (GET /books/new)

    // Взять книгу - ТОЛЬКО для читателей
    @PostMapping("/{id}/borrow")
    public String borrowBook(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Проверяем, что пользователь - читатель
        boolean isReader = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_READER"));

        if (!isReader) {
            return "redirect:/books?error=not_reader";
        }

        String username = authentication.getName();

        // Находим книгу
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена"));

        // Находим читателя
        Reader reader = readerRepository.findByUserLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("Читатель не найден"));

        // Проверяем, доступна ли книга
        if (book.getAvailableCount() <= 0) {
            return "redirect:/books?error=not_available";
        }

        // Проверяем, не взял ли уже эту книгу пользователь
        boolean alreadyBorrowed = bookRentalRepository
                .findByReaderIdAndBookIdAndReturnDateIsNull(reader.getId(), id)
                .stream()
                .anyMatch(rental -> "ACTIVE".equals(rental.getStatus()));

        if (alreadyBorrowed) {
            return "redirect:/books?error=already_borrowed";
        }

        // Создаем аренду
        BookRental rental = new BookRental();
        rental.setBook(book);
        rental.setReader(reader);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(14)); // 14 дней на возврат
        rental.setStatus("ACTIVE");
        bookRentalRepository.save(rental);

        // Обновляем количество доступных книг
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookRepository.save(book);

        return "redirect:/books?success=borrowed";
    }

    // Вернуть книгу - ТОЛЬКО для читателей
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Проверяем, что пользователь - читатель
        boolean isReader = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_READER"));

        if (!isReader) {
            return "redirect:/books?error=not_reader";
        }

        String username = authentication.getName();

        // Находим читателя
        Reader reader = readerRepository.findByUserLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("Читатель не найден"));

        // Находим активную аренду этой книги
        BookRental rental = bookRentalRepository
                .findByReaderIdAndBookIdAndReturnDateIsNull(reader.getId(), id)
                .stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Аренда не найдена"));

        // Обновляем аренду
        rental.setReturnDate(LocalDate.now());
        rental.setStatus("RETURNED");
        bookRentalRepository.save(rental);

        // Возвращаем книгу в фонд
        Book book = rental.getBook();
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookRepository.save(book);

        return "redirect:/books?success=returned";
    }
}