package com.group5.ecommerce.service;

import com.group5.ecommerce.model.Product;
import com.group5.ecommerce.repository.ProductRepository;
import com.group5.ecommerce.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;

    public List<ProductDTO> getAllProduct(){

        return productRepository.findAll().stream()
                .map(p -> mapper.map(p, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategoryId(Long categoryId){
        return productRepository.findAllByCategory_Id(categoryId).stream()
                .map(p -> mapper.map(p, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(!optionalProduct.isPresent()) {
            throw new EntityNotFoundException("Product Not Found");
        }
        return mapper.map(optionalProduct.get(), ProductDTO.class);
    }

    public void addProduct(ProductDTO productDTO, MultipartFile file, String imgName) throws IOException {
        String imageUUID = (file != null && !file.isEmpty()) ? saveImage(file) : imgName;
        productDTO.setImageName(imageUUID);
        Product product = mapper.map(productDTO, Product.class);
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
}
