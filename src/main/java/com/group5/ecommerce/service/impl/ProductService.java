package com.group5.ecommerce.service.impl;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.ProductDetailRepository;
import com.group5.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper mapper;

    public List<Product> getAllProduct(){

        return productRepository.findAll();
    }

    public Page<ProductDetail> getProductDetailsByProductId(Long productId, Pageable pageRequest) {
        Optional<Product> product = getProductById(productId);
        if(product.isPresent()) {
            return productDetailRepository.getProductDetailsByProductId(productId, pageRequest);
        }else {
            throw new RuntimeException("Product not found.");
        }
    }

    public Page<Product> getAllProductByStoreId(Long storeId, Pageable pageRequest){
        return productRepository.findAllByStoreId(storeId, pageRequest);
    }

    public List<Product> getProductsByCategoryId(Long categoryId){
        return productRepository.findAllByCategoryId(categoryId).stream()
                .map(p -> mapper.map(p, Product.class))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategoryName(String categoryName){
        List<Category> categories = categoryService.getCategoriesByNameContaining(categoryName.trim());
        List<Product> products = new ArrayList<>();
        if(!categories.isEmpty()){
            categories.forEach(c -> {
                products.addAll(c.getProducts());
            });
        }
        return products;
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id).map(p -> mapper.map(p, Product.class));
    }

    public List<Product> getProductsByQuantityLessThan(int quantity){
        return productRepository.findByQuantityLessThan(quantity);
    }

    public void addProduct(Product product, Long storeId){
        if(product.getQuantity() < 0)
            throw new RuntimeException("Product quantity must be a positive number.");
        product.setStore(storeService.getStoreById(storeId));
        productRepository.save(product);
    }

    public void addDetail(ProductDetail detail, Long productId){
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
            detail.setProduct(product.get());
            productDetailRepository.save(detail);
        }
    }

    public void updateDetail(Long detailId, String description){
        Optional<ProductDetail> detail = productDetailRepository.findById(detailId);
        detail.ifPresent(d -> {
            d.setDescription(description);
            productDetailRepository.save(d);
        });
    }

    public void removeDetail(Long detailId){
        Optional<ProductDetail> detail = productDetailRepository.findById(detailId);
        detail.ifPresent(d -> productDetailRepository.deleteById(d.getId()));
    }

    public void removeProductById(Long id){
        productRepository.deleteById(id);
    }

    public List<Review> getReviewsByProductId(long productId){
        Product product = getProductById(productId).get();
        List<Review> productReviews = product.getReviews();
        if(productReviews == null) {
            productReviews = new ArrayList<>();
            product.setReviews(productReviews);
            productRepository.save(product);
        }
        return productReviews;
    }

    public void saveProduct(Product product){
        productRepository.save(product);
    }
}
