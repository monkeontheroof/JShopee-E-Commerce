package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.CartService;
import com.group5.ecommerce.service.OrderService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.StoreService;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.DecimalFormat;
import java.util.List;

import static java.util.Objects.isNull;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private OrderService orderService;

    @GetMapping({"/", "/home"})
    public String getHome(Model model) {
        List<Product> products = productService.getAllProduct();
        DecimalFormat formatter = new DecimalFormat("#,###");
        getUserId(model, cartService);
        model.addAttribute("products", products.subList(0, Math.min(5, products.size())));
        model.addAttribute("formatter", formatter);
        return "clients/home";
    }

    @GetMapping("/register-shop")
    public String getRegisterShop(Model model) {
        model.addAttribute("cart", cartService.getCartByUserId(SecurityUtil.getPrincipal().get().getId()));
        model.addAttribute("agreeTerms", false);
        model.addAttribute("store", new UserStore());
        return "clients/registerShop";
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        User user = SecurityUtil.getPrincipal().orElse(null);
        List<Order> orders = orderService.getOrdersByUserId(user.getId());
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("orders", orders);
        model.addAttribute("formatter", formatter);
        model.addAttribute("cart", cartService.getCartByUserId(user.getId()));
        return "clients/orders";
    }

    @GetMapping("/orders/{id}/details")
    public String getOrderDetails(Model model,
                            @PathVariable("id") Long orderId) {
        User user = SecurityUtil.getPrincipal().orElse(null);
        Order order = orderService.getOrderById(orderId);
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("orderDetails", order.getOrderItems());
        model.addAttribute("cart", cartService.getCartByUserId(user.getId()));
        model.addAttribute("formatter", formatter);
        return "clients/order-details";
    }

    @PostMapping("/register-shop")
    public String postRegisterShop(@ModelAttribute("store") UserStore store,
                                   Model model) {
        User user = SecurityUtil.getPrincipal().orElse(null);
        store.setUser(user);
        storeService.addStore(store);
        return "redirect:/";
    }

    static void getUserId(Model model, CartService cartService) {
        User user = SecurityUtil.getPrincipal().orElse(null);
        if(user != null && user.getId() != null){
            Cart cart = cartService.getCartByUserId(user.getId());
            int cartCount = cartService.countCartItems(cart);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("cart", cart);
        }
    }
}
