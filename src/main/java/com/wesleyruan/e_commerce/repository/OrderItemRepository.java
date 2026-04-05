package com.wesleyruan.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wesleyruan.e_commerce.domain.model.OrderItemModel;

public interface OrderItemRepository extends JpaRepository<OrderItemModel, Long> {
    
}
