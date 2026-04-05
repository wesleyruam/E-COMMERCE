package com.wesleyruan.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wesleyruan.e_commerce.domain.model.CartModel;

public interface CartRepository extends JpaRepository<CartModel, Long>{
    Optional<CartModel> findByUserId(Long userId);
}
