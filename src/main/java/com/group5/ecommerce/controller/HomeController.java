package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.CustomUserDetail;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.Review;
import com.group5.ecommerce.model.User;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String getHome(Model model) {
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "clients/home";
    }
}
