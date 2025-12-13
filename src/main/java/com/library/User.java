package com.library;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String login;

    private String password;
    private String role; // ADMIN, LIBRARIAN, READER

    private LocalDateTime createdAt;

    // конструктор
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ДОБАВЬТЕ ЭТОТ СЕТТЕР ↓
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}