package com.group5.ecommerce.controller;

import com.group5.ecommerce.service.impl.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.group5.ecommerce.controller.HomeController.getUserId;

@Controller
public class CustomErrorController implements ErrorController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/error")
    public String handleError(Model model) {
        getUserId(model, cartService);
        return "error/404";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
