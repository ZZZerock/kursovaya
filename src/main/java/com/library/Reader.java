package com.library;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "readers")
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String passportSeries;
    private String passportNumber;
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // ДОБАВЬТЕ ЭТО

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Конструктор
    public Reader() {
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassportSeries() { return passportSeries; }
    public void setPassportSeries(String passportSeries) { this.passportSeries = passportSeries; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // ДОБАВЬТЕ ГЕТТЕР И СЕТТЕР ДЛЯ createdAt
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Удобный метод для получения полного имени
    public String getFullName() {
        return firstName + " " + lastName;
    }
}