package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.*;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.group5.ecommerce.controller.HomeController.getUserId;

@Controller
public class StoreAdminController {

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

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

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
        return "storeAdmin/adminHome";
    }

    @GetMapping("/store/{storeId}/categories")
    public String getCat(Model model,
                         @PathVariable("storeId") Long storeId) {
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId));
        return "storeAdmin/categories";
    }

    @GetMapping("/store/{storeId}/categories/add")
    public String getAddCat(@PathVariable("storeId") Long storeId,
                            Model model){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("category", new Category());
        model.addAttribute("storeId", storeId);
        return "storeAdmin/categoriesAdd";
    }

    @PostMapping("/store/{storeId}/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category,
                             @PathVariable("storeId") Long storeId) {
        categoryService.addCategory(category, storeId);
        return "redirect:/store/" + storeId + "/categories";
    }

    @GetMapping("/store/{storeId}/categories/delete/{id}")
    public String deleteCat(@PathVariable("id") Long categoryId,
                            @PathVariable("storeId") Long storeId){
        categoryService.removeCategoryById(categoryId);
        return "redirect:/store/" + storeId + "/categories";
    }

    @GetMapping("/store/{storeId}/categories/update/{id}")
    public String updateCat(@PathVariable("id") Long id, Model model,
                            @PathVariable("storeId") Long storeId){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            UserStore userStore = storeService.getStoreById(storeId);
            model.addAttribute("store", userStore);
            model.addAttribute("category", category.get());
            return "storeAdmin/categoriesAdd";
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
        return "storeAdmin/products";
    }

    @GetMapping("/store/{storeId}/products/{id}/details")
    public String getProductDetails(@PathVariable("storeId") Long storeId,
                                    @PathVariable("id") Long productId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model){
        UserStore userStore = storeService.getStoreById(storeId);
        Page<ProductDetail> productDetails = productService.getProductDetailsByProductId(productId, PageRequest.of(page, size));
        model.addAttribute("store", userStore);
        model.addAttribute("productDetails", productDetails);
        model.addAttribute("productId", productId);
        return "storeAdmin/productDetail";
    }

    @GetMapping("/store/{storeId}/products/{id}/details/add")
    public String getAddDetail(@PathVariable("storeId") Long storeId,
                               @PathVariable("id") Long productId,
                               Model model){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("detail", new ProductDetail());
        model.addAttribute("storeId", storeId);
        model.addAttribute("productId", productId);
        return "storeAdmin/productDetailAdd";
    }

    @PostMapping("/store/{storeId}/products/{id}/details/add")
    public String postAddDetail(@ModelAttribute("detail") ProductDetail productDetail,
                                @PathVariable("storeId") Long storeId,
                                @PathVariable("id") Long productId) {
        productService.addDetail(productDetail, productId);
        return "redirect:/store/" + storeId + "/products/" + productDetail.getProduct().getId() + "/details";
    }

    @PostMapping(value = "/update-detail", params = "action=update")
    public String updateDetail(@RequestParam("description") String description,
                             @RequestParam("id") Long detailId,
                             HttpServletRequest request){

        productService.updateDetail(detailId, description);

        String referer = request.getHeader("Referer");
        if(referer == null || referer.isEmpty())
            return "redirect:/details";

        return "redirect:" + referer;
    }

    @PostMapping(value = "/update-detail", params = "action=delete")
    public String deleteDetail(@RequestParam("id") Long detailId,
                               HttpServletRequest request){
        productService.removeDetail(detailId);

        String referer = request.getHeader("Referer");
        if(referer == null || referer.isEmpty())
            return "redirect:/details";

        return "redirect:" + referer;
    }

    @GetMapping("/store/{storeId}/products/add")
    public String getAddProduct(Model model,
                                @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId));
        model.addAttribute("isUpdate", false);
        return "storeAdmin/productsAdd";
    }

    @PostMapping("/store/{storeId}/products/add")
    public String postAddProduct(@ModelAttribute("product") Product product,
                                 @PathVariable("storeId") Long storeId) throws IOException {
        productService.addProduct(product, storeId);
        return "redirect:/store/" + storeId + "/products";
    }

    @GetMapping("/store/{storeId}/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                @PathVariable("storeId") Long storeId){
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
        return "storeAdmin/productsAdd";
    }



    // CUSTOMER SESSIONS //
    @GetMapping("/store/{storeId}/customers")
    public String getCustomers(Model model,
                               @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        List<User> customers = orderService.getCustomersPurchasedFromStore(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("customers", customers);
        return "storeAdmin/customer";
    }



    // ORDER SESSIONS //
    @GetMapping("/store/{storeId}/orders")
    public String getOrders(Model model,
                            @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("store", userStore);
        model.addAttribute("formatter", formatter);
        model.addAttribute("orders", orderService.getAllOrdersByStoreId(storeId));
        return "storeAdmin/orders";
    }

    @GetMapping("/store/{storeId}/orders/update/{id}")
    public String updateOrder(Model model,
                              @PathVariable("storeId") Long storeId,
                              @PathVariable("id") Long id){
        UserStore userStore = storeService.getStoreById(storeId);
        List<String> statuses = List.of("Pending", "Processing", "Delivering", "Delivered", "Canceled");
        model.addAttribute("store", userStore);
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("statuses", statuses);
        model.addAttribute("isUpdate", true);
        return "storeAdmin/ordersAdd";
    }

    @PostMapping("/store/{storeId}/orders/add")
    public String postAddOrder(@ModelAttribute("order") Order order,
                               @PathVariable("storeId") Long storeId){
        orderService.saveOrder(order, storeId);
        return "redirect:/store/" + storeId + "/orders";
    }

    @GetMapping("/store/{storeId}/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id,
                              @PathVariable("storeId") Long storeId){
        orderService.deleteOrder(id);
        return "redirect:/store/" + storeId + "/orders";
    }



    // SALES SESSIONS //
    @GetMapping("/store/{storeId}/sales")
    public String getSales(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        return "storeAdmin/sales";
    }



    // VOUCHER SESSIONS //
    @GetMapping("/store/{storeId}/vouchers")
    public String getVouchers(Model model, @PathVariable("storeId") Long storeId){
        model.addAttribute("store", storeService.getStoreById(storeId));
        model.addAttribute("vouchers", voucherService.findAllVouchersByStore(storeId));
        return "storeAdmin/voucher";
    }



    // REVIEWS SESSIONS //
    @GetMapping("/store/{storeId}/products/{id}/reviews")
    public String getReviewsByProductId(Model model,
                                        @PathVariable("storeId") Long storeId,
                                        @PathVariable("id") Long productId){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<Review> reviews = reviewService.getReviewsByProductId(productId);

        model.addAttribute("reviews", reviews);
        model.addAttribute("formatter", formatter);
        model.addAttribute("store", storeService.getStoreById(storeId));
        return "storeAdmin/productReviews";
    }
}
