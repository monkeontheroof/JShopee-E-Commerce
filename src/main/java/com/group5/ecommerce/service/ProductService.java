package com.group5.ecommerce.service;

import com.group5.ecommerce.model.*;
import com.group5.ecommerce.repository.ProductDetailRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ModelMapper mapper;

    public List<Product> getAllProduct(){

        return productRepository.findAll().stream()
                .map(p -> mapper.map(p, Product.class))
                .collect(Collectors.toList());
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

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id).map(p -> mapper.map(p, Product.class));
    }

    public List<Product> getProductsByQuantityLessThan(int quantity){
        return productRepository.findByQuantityLessThan(quantity);
    }

    public void addProduct(Product product, Long storeId){
        product.setStore(storeService.getStoreById(storeId));
        productRepository.save(product);
    }

//    public void addProduct(Product product, MultipartFile[] files, Long storeId) throws IOException {
//        List<ProductImage> imageFiles = Arrays.stream(files).map(file -> {
//            ProductImage image = null;
//            try {
//                image = ProductImage.builder()
//                        .imageName(saveImage(file))
//                        .product(product)
//                        .build();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            return image;
//        }).collect(Collectors.toList());
//        product.setImages(imageFiles);
//        UserStore store = storeService.getStoreById(storeId);
//        product.setStore(store);
//        productRepository.save(product);
//    }

    public void addDetail(ProductDetail detail, Long productId){
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
            product.get().getDetails().add(detail);
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
