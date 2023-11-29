package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.CartService;
import com.group5.ecommerce.service.OrderService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;

@Controller
public class CartController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/cart")
    public String getCart(Model model){
        HomeController.getUserId(model, cartService);
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("formatter", formatter);
        return "clients/shopping-cart";
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
                             @RequestParam("voucherCode") String voucherCode,
                             Model model,
                             HttpServletRequest request){
        Cart cart = cartService.updateItemInCart(productId, quantity, SecurityUtil.getPrincipal().get().getId(), voucherCode);
        model.addAttribute("cart", cart);

        String referer = request.getHeader("Referer");
        if(referer == null || referer.isEmpty())
            return "redirect:/cart";

        return "redirect:" + referer;
    }

    @PostMapping(value = "/update-cart", params = "action=delete")
    public String cartItemRemove(@RequestParam("productId") long productId, Model model) {
        Product product = productService.getProductById(productId).get();
        cartService.deleteItemFromCart(product, SecurityUtil.getPrincipal().get().getId());
        return "redirect:/cart";
    }

    @GetMapping("/check-out")
    public String checkout(Model model) {
        Cart cart = cartService.getCartByUserId(SecurityUtil.getPrincipal().get().getId());
        int cartCount = cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum();
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("cart", cart);
        model.addAttribute("formatter", formatter);
        return "clients/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam("cart") Long cartId,
                             @RequestParam("billingAddress") String billingAddress,
                             @RequestParam("paymentMethod") String  paymentMethod,
                             Model model){
        try {
            Cart cart = cartService.findById(cartId);
            User user = SecurityUtil.getPrincipal().get();
            orderService.createOrderFromCart(cart, user, billingAddress, paymentMethod);
            model.addAttribute("cart", cart);
            model.addAttribute("review", new Review());
            return "clients/order-placed";
        }
        catch (Exception e){
            return "redirect:/cart";
        }
    }
}
