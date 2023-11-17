package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.Cart;
import com.group5.ecommerce.model.CustomUserDetail;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.Review;
import com.group5.ecommerce.service.CartService;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.ReviewService;
import com.group5.ecommerce.utils.CartUtil;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        Product product = productService.getProductById(id).get();

        SecurityUtil.getPrincipal().ifPresent(principal -> {
            Long userId = principal.getId();
            if(userId != null && userId.equals(product.getStore().getUser().getId())){
                Cart cart = cartService.getCartByUserId(userId);
                int cartCount = cartService.countCartItems(cart);
                model.addAttribute("cartCount", cartCount);
                model.addAttribute("cart", cart);
                model.addAttribute("canDelete", true);
            }
        });
        model.addAttribute("product", product);
        model.addAttribute("dateTimeFormatter", dateTimeFormatter);
        model.addAttribute("decimalFormatter", decimalFormat);
        model.addAttribute("userReviews", product.getReviews());

        return "clients/detail";
    }

    @PostMapping("/submitReview")
    public String submitReview(@RequestParam("comment") String comment,
                               @RequestParam("productId") Long productId,
                               HttpServletRequest request){
        Optional<CustomUserDetail> authentication = SecurityUtil.getPrincipal();
        authentication.ifPresent(userDetail -> reviewService.save(comment, productId, userDetail.getId()));

        String referer = request.getHeader("Referer");
        if(referer == null || referer.isEmpty())
            return "redirect:/shop/viewProduct/" + productId;

        return "redirect:" + referer;
    }

    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam("reviewId") Long reviewId,
                               @RequestParam("productId") Long productId) {
        reviewService.remove(reviewId);

        // Sau khi xóa đánh giá, bạn có thể thực hiện các xử lý khác hoặc chuyển hướng đến trang khác
        return "redirect:/shop/viewProduct/" + productId;
    }
}
