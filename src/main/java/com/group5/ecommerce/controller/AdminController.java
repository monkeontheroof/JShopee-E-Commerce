package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.Order;
import com.group5.ecommerce.model.OrderItem;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.service.*;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    @Autowired
    private SaleService saleService;

    @GetMapping
    public String adminHome(Model model, Principal principal){
        if (principal == null){
            return "redirect:/logout";
        }
        SecurityUtil.getPrincipal().ifPresent(userDetail -> {
            User user = userService.getUserById(userDetail.getId());
            model.addAttribute("user", user);
        });

        return "admin/home";
    }

    @GetMapping("/customers")
    public String getAllCustomers(Model model,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size){
        SecurityUtil.getPrincipal().ifPresent(userDetail -> {
            User user = userService.getUserById(userDetail.getId());
            Page<User> customerPages = userService.getAllUsers(PageRequest.of(page - 1, size));
            model.addAttribute("user", user);
            model.addAttribute("customerPages", customerPages);
            model.addAttribute("pageSize", size);
        });
        return "admin/customers";
    }

    @GetMapping("/stores")
    public String getAllStores(Model model,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size){
        SecurityUtil.getPrincipal().ifPresent(userDetail -> {
            User user = userService.getUserById(userDetail.getId());
            Page<UserStore> storePages = storeService.getAllStores(PageRequest.of(page - 1, size));
            model.addAttribute("user", user);
            model.addAttribute("storePages", storePages);
            model.addAttribute("pageSize", size);
        });
        return "admin/stores";
    }

    @GetMapping("/sales")
    public String getAllRevenue(Model model,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate startDate,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate endDate){
        SecurityUtil.getPrincipal().ifPresent(userDetail -> {
            User user = userService.getUserById(userDetail.getId());
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            Page<Order> orderPages;

            if(startDate != null && endDate != null){
                orderPages = saleService.getOrdersBetween(startDate, endDate, PageRequest.of(page - 1, size));
            }
            else {
                orderPages = saleService.getAllOrderPages(PageRequest.of(page - 1, size));
            }

            model.addAttribute("user", user);
            model.addAttribute("decimalFormat", decimalFormat);
            model.addAttribute("totalOrderRevenue", saleService.calculateTotalOrderRevenue(orderPages.getContent()));
            model.addAttribute("orderPages", orderPages);
            model.addAttribute("pageSize", size);
        });
        return "admin/sales";
    }

    @GetMapping("/lockStore/{storeId}")
    public String lockAccount(Model model,
                              @PathVariable("storeId") Long storeId){
        try{
            storeService.lockStore(storeId);
            return "redirect:/admin/customers";
        } catch (Exception e){
            model.addAttribute("error", true);
            return "admin/stores";
        }
    }

    @GetMapping("/unlockStore/{storeId}")
    public String unlockAccount(Model model,
                                @PathVariable("storeId") Long storeId){
        try{
            storeService.unlockStore(storeId);
            return "redirect:/admin/customers";
        } catch (Exception e){
            model.addAttribute("error", true);
            return "admin/stores";
        }
    }

    @GetMapping("/lockAccount/{email}")
    public String lockAccount(Model model,
                              @PathVariable("email") String email){
        try{
            userService.lockAccount(email);
            return "redirect:/admin/customers";
        } catch (Exception e){
            model.addAttribute("error", true);
            return "admin/customers";
        }
    }

    @GetMapping("/unlockAccount/{email}")
    public String unlockAccount(Model model,
                              @PathVariable("email") String email){
        try{
            userService.unlockAccount(email);
            return "redirect:/admin/customers";
        } catch (Exception e){
            model.addAttribute("error", true);
            return "admin/customers";
        }
    }
}
