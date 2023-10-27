package com.group5.ecommerce.controller;

import com.group5.ecommerce.dto.ProductDTO;
import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.service.CategoryService;
import com.group5.ecommerce.service.ProductService;
import com.group5.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService  productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminHome(){
    return "adminHome";}


    //CATEGORY SESSIONS
    @GetMapping("/categories")
    public String getCat(Model model){
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }

    @GetMapping("/categories/add")
    public String getAddCat(Model model){
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    //post method for category
    @PostMapping("/categories/add")
    public String postAddCat(@ModelAttribute("category") Category category) {
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCat(@PathVariable("id") Long id, Model model){
        categoryService.removeCategoryById(id);
        return "redirect:/categories";  //redirect to categories page
    }

    @GetMapping("/categories/update/{id}")
    public String updateCat(@PathVariable("id") Long id, Model model){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        }
        return "404";  //redirect to categories page
    }




    //PRODUCT SESSIONS

    @GetMapping("/products")
    public String getProducts(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "products";  //redirect to categories page
    }

    @GetMapping("/products/add")
    public String getAddProduct(Model model){
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }

    //post method for category
    @PostMapping("/products/add")
    public String postAddProduct(@ModelAttribute("ProductDTO") ProductDTO productDTO,
                                 @RequestParam("productImage")MultipartFile file,
                                 @RequestParam("imgName") String imgName) throws IOException {
        productService.addProduct(productDTO, file, imgName);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id){
        productService.removeProductById(id);
        return "redirect:/products";  //redirect to categories page
    }

    @GetMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model){
        ProductDTO productDTO = productService.getProductById(id);

        model.addAttribute("productDTO", productDTO);
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";

    }



    // USER SESSIONS
    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "users";  //redirect to users page
    }

    @GetMapping("/users/add")
    public String getAddUser(Model model){
        model.addAttribute("user", new User());
        return "usersAdd"; //redirect to add user page
    }

    //post method for category
    @PostMapping("/users/add")
    public String postAddUser(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.removeUserById(id);

        return "redirect:/users";  //redirect to categories page
    }

    @GetMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model){
        User user = userService.getUserById(id);

        model.addAttribute("user", user);
        return "usersAdd";

    }

}
