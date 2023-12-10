package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.User;
import com.group5.ecommerce.service.UserService;
import com.group5.ecommerce.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // USER SESSIONS
    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "users";  //redirect to users page
    }

    @GetMapping("/users/add")
    public String getAddUser(Model model){
        model.addAttribute("user", new User());
        return "usersAdd"; //redirect to add user page
    }

    //post method for category
    @PostMapping("/users/add")
    public String postAddUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.removeUserById(id);

        return "redirect:/users";  //redirect to categories page
    }

    @GetMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model){
        User user = userService.getUserById(id);

        model.addAttribute("user", user);
        return "usersAdd";

    }
}
