package com.library;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // Если не авторизован - редирект на логин
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Только для авторизованных:
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isLibrarian = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_LIBRARIAN"));
        boolean isReader = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_READER"));

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isLibrarian", isLibrarian);
        model.addAttribute("isReader", isReader);

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}