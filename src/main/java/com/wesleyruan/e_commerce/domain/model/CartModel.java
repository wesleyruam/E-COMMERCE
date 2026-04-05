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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JoinColumn(name = "user_id")
    private UserModel user;
}
