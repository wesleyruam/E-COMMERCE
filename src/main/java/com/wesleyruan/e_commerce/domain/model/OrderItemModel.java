package com.wesleyruan.e_commerce.domain.model;

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
public class OrderItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JoinColumn(name = "order_id")
    private OrderModel order;


    @JoinColumn(name = "product_id")
    private ProductModel product;

    private Integer quantity;

    private Double price;
}
