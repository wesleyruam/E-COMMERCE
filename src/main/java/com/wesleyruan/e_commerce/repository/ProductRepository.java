package com.wesleyruan.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wesleyruan.e_commerce.domain.model.ProductModel;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long>{
    List<ProductModel> findByActiveTrue();

    List<ProductModel> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<ProductModel> findByPriceBetweenAndActiveTrue(Double minPrice, Double maxPrice);
}