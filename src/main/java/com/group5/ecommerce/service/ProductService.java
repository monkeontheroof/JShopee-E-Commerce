package com.group5.ecommerce.service;

import com.group5.ecommerce.model.OrderItem;
import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.Review;
import com.group5.ecommerce.model.UserStore;
import com.group5.ecommerce.repository.ProductRepository;
import com.group5.ecommerce.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ModelMapper mapper;

    public List<Product> getAllProduct(){

        return productRepository.findAll().stream()
                .map(p -> mapper.map(p, Product.class))
                .collect(Collectors.toList());
    }

    public Page<Product> getAllProductByStoreId(Long storeId, Pageable pageRequest){
        return productRepository.findAllByStoreId(storeId, pageRequest);
    }

    public List<Product> getProductsByCategoryId(Long categoryId){
        return productRepository.findAllByCategoryId(categoryId).stream()
                .map(p -> mapper.map(p, Product.class))
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id).map(p -> mapper.map(p, Product.class));
    }

    public List<Product> getProductsByQuantityLessThan(int quantity){
        return productRepository.findByQuantityLessThan(quantity);
    }

    public void addProduct(Product product, MultipartFile file, String imgName, Long storeId) throws IOException {
        String imageUUID = (file != null && !file.isEmpty()) ? saveImage(file) : imgName;
        UserStore store = storeService.getStoreById(storeId);
        product.setImageName(imageUUID);
        product.setStore(store);
        productRepository.save(product);
    }

    public void removeProductById(Long id){
        productRepository.deleteById(id);
    }

    private String saveImage(MultipartFile file) throws IOException {
        String imageUUID = file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
        Files.write(fileNameAndPath, file.getBytes());
        return imageUUID;
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
}
