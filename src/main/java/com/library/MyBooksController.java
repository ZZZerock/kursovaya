package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class MyBooksController {

    @Autowired
    private BookRentalRepository bookRentalRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @GetMapping("/my-books")
    public String myBooks(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        readerRepository.findByUserLogin(username).ifPresentOrElse(
                reader -> {
                    // Используй один из методов:
                    List<BookRental> myRentals = bookRentalRepository
                            .findByReaderIdAndReturnDateIsNull(reader.getId());

                    // Или используй @Query метод:
                    // List<BookRental> myRentals = bookRentalRepository
                    //     .findActiveRentalsByReader(reader.getId());

                    model.addAttribute("myRentals", myRentals);
                    model.addAttribute("reader", reader);
                },
                () -> {
                    // Если читатель не найден
                    model.addAttribute("myRentals", List.of());
                }
        );

        return "my-books";
    }
}