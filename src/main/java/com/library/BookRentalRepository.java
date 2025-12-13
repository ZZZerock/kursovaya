package com.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRentalRepository extends JpaRepository<BookRental, Long> {
    List<BookRental> findByReaderId(Long readerId);
    List<BookRental> findByBookId(Long bookId);
    List<BookRental> findByStatus(String status);
    List<BookRental> findByReaderUserLogin(String userLogin);

    List<BookRental> findByStatusAndReturnDateIsNull(String status);
    List<BookRental> findByStatusAndDueDateBeforeAndReturnDateIsNull(String status, java.time.LocalDate date);

    // НОВЫЙ МЕТОД для проверки активных аренд конкретной книги
    List<BookRental> findByReaderIdAndBookIdAndReturnDateIsNull(Long readerId, Long bookId);
    List<BookRental> findByReaderIdAndReturnDateIsNull(Long readerId);

    // НОВЫЙ МЕТОД для активных аренд читателя
    List<BookRental> findByReaderIdAndStatusAndReturnDateIsNull(Long readerId, String status);
}