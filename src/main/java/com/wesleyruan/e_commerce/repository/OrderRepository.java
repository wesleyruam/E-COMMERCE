package com.wesleyruan.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wesleyruan.e_commerce.domain.model.OrderModel;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    
}
