package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.*;
import com.group5.ecommerce.utils.CartUtil;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.group5.ecommerce.controller.HomeController.getUserId;

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

    @Autowired
    private StoreService storeService;

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
    @GetMapping("/products")
    public String getProductsByCategory(Model model){
        List<Product> products = productService.getAllProduct();
        DecimalFormat formatter = new DecimalFormat("#,###");
        getUserId(model, cartService);
        model.addAttribute("products", products);
        model.addAttribute("formatter", formatter);
        return "clients/products";
    }

    //get all products by category name
    @GetMapping("/{categoryName}/products")
    public String getProductsByCategoryName(Model model,
                                            @PathVariable("categoryName") String categoryName){
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        DecimalFormat formatter = new DecimalFormat("#,###");
        getUserId(model, cartService);
        model.addAttribute("products", products);
        model.addAttribute("formatter", formatter);
        return "clients/products";
    }

    @GetMapping("/viewProduct/{id}")
    public String viewProduct(Model model, @PathVariable("id") long id) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        Product product = productService.getProductById(id).get();
        List<ProductImage> productImages = product.getImages();
        productImages.sort(Comparator.comparing(ProductImage::getId));
        SecurityUtil.getPrincipal().ifPresent(principal -> {
            Long userId = principal.getId();
            Cart cart = cartService.getCartByUserId(userId);
            int cartCount = cartService.countCartItems(cart);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("store", storeService.getStoreByUserId(userId));
            model.addAttribute("cart", cart);
            if(userId.equals(product.getStore().getUser().getId())){
                model.addAttribute("canDelete", true);
            }
        });
        model.addAttribute("product", product);
        model.addAttribute("productImages", productImages);
        model.addAttribute("dateTimeFormatter", dateTimeFormatter);
        model.addAttribute("decimalFormatter", decimalFormat);
        model.addAttribute("userReviews", product.getReviews());

        return "clients/detail";
    }

    @PostMapping("/submitReview")
    public String submitReview(@Valid Review review,
                               @RequestParam("productId") Long productId,
                               HttpServletRequest request){
        Optional<CustomUserDetail> authentication = SecurityUtil.getPrincipal();
        authentication.ifPresent(userDetail -> reviewService.save(review, productId, userDetail.getId()));

        String referer = request.getHeader("Referer");
        if(referer == null || referer.isEmpty())
            return "redirect:/shop/viewProduct/" + productId;

        return "redirect:" + referer;
    }

    @GetMapping("/order-placed")
    public String orderPlaced(Model model) {
        model.addAttribute("review", new Review());
        return "clients/order-placed";
    }

    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam("reviewId") Long reviewId,
                               @RequestParam("productId") Long productId) {
        reviewService.remove(reviewId);

        // Sau khi xóa đánh giá, bạn có thể thực hiện các xử lý khác hoặc chuyển hướng đến trang khác
        return "redirect:/shop/viewProduct/" + productId;
    }
}
