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

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

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

    @GetMapping("/shop/viewProduct/{id}")
    public String viewProduct(Model model, @PathVariable("id") long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Product product = productService.getProductById(id).get();

        SecurityUtil.getPrincipal().ifPresent(principal -> {
            Long userId = principal.getId();
            if(userId.equals(product.getStore().getUser().getId()))
                model.addAttribute("canDelete", true);
        });
        model.addAttribute("product", product);
        model.addAttribute("dateTimeFormatter", formatter);
        model.addAttribute("userReviews", product.getReviews());
        model.addAttribute("cartCount", CartUtil.cart.size());

        return "viewProduct";
    }

    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam("reviewId") Long reviewId,
                               @RequestParam("productId") Long productId) {
        reviewService.remove(reviewId);

        // Sau khi xóa đánh giá, bạn có thể thực hiện các xử lý khác hoặc chuyển hướng đến trang khác
        return "redirect:/shop/viewProduct/" + productId;
    }
}
