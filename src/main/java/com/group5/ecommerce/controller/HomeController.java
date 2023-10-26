package com.group5.ecommerce.controller;

import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.utils.CartUtil;
import com.group5.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping({"/", "/home"})
    public String getHome(Model model) {
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "index";
    }

    @GetMapping("/shop")
    public String getShop(Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "shop"; //shop.html will be rendered here.
    }

    @GetMapping("/shop/category/{id}")
    public String shopByCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getProductsByCategoryId(id));
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "shop"; //shop.html will be rendered here.
    }

    @GetMapping("/shop/viewproduct/{id}")
    public String viewProduct(Model model, @PathVariable("id") Long id) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "viewProduct";
    }
}
