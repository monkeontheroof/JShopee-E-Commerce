package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Review;
import com.group5.ecommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review getById(Long id) {
        return reviewRepository.findById(id).get();
    }

    public void remove(Long id) {
        reviewRepository.deleteById(id);
    }
}
