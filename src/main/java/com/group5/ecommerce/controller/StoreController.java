package com.group5.ecommerce.controller;

import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.StoreService;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

//    @GetMapping("/sideMenu")
//    public String getSideMenu(Model model) {
//        Long userId = SecurityUtil.getPrincipal().getId();
//        UserStore userStore = storeService.getStoreByUserId(userId);
//        model.addAttribute("store", userStore);
//        return "fragments/sideMenu";
//    }


    @GetMapping("/store/home")
    public String getHome(Model model) {
        Long userId = SecurityUtil.getPrincipal().getId();
        UserStore userStore = storeService.getStoreByUserId(userId);
        model.addAttribute("store", userStore);
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
    public String getProducts(Model model, @PathVariable("storeId") Long storeId){
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("products", productService.getAllProductByStoreId(storeId));
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
        model.addAttribute("product", productService.getProductById(id).get());
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(storeId));
        model.addAttribute("isUpdate", true);
        return "productsAdd";
    }
}
