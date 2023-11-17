package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.ProductDetail;
import com.group5.ecommerce.model.Review;
import com.group5.ecommerce.repository.ProductRepository;
import com.group5.ecommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public Review getById(Long id) {
        return reviewRepository.findById(id).get();
    }

    public void save(String comment, Long productId, Long userId) {
        Optional<Product> product = productService.getProductById(productId);
        product.ifPresent(p -> {
            Review view = Review.builder()
                    .comment(comment)
                    .dateTime(LocalDateTime.now())
                    .user(userService.getUserById(userId))
                    .product(p).build();
            reviewRepository.save(view);
        });
    }

    public void remove(Long id) {
        reviewRepository.deleteById(id);
    }
}
