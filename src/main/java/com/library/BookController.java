package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // Показывает все книги
    @GetMapping
    public String listBooks(Model model) {
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
    public String saveBook(@ModelAttribute Book book) {
        bookRepository.save(book);
        return "redirect:/books";
    }
}