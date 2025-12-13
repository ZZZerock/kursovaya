package com.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Только эти страницы доступны без авторизации
                        .requestMatchers("/login", "/register", "/about", "/css/**", "/js/**", "/images/**").permitAll()

                        // Остальные требуют авторизации
                        .requestMatchers("/books").hasAnyRole("READER", "ADMIN", "LIBRARIAN")
                        // Взять/вернуть книгу могут ТОЛЬКО читатели
                        .requestMatchers("/books/*/borrow", "/books/*/return").hasRole("READER")
                        .requestMatchers("/books/new").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/statistics").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/rentals/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/my-books").hasRole("READER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}