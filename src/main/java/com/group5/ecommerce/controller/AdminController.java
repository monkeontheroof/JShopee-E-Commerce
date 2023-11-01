package com.group5.ecommerce.controller;

import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService  productService;

    @GetMapping
    public String adminHome(){
    return "adminHome";}

}
