package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.CartService;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ReviewService;
import com.group5.ecommerce.utils.CartUtil;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @GetMapping({"/", "/home"})
    public String getHome(Model model) {
        List<Product> products = productService.getAllProduct();
        DecimalFormat formatter = new DecimalFormat("#,###");
        getUserId(model, cartService);
        model.addAttribute("products", products.subList(0, Math.min(5, products.size())));
        model.addAttribute("formatter", formatter);
        return "clients/home";
    }

    @GetMapping("/404")
    public String get404() {
        return "clients/404";
    }

    static void getUserId(Model model, CartService cartService) {
        User user = SecurityUtil.getPrincipal().orElse(null);
        if(user != null && user.getId() != null){
            Cart cart = cartService.getCartByUserId(user.getId());
            int cartCount = cartService.countCartItems(cart);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("cart", cart);
        }
    }
}
