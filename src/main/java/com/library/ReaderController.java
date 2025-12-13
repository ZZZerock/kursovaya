package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/readers")
public class ReaderController {

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private UserRepository userRepository;

    // Список всех читателей
    @GetMapping
    public String listReaders(Model model) {
        List<Reader> readers = readerRepository.findAll();
        model.addAttribute("readers", readers);
        return "readers";
    }

    // Форма регистрации нового читателя
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("reader", new Reader());
        return "register-reader";
    }

    // Сохранение нового читателя
    @PostMapping("/register")
    public String registerReader(@ModelAttribute Reader reader,
                                 @RequestParam String login,
                                 @RequestParam String password) {
        // Создаем пользователя
        User user = new User();
        user.setLogin(login);
        user.setPassword(password); // В реальности нужно хэшировать
        user.setRole("READER");
        userRepository.save(user);

        // Связываем читателя с пользователем
        reader.setUser(user);
        readerRepository.save(reader);

        return "redirect:/readers";
    }
}