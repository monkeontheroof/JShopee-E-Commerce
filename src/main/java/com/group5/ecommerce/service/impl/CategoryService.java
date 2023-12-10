package com.group5.ecommerce.service.impl;

import com.group5.ecommerce.model.Category;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public List<Category> getCategoriesByNameContaining(String name) {
        return categoryRepository.findAllByNameContaining(name);
    }

    public List<Category> getCategoriesByStoreId(Long storeId) {
        return categoryRepository.findAllByStoreId(storeId);
    }

    public Page<Category> getAllCategoryByStoreId(Long id, Pageable pageRequest) {
        return categoryRepository.findAllByStoreId(id, pageRequest);
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
