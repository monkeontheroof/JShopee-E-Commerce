package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.model.ProductImage;
import com.group5.ecommerce.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImageService {
    private static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageRepository productImageRepository;

    public void addProductImages(Long productId, MultipartFile file) throws IOException {
        Optional<Product> product = productService.getProductById(productId);
        if(product.isPresent()) {
            ProductImage image = ProductImage.builder()
                    .imageName(saveImage(file))
                    .product(product.get())
                    .build();
            product.get().getImages().add(image);
            productService.saveProduct(product.get());
        }
        return;
    }

    public void removeProductImages(Long imageId){
        productImageRepository.deleteById(imageId);
    }

    private String saveImage(MultipartFile file) throws IOException {
        String imageUUID = file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
        Files.write(fileNameAndPath, file.getBytes());
        return imageUUID;
    }
}
