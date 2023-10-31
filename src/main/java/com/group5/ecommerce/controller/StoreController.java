package com.group5.ecommerce.controller;

import com.group5.ecommerce.dto.ProductDTO;
import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/store/{storeId}/admin")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/home")
    public String storeHome(@PathVariable("storeId") Long storeId, Model model) {
//        UserStore store = storeService.getStoreByUserId(userId);
//        model.addAttribute("store", store);
        return "adminHome";
    }

    @GetMapping("/categories")
    public String getCat(@PathVariable("storeId") Long userId , Model model){
        model.addAttribute("categories", categoryService.getAllCategoryByStoreId(userId));
        return "categories";
    }

    @GetMapping("/categories/add")
    public String getAddCat(Model model, @PathVariable String storeId){
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    //post method for category
    @PostMapping("/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category, @PathVariable Long storeId) {
        categoryService.addCategory(category, storeId);
        return "redirect:/categories";
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
