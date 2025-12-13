package com.library;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookrentals")
public class BookRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private String status; // ACTIVE, RETURNED, OVERDUE

    // Конструкторы
    public BookRental() {
        this.rentalDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public BookRental(Book book, Reader reader, int rentalDays) {
        this.book = book;
        this.reader = reader;
        this.rentalDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(rentalDays);
        this.status = "ACTIVE";
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }

    public LocalDate getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDate rentalDate) { this.rentalDate = rentalDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Удобный метод для проверки просрочки
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !"RETURNED".equals(status);
    }
}