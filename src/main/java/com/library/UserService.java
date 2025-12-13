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

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataGenerator dataGenerator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public void registerUser(String login, String password, String role) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);

        // Если пользователь - читатель, создаем для него запись в Readers
        if ("READER".equals(role)) {
            createReaderForUser(user);
        }
    }

    private void createReaderForUser(User user) {
        Reader reader = new Reader();
        reader.setFirstName(dataGenerator.randomFirstName());
        reader.setLastName(dataGenerator.randomLastName());
        reader.setPassportSeries(dataGenerator.randomPassportSeries());
        reader.setPassportNumber(dataGenerator.randomPassportNumber());
        reader.setPhone(dataGenerator.randomPhone());
        reader.setUser(user);

        readerRepository.save(reader);
    }

    public void registerReader(String login, String password, String firstName, String lastName,
                               String passportSeries, String passportNumber, String phone) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("READER");
        userRepository.save(user);

        Reader reader = new Reader();
        reader.setFirstName(firstName);
        reader.setLastName(lastName);
        reader.setPassportSeries(passportSeries);
        reader.setPassportNumber(passportNumber);
        reader.setPhone(phone);
        reader.setUser(user);

        readerRepository.save(reader);
    }
}