package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.service.impl.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/store")
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
    private VoucherServiceImpl voucherServiceImpl;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private CartService cartService;

//    @GetMapping("/sideMenu")
//    public String getSideMenu(Model model) {
//        Long userId = SecurityUtil.getPrincipal().getId();
//        UserStore userStore = storeService.getStoreByUserId(userId);
//        model.addAttribute("store", userStore);
//        return "fragments/sideMenu";
//    }


    @GetMapping("/home")
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
        return "store/index";
    }

    @GetMapping("/{storeId}/categories")
    public String getCat(Model model,
                         @PathVariable("storeId") Long storeId,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size) {
        UserStore userStore = storeService.getStoreById(storeId);
        Page<Category> categoriesPage = categoryService.getAllCategoryByStoreId(storeId, PageRequest.of(page - 1, size));
        model.addAttribute("store", userStore);
        model.addAttribute("categories", categoriesPage.getContent());
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        model.addAttribute("currentPage", categoriesPage.getNumber() + 1);
        model.addAttribute("pageSize", size);
        return "store/categories";
    }

    @GetMapping("/{storeId}/categories/add")
    public String getAddCat(@PathVariable("storeId") Long storeId,
                            Model model){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("category", new Category());
        model.addAttribute("storeId", storeId);
        return "store/categories-add";
    }

    @PostMapping("/{storeId}/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category,
                             @PathVariable("storeId") Long storeId) {
        categoryService.addCategory(category, storeId);
        return "redirect:/store/" + storeId + "/categories";
    }

    @GetMapping("/{storeId}/categories/delete/{id}")
    public String deleteCat(@PathVariable("id") Long categoryId,
                            @PathVariable("storeId") Long storeId){
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        category.ifPresent(c -> {
            if(!c.getProducts().isEmpty()){
                categoryService.removeCategoryById(categoryId);
            }
        });
        return "redirect:/store/" + storeId + "/categories";
    }

    @GetMapping("/{storeId}/categories/update/{id}")
    public String updateCat(@PathVariable("id") Long id, Model model,
                            @PathVariable("storeId") Long storeId){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            UserStore userStore = storeService.getStoreById(storeId);
            model.addAttribute("store", userStore);
            model.addAttribute("category", category.get());
            return "store/categories-add";
        }
        return "Redirect:/error";  //redirect to categories page
    }


    //PRODUCT SESSIONS

    @GetMapping("/{storeId}/products")
    public String getProducts(Model model,
                              @PathVariable("storeId") Long storeId,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size){
        UserStore userStore = storeService.getStoreById(storeId);
        Page<Product> productsPage = productService.getAllProductByStoreId(storeId, PageRequest.of(page - 1, size));
        model.addAttribute("store", userStore);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("currentPage", productsPage.getNumber() + 1);
        model.addAttribute("pageSize", size);
        model.addAttribute("detail", new ProductDetail());
        return "store/products";
    }

    @GetMapping("/{storeId}/products/{id}/details")
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

    @PostMapping("/{storeId}/products/{id}/details/add")
    public String postAddDetail(@ModelAttribute("detail") ProductDetail productDetail,
                                @PathVariable("storeId") Long storeId,
                                @PathVariable("id") Long productId,
                                HttpServletRequest request) {
        productService.addDetail(productDetail, productId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping(value = "/update-detail", params = "action=update")
    public String updateDetail(@RequestParam("description") String description,
                             @RequestParam("id") Long detailId,
                             HttpServletRequest request){

        productService.updateDetail(detailId, description);

        String referer = request.getHeader("Referer");
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

    @GetMapping("/{storeId}/products/add")
    public String getAddProduct(Model model,
                                @PathVariable("storeId") Long storeId){
        DecimalFormat formatter = new DecimalFormat("#,###");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("decimalFormatter", formatter);
        model.addAttribute("datetimeFormatter", dateTimeFormatter);
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getCategoriesByStoreId(storeId));
        model.addAttribute("isUpdate", false);
        return "store/product-add";
    }

    @PostMapping("/{storeId}/products/add")
    public String postAddProduct(@ModelAttribute("product") Product product,
                                 @PathVariable("storeId") Long storeId) throws IOException {
        productService.addProduct(product, storeId);
        return "redirect:/store/" + storeId + "/products";
    }

    @GetMapping("/{storeId}/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                @PathVariable("storeId") Long storeId){
        productService.removeProductById(id);
        return "redirect:/store/" + storeId + "/products";  //redirect to categories page
    }

    @GetMapping("/{storeId}/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                Model model,
                                @PathVariable("storeId") Long storeId){

        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("product", productService.getProductById(id).orElse(null));
        model.addAttribute("detail", new ProductDetail());
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId, PageRequest.of(0,5)).getContent());
        model.addAttribute("isUpdate", true);
        return "store/product-add";
    }

    @PostMapping(value = "/upload-productImage")
    public String postProductImage(@RequestParam("image")MultipartFile file,
                                   @RequestParam("productId") Long productId,
                                   HttpServletRequest request) throws IOException {
        imageService.addProductImages(productId, file);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/remove-productImage/{imageId}")
    public String removeProductImage(@PathVariable("imageId") Long imageId,
                                     HttpServletRequest request) {
        imageService.removeProductImages(imageId);
        return "redirect:" + request.getHeader("Referer");
    }

    // CUSTOMER SESSIONS //
    @GetMapping("/{storeId}/customers")
    public String getCustomers(Model model,
                               @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        List<User> customers = orderService.getCustomersPurchasedFromStore(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("customers", customers);
        return "storeAdmin/customers";
    }

    // ORDER SESSIONS //
    @GetMapping("/{storeId}/orders")
    public String getOrders(Model model,
                            @PathVariable("storeId") Long storeId,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size){
        Page<Order> orderPage = orderService.getAllOrdersByStoreId(storeId, PageRequest.of(page - 1, size));
        UserStore userStore = storeService.getStoreById(storeId);
        DecimalFormat formatter = new DecimalFormat("#,###");
        model.addAttribute("store", userStore);
        model.addAttribute("formatter", formatter);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("waitToPay", orderPage.getContent().stream().filter(order -> !order.isPaid()).collect(Collectors.toList()));
        model.addAttribute("delivering", orderPage.getContent().stream().filter(order -> order.getStatus().trim().equals("Đang giao hàng")).collect(Collectors.toList()));
        model.addAttribute("finished", orderPage.getContent().stream().filter(order -> order.getStatus().trim().equals("Đã giao")).collect(Collectors.toList()));
        model.addAttribute("canceled", orderPage.getContent().stream().filter(order -> order.getStatus().trim().equals("Đã hủy")).collect(Collectors.toList()));
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getNumber() + 1);
        model.addAttribute("pageSize", size);
        return "store/orders";
    }

    @GetMapping("/{storeId}/orders/{id}/details")
    public String getOrderDetails(@PathVariable("storeId") Long storeId,
                                  @PathVariable("id") Long orderId,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  Model model){
        DecimalFormat formatter = new DecimalFormat("#,###");
        Page<OrderItem> orderItemsPage = orderService.getOrderItemsByOrderId(orderId, PageRequest.of(page - 1, size));
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("formatter", formatter);
        model.addAttribute("store", userStore);
        model.addAttribute("orderItems", orderItemsPage.getContent());
        model.addAttribute("totalPages", orderItemsPage.getTotalPages());
        model.addAttribute("currentPage", orderItemsPage.getNumber() + 1);
        model.addAttribute("pageSize", size);
        return "store/order-items";
    }

    @GetMapping("/{storeId}/orders/update/{id}")
    public String getUpdateOrder(Model model,
                              @PathVariable("storeId") Long storeId,
                              @PathVariable("id") Long id){
        UserStore userStore = storeService.getStoreById(storeId);
        List<String> statuses = List.of("Đang xác nhận", "Đang xử lý", "Đang giao hàng", "Đã giao", "Đã hủy");
        List<Boolean> isPaid = List.of(true, false);
        model.addAttribute("store", userStore);
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("statuses", statuses);
        model.addAttribute("isPaid", isPaid);
        model.addAttribute("isUpdate", true);
        return "store/order-add";
    }

    @PostMapping("/{storeId}/orders/update/{id}")
    public String postUpdateOrder(Model model,
                              @PathVariable("storeId") Long storeId,
                              @PathVariable("id") Long orderId,
                              @RequestParam("isPaid") boolean isPaid,
                              @RequestParam("status") String status){
        orderService.updateOrderStatus(isPaid, status, orderId);
        UserStore store = storeService.getStoreById(storeId);
        model.addAttribute("store", store);
        return "redirect:/store/" + storeId + "/orders";
    }

    @PostMapping("/{storeId}/orders/add")
    public String postAddOrder(@ModelAttribute("order") Order order,
                               @PathVariable("storeId") Long storeId){
        orderService.saveOrder(order, storeId);
        return "redirect:/store/" + storeId + "/orders";
    }

    @GetMapping("/{storeId}/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id,
                              @PathVariable("storeId") Long storeId){
        orderService.deleteOrder(id);
        return "redirect:/store/" + storeId + "/orders";
    }



    // SALES SESSIONS //
    @GetMapping("/{storeId}/sales")
    public String getSales(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        return "store/sales";
    }



    // VOUCHER SESSIONS //
    @GetMapping("/{storeId}/vouchers")
    public String getVouchers(Model model, @PathVariable("storeId") Long storeId,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Page<Voucher> voucherPages = voucherServiceImpl.findAllVouchersByStore(storeId, PageRequest.of(page - 1, size));
        model.addAttribute("store", storeService.getStoreById(storeId));
        model.addAttribute("vouchers", voucherPages.getContent());
        model.addAttribute("formatter", dateTimeFormatter);
        model.addAttribute("totalPages", voucherPages.getTotalPages());
        model.addAttribute("currentPage", voucherPages.getNumber() + 1);
        model.addAttribute("pageSize", size);
        return "store/vouchers";
    }

    @GetMapping("/{storeId}/vouchers/add")
    public String getAddVoucher(@PathVariable("storeId") Long storeId,
                            Model model){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("voucher", new Voucher());
        model.addAttribute("storeId", storeId);
        return "store/vouchers-add";
    }

    @PostMapping("/{storeId}/vouchers/add")
    public String postAddVoucher(@ModelAttribute("voucher") Voucher voucher,
                                 @PathVariable("storeId") Long storeId,
                                 Model model) {
        UserStore userStore = storeService.getStoreById(storeId);
        if(voucher.getDiscountAmount() == 0){
            model.addAttribute("error", true);
            model.addAttribute("store", userStore);
            model.addAttribute("voucher", new Voucher());
            model.addAttribute("storeId", storeId);
            return "store/vouchers-add";
        }
        voucherServiceImpl.createVoucher(storeId, voucher);
        return "redirect:/store/" + storeId + "/vouchers";
    }

    @GetMapping("/{storeId}/vouchers/delete/{id}")
    public String deleteVoucher(@PathVariable("storeId") Long storeId,
                                @PathVariable("id") Long voucherId){
        Voucher voucher = voucherServiceImpl.findById(voucherId);
        if(voucher != null && voucher.getExpiryDate().isBefore(LocalDate.now())){
            voucherServiceImpl.deleteVoucherById(voucherId);
        }
        return "redirect:/store/" + storeId + "/vouchers";
    }

    // REVIEWS SESSIONS //
    @GetMapping("/{storeId}/products/{id}/reviews")
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

    @GetMapping("/delete-reviews/{id}")
    public String deleteReviews(Model model,
                                @PathVariable("id") Long reviewId,
                                HttpServletRequest request){
        reviewService.remove(reviewId);
        return "redirect:" + request.getHeader("Referer");
    }
}
