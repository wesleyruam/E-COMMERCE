package com.wesleyruan.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruan.e_commerce.dto.request.ProductRequestDTO;
import com.wesleyruan.e_commerce.dto.response.ProductResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse<ProductResponseDTO>> createProduct(@RequestBody @Valid ProductRequestDTO productRequest){
        ServiceResponse<ProductResponseDTO> response = productService.createProduct(productRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse<ProductResponseDTO>> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO productRequest){
        ServiceResponse<ProductResponseDTO> response = productService.updateProduct(id, productRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<ProductResponseDTO>> getProductById(@PathVariable Long id){
        ServiceResponse<ProductResponseDTO> response = productService.getProductById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<ServiceResponse<List<ProductResponseDTO>>> getAllProducts(){
        ServiceResponse<java.util.List<ProductResponseDTO>> response = productService.listProducts();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse<Void>> deleteProduct(@PathVariable Long id){
        ServiceResponse<Void> response = productService.deleteProduct(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ServiceResponse<List<ProductResponseDTO>>> searchProducts(@RequestParam(required = false) String name, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice){
        ServiceResponse<List<ProductResponseDTO>> response = productService.searchProducts(name, minPrice, maxPrice);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
