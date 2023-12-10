package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.RegistrationForm;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.pattern.command.AddUserCommand;
import com.group5.ecommerce.pattern.command.Command;
import com.group5.ecommerce.service.UserService;
import com.group5.ecommerce.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller //TODO: Singleton
public class AuthController {
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new RegistrationForm());
        return "clients/sign-in";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("user") RegistrationForm registerUser,
                               HttpServletRequest request,
                               Model model) throws ServletException {
        User user = userService.getUserByEmail(registerUser.getEmail());
        if(user != null && user.getEmail() != null){
            model.addAttribute("emailExists", true);
            return "clients/sign-in";
        }
        if(!registerUser.getPassword().trim().equals(registerUser.getConfirmPassword().trim())){
            model.addAttribute("passwordMismatch", true);
            return "clients/sign-in";
        }

        Command command = new AddUserCommand(registerUser); //TODO: Command
        command.execute();
        request.login(registerUser.getEmail(), registerUser.getPassword());
        return "redirect:/home";
    }
}
