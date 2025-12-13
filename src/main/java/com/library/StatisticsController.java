package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRentalRepository bookRentalRepository;

    @GetMapping
    public String showStatistics(Model model) {
        System.out.println("=== НАЧАЛО СТАТИСТИКИ ===");

        // 1. Получаем все книги ОДИН РАЗ
        List<Book> allBooks = bookRepository.findAll();
        System.out.println("Всего книг найдено: " + allBooks.size());

        // 2. Статистика по книгам
        long totalBooks = allBooks.size();
        long totalAvailableBooks = allBooks.stream()
                .mapToInt(Book::getAvailableCount)
                .sum();
        long totalRentedBooks = allBooks.stream()
                .mapToInt(book -> book.getTotalCount() - book.getAvailableCount())
                .sum();

        // 3. Статистика по пользователям
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long adminUsers = allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long librarianUsers = allUsers.stream().filter(u -> "LIBRARIAN".equals(u.getRole())).count();
        long readerUsers = allUsers.stream().filter(u -> "READER".equals(u.getRole())).count();

        // 4. Статистика по читателям
        long totalReaders = readerRepository.count();

        // 5. Статистика по арендам
        List<BookRental> allRentals = bookRentalRepository.findAll();
        long totalRentals = allRentals.size();
        long activeRentals = allRentals.stream().filter(r -> "ACTIVE".equals(r.getStatus())).count();

        long overdueRentals = allRentals.stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()) &&
                        r.getDueDate() != null &&
                        r.getDueDate().isBefore(LocalDate.now()))
                .count();

        long returnedRentals = allRentals.stream().filter(r -> "RETURNED".equals(r.getStatus())).count();

        // 6. СТАТИСТИКА ПО ЖАНРАМ - ГЛАВНОЕ!
        Map<String, Integer> genreStats = new LinkedHashMap<>();

        // Сначала собираем все жанры
        for (Book book : allBooks) {
            String genre = book.getGenre();
            if (genre == null || genre.trim().isEmpty()) {
                genre = "Без жанра";
            } else {
                genre = genre.trim();
            }

            genreStats.put(genre, genreStats.getOrDefault(genre, 0) + 1);
        }

        // СОРТИРУЕМ по убыванию количества книг
        Map<String, Integer> sortedGenreStats = genreStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // сохраняем порядок
                ));

        System.out.println("Отсортированная статистика жанров:");
        sortedGenreStats.forEach((genre, count) -> {
            System.out.println("  " + genre + ": " + count);
        });

        // 7. Подготовка данных для графиков в JavaScript
        // Создаем списки для передачи в JavaScript
        List<String> genreNames = new ArrayList<>(sortedGenreStats.keySet());
        List<Integer> genreCounts = new ArrayList<>(sortedGenreStats.values());

        // 8. Передаем все в модель
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalAvailableBooks", totalAvailableBooks);
        model.addAttribute("totalRentedBooks", totalRentedBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("librarianUsers", librarianUsers);
        model.addAttribute("readerUsers", readerUsers);
        model.addAttribute("totalReaders", totalReaders);
        model.addAttribute("totalRentals", totalRentals);
        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("overdueRentals", overdueRentals);
        model.addAttribute("returnedRentals", returnedRentals);

        // Передаем отсортированную статистику
        model.addAttribute("genreStats", sortedGenreStats);

        // Дополнительно передаем как списки для JavaScript
        model.addAttribute("genreNames", genreNames);
        model.addAttribute("genreCounts", genreCounts);

        System.out.println("=== КОНЕЦ СТАТИСТИКИ ===");
        return "statistics";
    }
}