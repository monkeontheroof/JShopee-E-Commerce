package com.group5.ecommerce.controller;

import com.group5.ecommerce.dto.ProductDTO;
import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService  productService;

    @GetMapping("/admin")
    public String adminHome(){
    return "adminHome";}


    //CATEGORY SESSIONS
    @GetMapping("/admin/categories")
    public String getCat(Model model){
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }

    @GetMapping("/admin/categories/add")
    public String getAddCat(Model model){
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    //post method for category
    @PostMapping("/admin/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category) {
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCat(@PathVariable("id") Long id, Model model){
        categoryService.removeCategoryById(id);
        return "redirect:/admin/categories";  //redirect to categories page
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCat(@PathVariable("id") Long id, Model model){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        }
        return "404";  //redirect to categories page
    }


    //PRODUCT SESSIONS

    @GetMapping("/admin/products")
    public String getProducts(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "products";  //redirect to categories page
    }

    @GetMapping("/admin/products/add")
    public String getAddProduct(Model model){
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }

    //post method for category
    @PostMapping("/admin/products/add")
    public String postAddProduct(@ModelAttribute("ProductDTO") ProductDTO productDTO,
                                 @RequestParam("productImage")MultipartFile file,
                                 @RequestParam("imgName") String imgName) throws IOException {
        productService.addProduct(productDTO, file, imgName);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id){
        productService.removeProductById(id);
        return "redirect:/admin/products";  //redirect to categories page
    }

    @GetMapping("/admin/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model){
        ProductDTO productDTO = productService.getProductById(id);

        model.addAttribute("productDTO", productDTO);
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";

    }
}
