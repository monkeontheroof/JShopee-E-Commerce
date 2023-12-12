package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.UserService;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService  productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminHome(Model model){
        SecurityUtil.getPrincipal().ifPresent(userDetail -> {
            User user = userService.getUserById(userDetail.getId());
            model.addAttribute("user", user);
        });
        return "admin/home";
    }

    @GetMapping("/customers")
    public String getAllCustomers(Model model,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size){
        SecurityUtil.getPrincipal().ifPresent(userDetail -> {
            User user = userService.getUserById(userDetail.getId());
            Page<User> customerPages = userService.getAllUsers(PageRequest.of(page - 1, size));
            model.addAttribute("user", user);
            model.addAttribute("customerPages", customerPages);
            model.addAttribute("pageSize", size);
        });
        return "admin/customers";
    }

    @GetMapping("/lockAccount/{email}")
    public String lockAccount(Model model,
                              @PathVariable("email") String email){
        try{
            userService.lockAccount(email);
            return "redirect:/admin/customers";
        } catch (Exception e){
            model.addAttribute("error", true);
            return "admin/customers";
        }
    }

    @GetMapping("/unlockAccount/{email}")
    public String unlockAccount(Model model,
                              @PathVariable("email") String email){
        try{
            userService.unlockAccount(email);
            return "redirect:/admin/customers";
        } catch (Exception e){
            model.addAttribute("error", true);
            return "admin/customers";
        }
    }
}
