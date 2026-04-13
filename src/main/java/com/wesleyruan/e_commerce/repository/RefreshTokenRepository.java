package com.wesleyruan.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wesleyruan.e_commerce.domain.model.RefreshTokenModel;
import com.wesleyruan.e_commerce.domain.model.UserModel;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Long> {
    Optional<RefreshTokenModel> findByToken(String token);
    void deleteByUser(UserModel user);
}
