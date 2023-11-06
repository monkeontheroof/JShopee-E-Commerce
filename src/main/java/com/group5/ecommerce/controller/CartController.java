package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.CartService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.UserService;
import com.group5.ecommerce.utils.CartUtil;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.DecimalFormat;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/cart")
    public String getCart(Model model){
        Cart cart = cartService.getCartByUserId(SecurityUtil.getPrincipal().get().getId());
        int cartCount = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("cart", cart);
        model.addAttribute("formatter", formatter);
        model.addAttribute("cartCount", cartCount);
        return "cart";
    }

    @PostMapping("/addItemToCart")
    public String addItemToCart(@RequestParam("id") Long productId,
                                @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity) {

        Product product = productService.getProductById(productId).get();
        Long userId = SecurityUtil.getPrincipal().get().getId();
        cartService.addItemToCart(product, quantity, userId);
        return "redirect:/cart";
    }

    @PostMapping(value = "/update-cart", params = "action=update")
    public String updateCart(@RequestParam("quantity") int quantity,
                             @RequestParam("productId") Long productId,
                             Model model){

        Cart cart = cartService.updateItemInCart(productId, quantity, SecurityUtil.getPrincipal().get().getId());

        model.addAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping(value = "/update-cart", params = "action=delete")
    public String cartItemRemove(@RequestParam("productId") long productId, Model model) {
        Product product = productService.getProductById(productId).get();
        cartService.deleteItemFromCart(product, SecurityUtil.getPrincipal().get().getId());
        return "redirect:/cart";
    }


    @GetMapping("/check-out")
    public String checkout(Model model) {
        model.addAttribute("total", CartUtil.cart.stream().mapToDouble(Product::getPrice).sum());
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "checkout";
    }
}
