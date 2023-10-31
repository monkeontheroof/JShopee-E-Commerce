package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreService storeService;

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllCategoryByStoreId(Long id) {
        return categoryRepository.findAllByStoreId(id);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void addCategory(Category category, Long storeId) {
        UserStore userStore = storeService.getStoreById(storeId);
        if(userStore == null) {
            return;
        }
        category.setStore(userStore);
        categoryRepository.save(category);
    }

    public void removeCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }
}
