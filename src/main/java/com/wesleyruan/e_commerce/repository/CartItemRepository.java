package com.wesleyruan.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wesleyruan.e_commerce.domain.model.CartItemModel;

public interface CartItemRepository extends JpaRepository<CartItemModel, Long> {
    Optional<CartItemModel> findByProductId(Long productId);

    Optional<CartItemModel> findByCartIdAndProductId(Long cartId, Long productId);
}
