package com.library;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @PostConstruct
    public void recreateTestUsers() {
        // Удаляем старых пользователей
        userRepository.deleteAll();

        // Создаем новых с правильными паролями
        registerUser("admin.test", "admin123", "ADMIN");
        registerUser("librarian.test", "lib123", "LIBRARIAN");
        registerUser("reader123", "reader123", "READER");

        System.out.println("✅ Тестовые пользователи пересозданы");
    }
    @PostConstruct
    public void checkAndFixUsers() {
        // Пересоздаем пользователей с гарантированно правильными паролями
        if (userRepository.findByLogin("reader123").isEmpty()) {
            System.out.println("🔄 Пересоздаем тестовых пользователей...");

            userRepository.deleteAll();

            User reader = new User();
            reader.setLogin("reader123");
            reader.setPassword(passwordEncoder.encode("reader123")); // точно правильный пароль
            reader.setRole("READER");
            userRepository.save(reader);

            System.out.println("✅ Пользователь reader123 создан с паролем: " + reader.getPassword());
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostConstruct
    public void init() {
        System.out.println("🔑 ТЕСТ ПАРОЛЕЙ:");
        System.out.println("reader123 -> " + passwordEncoder.encode("reader123"));
        System.out.println("admin123 -> " + passwordEncoder.encode("admin123"));
        System.out.println("lib123 -> " + passwordEncoder.encode("lib123"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Ищем пользователя: " + username);

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("✅ ПОЛЬЗОВАТЕЛЬ НАЙДЕН: " + user.getLogin());
        System.out.println("🔐 Пароль в БД: " + user.getPassword());

        // ДЛЯ ДИАГНОСТИКИ - проверяем пароль прямо здесь
        String testPassword = "reader123";
        boolean matches = passwordEncoder.matches(testPassword, user.getPassword());
        System.out.println("🔑 ПРОВЕРКА ПАРОЛЯ: " + testPassword + " -> " + matches);

        if (!matches) {
            System.out.println("❌ ПАРОЛЬ НЕ СОВПАДАЕТ!");
            System.out.println("🔑 Текущий хеш пароля: " + user.getPassword());
            System.out.println("🔑 reader123 должен хешироваться как: " + passwordEncoder.encode("reader123"));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public void registerUser(String login, String password, String role) {
        // Проверяем, нет ли уже пользователя с таким логином
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }
}