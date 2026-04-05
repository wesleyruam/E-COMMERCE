package com.wesleyruan.e_commerce.domain.model;


import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.wesleyruan.e_commerce.domain.enums.OrderStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    private UserModel user;

    private Double total;

    private OrderStatusEnum status;

    private String paymentId;
    private String paymentUrl;

    @CreationTimestamp
    private Instant createdAt;



}
