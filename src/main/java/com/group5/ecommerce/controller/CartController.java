package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.Cart;
import com.group5.ecommerce.model.CartItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.User;
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
import java.text.DecimalFormat;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @PostMapping("/addItemToCart")
    public String addItemToCart(@RequestParam("id") Long productId,
                                @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                                Model model,
                                HttpServletRequest request) {

        Product product = productService.getProductById(productId).orElse(null);
        Long userId = SecurityUtil.getPrincipal().getId();
        Cart cart = cartService.addItemToCart(product, quantity, userId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String getCart(Model model){
        User user = userService.getUserById(SecurityUtil.getPrincipal().getId());
        Cart cart = user.getCart();
        int cartCount = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("cart", cart);
        model.addAttribute("formatter", formatter);
        model.addAttribute("cartCount", cartCount);
        return "cart";
    }

    @PutMapping("/updateQuantity/{cartItemId}")
    @ResponseBody
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam int newQuantity, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            CartItem cartItem = cart.getCartItems().get(cartItemId.intValue());

            if (cartItem != null) {
                cartItem.setQuantity(newQuantity);

                return ResponseEntity.ok("Quantity updated successfully");
            }
        }
        return ResponseEntity.badRequest().body("Failed to update quantity");
    }


    @GetMapping("/cart/removeItem/{id}")
    public String cartItemRemove(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id).get();
        cartService.deleteItemFromCart(product, SecurityUtil.getPrincipal().getId());
        return "redirect:/cart";
    }


    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("total", CartUtil.cart.stream().mapToDouble(Product::getPrice).sum());
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "checkout";
    }
}
