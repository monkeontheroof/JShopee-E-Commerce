package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.ReviewService;
import com.group5.ecommerce.utils.CartUtil;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public String getShop(Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "shop"; //shop.html will be rendered here.
    }

    @GetMapping("/category/{id}")
    public String shopByCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getProductsByCategoryId(id));
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "shop"; //shop.html will be rendered here.
    }

    @GetMapping("/viewProduct/{id}")
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

        return "clients/detail";
    }

    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam("reviewId") Long reviewId,
                               @RequestParam("productId") Long productId) {
        reviewService.remove(reviewId);

        // Sau khi xóa đánh giá, bạn có thể thực hiện các xử lý khác hoặc chuyển hướng đến trang khác
        return "redirect:/shop/viewProduct/" + productId;
    }
}
