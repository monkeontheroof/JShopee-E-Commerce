package com.group5.ecommerce.controller;

import com.group5.ecommerce.dto.ProductDTO;
import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.model.CustomUserDetail;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.StoreService;
import com.group5.ecommerce.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/home")
    public String storeHome(Model model) {
        Long userId = SecurityUtil.getPrincipal().getId();
        UserStore userStore = storeService.getStoreByUserId(userId);
        model.addAttribute("store", userStore);
        return "adminHome";
    }

    @GetMapping("/{storeId}/categories")
    public String getCat(Model model, @PathVariable("storeId") Long storeId) {
        UserStore userStore = storeService.getStoreById(storeId);
        model.addAttribute("store", userStore);
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(userStore.getId()));
        return "categories";
    }

    @GetMapping("/{storeId}/categories/add")
    public String getCatAdd(@PathVariable("storeId") Long storeId , Model model){
        model.addAttribute("category", new Category());
        model.addAttribute("storeId", storeId);
        return "categoriesAdd";
    }

    //post method for category
    @PostMapping("/store/{storeId}/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category, @PathVariable Long storeId) {
        categoryService.addCategory(category, storeId);
        return "redirect:/store/home";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCat(@PathVariable("id") Long id, Model model, @PathVariable String storeId){
        categoryService.removeCategoryById(id);
        return "redirect:/categories";  //redirect to categories page
    }

    @GetMapping("/categories/update/{id}")
    public String updateCat(@PathVariable("id") Long id, Model model, @PathVariable String storeId){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        }
        return "404";  //redirect to categories page
    }


    //PRODUCT SESSIONS

    @GetMapping("/products")
    public String getProducts(Model model, @PathVariable String storeId){
        model.addAttribute("products", productService.getAllProduct());
        return "products";  //redirect to categories page
    }

    @GetMapping("/products/add")
    public String getAddProduct(Model model, @PathVariable String storeId){
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }

    //post method for category
    @PostMapping("/products/add")
    public String postAddProduct(@ModelAttribute("ProductDTO") ProductDTO productDTO,
                                 @RequestParam("productImage") MultipartFile file,
                                 @RequestParam("imgName") String imgName, @PathVariable String storeId) throws IOException {
        productService.addProduct(productDTO, file, imgName);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, @PathVariable String storeId){
        productService.removeProductById(id);
        return "redirect:/products";  //redirect to categories page
    }

    @GetMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model, @PathVariable String storeId){
        ProductDTO productDTO = productService.getProductById(id);

        model.addAttribute("productDTO", productDTO);
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";

    }
}
