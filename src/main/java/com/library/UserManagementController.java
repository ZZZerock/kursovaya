package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;
    @Autowired  // ← ДОБАВЬТЕ ЭТУ СТРОЧКУ
    private ReaderRepository readerRepository;
    @Autowired
    private DataGenerator dataGenerator;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-management";
    }

    @PostMapping("/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam String newRole) {
        User user = userRepository.findById(id).orElseThrow();

        // Сохраняем старую роль
        String oldRole = user.getRole();

        // Обновляем роль
        user.setRole(newRole);
        userRepository.save(user);

        // Если новая роль - READER и у пользователя еще нет Reader
        if ("READER".equals(newRole) && !"READER".equals(oldRole)) {
            if (readerRepository.findByUserId(user.getId()).isEmpty()) {
                Reader reader = new Reader();
                reader.setFirstName(dataGenerator.randomFirstName());
                reader.setLastName(dataGenerator.randomLastName());
                reader.setPassportSeries(dataGenerator.randomPassportSeries());
                reader.setPassportNumber(dataGenerator.randomPassportNumber());
                reader.setPhone(dataGenerator.randomPhone());
                reader.setUser(user);
                readerRepository.save(reader);
            }
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}