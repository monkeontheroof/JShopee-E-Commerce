package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.*;
import com.group5.ecommerce.utils.SecurityUtil;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Controller
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private VoucherService voucherService;

//    @GetMapping("/sideMenu")
//    public String getSideMenu(Model model) {
//        Long userId = SecurityUtil.getPrincipal().getId();
//        UserStore userStore = storeService.getStoreByUserId(userId);
//        model.addAttribute("store", userStore);
//        return "fragments/sideMenu";
//    }


    @GetMapping("/store/home")
    public String getHome(Model model) {
        Long userId = SecurityUtil.getPrincipal().get().getId();
        UserStore userStore = storeService.getStoreByUserId(userId);
        List<User> customers = orderService.getCustomersPurchasedFromStore(userStore.getId());
        List<Order> orders = orderService.getAllOrdersByStoreId(userStore.getId());
        List<Product> productsAlmostOut = productService.getProductsByQuantityLessThan(10);
        model.addAttribute("store", userStore);
        model.addAttribute("customerCount", customers.size());
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("productCount", userStore.getProducts().size());
        model.addAttribute("productAlmostOutCount", productsAlmostOut.size());
        return "adminHome";
    }

    @GetMapping("/store/{storeId}/categories")
    public String getCat(Model model, @PathVariable("storeId") Long storeId) {
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId));
        return "categories";
    }

    @GetMapping("/store/{storeId}/categories/add")
    public String getAddCat(@PathVariable("storeId") Long storeId , Model model){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("category", new Category());
        model.addAttribute("storeId", storeId);
        return "categoriesAdd";
    }

    @PostMapping("/store/{storeId}/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category, @PathVariable("storeId") Long storeId) {
        categoryService.addCategory(category, storeId);
        return "redirect:/store/" + storeId + "/categories";
    }

    @GetMapping("/store/{storeId}/categories/delete/{id}")
    public String deleteCat(@PathVariable("id") Long categoryId, @PathVariable("storeId") Long storeId){
        categoryService.removeCategoryById(categoryId);
        return "redirect:/store/" + storeId + "/categories";
    }

    @GetMapping("/store/{storeId}/categories/update/{id}")
    public String updateCat(@PathVariable("id") Long id, Model model, @PathVariable("storeId") Long storeId){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            UserStore userStore = storeService.getStoreById(storeId);
            model.addAttribute("store", userStore);
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        }
        return "404";  //redirect to categories page
    }



    //PRODUCT SESSIONS

    @GetMapping("/store/{storeId}/products")
    public String getProducts(Model model,
                              @PathVariable("storeId") Long storeId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size){
        UserStore userStore = storeService.getStoreById(storeId);
        Page<Product> productsPage = productService.getAllProductByStoreId(storeId, PageRequest.of(page, size));
        model.addAttribute("store", userStore);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("page", productsPage);
        return "products";
    }

    @GetMapping("/store/{storeId}/products/add")
    public String getAddProduct(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId));
        model.addAttribute("isUpdate", false);
        return "productsAdd";
    }

    //post method for category
    @PostMapping("/store/{storeId}/products/add")
    public String postAddProduct(@ModelAttribute("product") Product product,
                                 @RequestParam("productImage") MultipartFile file,
                                 @RequestParam("imgName") String imgName, @PathVariable("storeId") Long storeId) throws IOException {
        productService.addProduct(product, file, imgName, storeId);
        return "redirect:/store/" + storeId + "/products";
    }

    @GetMapping("/store/{storeId}/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, @PathVariable("storeId") Long storeId){
        productService.removeProductById(id);
        return "redirect:/store/" + storeId + "/products";  //redirect to categories page
    }

    @GetMapping("/store/{storeId}/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                Model model,
                                @PathVariable("storeId") Long storeId){

        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("product", productService.getProductById(id).orElse(null));
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId));
        model.addAttribute("isUpdate", true);
        return "productsAdd";
    }



    // CUSTOMER SESSIONS //
    @GetMapping("/store/{storeId}/customers")
    public String getCustomers(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        List<User> customers = orderService.getCustomersPurchasedFromStore(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("customers", customers);
        return "customer";
    }



    // ORDER SESSIONS //
    @GetMapping("/store/{storeId}/orders")
    public String getOrders(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("store", userStore);
        model.addAttribute("formatter", formatter);
        model.addAttribute("orders", orderService.getAllOrdersByStoreId(storeId));
        return "orders";
    }

    @GetMapping("/store/{storeId}/orders/update/{id}")
    public String updateOrder(Model model, @PathVariable("storeId") Long storeId, @PathVariable("id") Long id){
        UserStore userStore = storeService.getStoreById(storeId);
        List<String> statuses = List.of("Pending", "Processing", "Delivering", "Delivered", "Canceled");
        model.addAttribute("store", userStore);
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("statuses", statuses);
        model.addAttribute("isUpdate", true);
        return "ordersAdd";
    }

    @PostMapping("/store/{storeId}/orders/add")
    public String postAddOrder(@ModelAttribute("order") Order order, @PathVariable("storeId") Long storeId){
        orderService.saveOrder(order, storeId);
        return "redirect:/store/" + storeId + "/orders";
    }

    @GetMapping("/store/{storeId}/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, @PathVariable("storeId") Long storeId){
        orderService.deleteOrder(id);
        return "redirect:/store/" + storeId + "/orders";
    }



    // SALES SESSIONS //
    @GetMapping("/store/{storeId}/sales")
    public String getSales(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        return "sales";
    }



    // VOUCHER SESSIONS //
    @GetMapping("/store/{storeId}/vouchers")
    public String getVouchers(Model model, @PathVariable("storeId") Long storeId){
        model.addAttribute("store", storeService.getStoreById(storeId));
        model.addAttribute("vouchers", voucherService.findAllVouchersByStore(storeId));
        return "voucher";
    }
}
