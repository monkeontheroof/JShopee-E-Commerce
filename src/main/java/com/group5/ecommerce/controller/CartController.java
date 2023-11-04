package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.Cart;
import com.group5.ecommerce.model.CartItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.utils.CartUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;

    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable("id") Long id, HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        if(cart == null){
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        CartItem cartItem = new CartItem();
        cartItem.setProduct(productService.getProductById(id).orElse(null));
        cartItem.setQuantity(1);

        cart.getCartItems().add(cartItem);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String getCart(Model model, HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        if(cart == null){
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        if(cart.getCartItems() != null){
            Double total = cart.getCartItems().stream().mapToDouble(item -> item.getProduct().getPrice()).sum();
            int cartCount = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("total", total);
        }
        model.addAttribute("cart", cart);
        return "cart";
    }

    @GetMapping("/cart/removeItem/{id}")
    public String cartItemRemove(@PathVariable("id") Long id, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if(cart != null){
            cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(id));
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("total", CartUtil.cart.stream().mapToDouble(Product::getPrice).sum());
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "checkout";
    }
}
