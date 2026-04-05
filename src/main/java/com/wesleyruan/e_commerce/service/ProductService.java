package com.wesleyruan.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wesleyruan.e_commerce.domain.model.ProductModel;
import com.wesleyruan.e_commerce.dto.request.ProductRequestDTO;
import com.wesleyruan.e_commerce.dto.response.ProductResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.exception.NotFoundException;
import com.wesleyruan.e_commerce.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ServiceResponse<ProductResponseDTO> createProduct(ProductRequestDTO productRequest){
        ProductModel product = new ProductModel();
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStock(productRequest.stock());
        product.setActive(true);

        productRepository.saveAndFlush(product);
        ProductResponseDTO response = new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.isActive()
        );

        return ServiceResponse.success(response);
    } 

    public ServiceResponse<ProductResponseDTO> updateProduct(Long id, ProductRequestDTO productRequest){
        ProductModel product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStock(productRequest.stock());

        productRepository.saveAndFlush(product);
        ProductResponseDTO response = new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.isActive()
        );

        return ServiceResponse.success(response);
    }

    public ServiceResponse<Void> deleteProduct(Long id){
        ProductModel product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        product.setActive(false);
        productRepository.saveAndFlush(product);
        return ServiceResponse.success(null);
    }

    public ServiceResponse<List<ProductResponseDTO>> listProducts(){
        List<ProductModel> products = productRepository.findByActiveTrue();
        List<ProductResponseDTO> response = products.stream().map(product -> new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.isActive()
        )).toList();

        return ServiceResponse.success(response);
    }

    public ServiceResponse<ProductResponseDTO> getProductById(Long id){
        ProductModel product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        ProductResponseDTO response = new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.isActive()
        );

        return ServiceResponse.success(response);
    }

    public List<ProductModel> searchProductsByName(String name){
        List<ProductModel> products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
        return products;
    }


    public List<ProductModel> searchProductsByPriceRange(Double minPrice, Double maxPrice){
        List<ProductModel> products = productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice);

        return products;
    }

    public ServiceResponse<List<ProductResponseDTO>> searchProducts(String name, Double minPrice, Double maxPrice){

        List<ProductModel> products = productRepository.findAll();

        if (name != null && !name.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }

        if (minPrice != null && maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                    .toList();
        }

        products = products.stream()
                .filter(ProductModel::isActive)
                .toList();
        
        List<ProductResponseDTO> response = products.stream().map(product -> new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.isActive()
        )).toList();

        return ServiceResponse.success(response);
    }
}
