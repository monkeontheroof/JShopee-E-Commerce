package com.group5.ecommerce.controller;

import com.group5.ecommerce.dto.ProductDTO;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.utils.CartUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;

    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable("id") Long id){
        CartUtil.cart.add(productService.getProductById(id));
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String getCart(Model model){
        model.addAttribute("cartCount", CartUtil.cart.size());
        model.addAttribute("total", CartUtil.cart.stream().mapToDouble(ProductDTO::getPrice).sum());
        model.addAttribute("cart", CartUtil.cart);
        return "cart";
    }

    @GetMapping("/cart/removeItem/{id}")
    public String cartItemRemove(@PathVariable("id") int id) {
        CartUtil.cart.remove(id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("total", CartUtil.cart.stream().mapToDouble(ProductDTO::getPrice).sum());
        model.addAttribute("cartCount", CartUtil.cart.size());
        return "checkout";
    }
}
