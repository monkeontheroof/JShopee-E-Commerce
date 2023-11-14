package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.RegistrationForm;
import com.group5.ecommerce.model.Role;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.repository.RoleRepository;
import com.group5.ecommerce.repository.UserRepository;
import com.group5.ecommerce.service.UserService;
import com.group5.ecommerce.utils.CartUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new RegistrationForm());
        return "clients/sign-in";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("user") RegistrationForm registerUser, HttpServletRequest request) throws ServletException {
        userService.register(registerUser);
        request.login(registerUser.getEmail(), registerUser.getPassword());
        return "redirect:/home";
    }
}
