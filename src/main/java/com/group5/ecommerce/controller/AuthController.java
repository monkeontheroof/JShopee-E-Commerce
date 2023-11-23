package com.group5.ecommerce.controller;

import com.group5.ecommerce.config.JwtUtils;
import com.group5.ecommerce.model.LoginRequest;
import com.group5.ecommerce.model.RegistrationForm;
import com.group5.ecommerce.service.CustomUserDetailService;
import com.group5.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
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
