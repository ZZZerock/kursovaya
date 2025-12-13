package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/rentals")
public class BookRentalController {

    @Autowired
    private BookRentalRepository bookRentalRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    // Список всех аренд
    @GetMapping
    public String listRentals(Model model) {
        List<BookRental> rentals = bookRentalRepository.findAll();
        model.addAttribute("rentals", rentals);
        return "rentals";
    }

    // Форма для выдачи книги
    @GetMapping("/new")
    public String newRentalForm(Model model) {
        List<Book> books = bookRepository.findAll();
        List<Reader> readers = readerRepository.findAll();

        model.addAttribute("books", books);
        model.addAttribute("readers", readers);
        model.addAttribute("rental", new BookRental());

        return "new-rental";
    }

    // Выдача книги (создание аренды)
    @PostMapping
    public String createRental(@RequestParam Long bookId,
                               @RequestParam Long readerId,
                               @RequestParam(defaultValue = "14") int days) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        Reader reader = readerRepository.findById(readerId).orElseThrow();

        // Проверяем, есть ли доступные экземпляры
        if (book.getAvailableCount() <= 0) {
            // Можно добавить сообщение об ошибке
            return "redirect:/rentals/new?error=no_available";
        }

        // Создаем аренду
        BookRental rental = new BookRental(book, reader, days);
        bookRentalRepository.save(rental);

        // Обновляем количество доступных книг
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookRepository.save(book);

        return "redirect:/rentals";
    }

    // Возврат книги
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id) {
        BookRental rental = bookRentalRepository.findById(id).orElseThrow();

        // Обновляем аренду
        rental.setReturnDate(LocalDate.now());
        rental.setStatus("RETURNED");
        bookRentalRepository.save(rental);

        // Возвращаем книгу в фонд
        Book book = rental.getBook();
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookRepository.save(book);

        return "redirect:/rentals";
    }
}