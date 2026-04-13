package com.wesleyruan.e_commerce.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wesleyruan.e_commerce.domain.model.RefreshTokenModel;
import com.wesleyruan.e_commerce.domain.model.UserModel;
import com.wesleyruan.e_commerce.dto.request.LoginRequestDTO;
import com.wesleyruan.e_commerce.dto.request.RefreshTokenRequestDTO;
import com.wesleyruan.e_commerce.dto.response.LoginResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.exception.NotFoundException;
import com.wesleyruan.e_commerce.repository.RefreshTokenRepository;

@Service
public class AuthService {

    private static final long ACCESS_TOKEN_EXPIRY  = 300L;        // 5 minutos
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 3600L; // 7 dias

    @Autowired
    private UserService userService;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ServiceResponse<LoginResponseDTO> login(LoginRequestDTO request) {
        var user = userService.getUserByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Email or password is incorrect"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ServiceResponse.error("Email or password is incorrect");
        }

        if (user.isDeleted()) {
            return ServiceResponse.error("User not found");
        }

        String accessToken  = generateAccessToken(user);
        String refreshToken = rotateRefreshToken(user);

        return ServiceResponse.success(new LoginResponseDTO(
                accessToken,
                ACCESS_TOKEN_EXPIRY,
                refreshToken,
                REFRESH_TOKEN_EXPIRY
        ));
    }

    @Transactional
    public ServiceResponse<LoginResponseDTO> refresh(RefreshTokenRequestDTO request) {
        var stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElse(null);

        if (stored == null || stored.isRevoked()) {
            return ServiceResponse.error("Invalid refresh token");
        }

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            return ServiceResponse.error("Refresh token expired");
        }

        UserModel user = stored.getUser();

        if (user.isDeleted()) {
            return ServiceResponse.error("User not found");
        }

        String newAccessToken  = generateAccessToken(user);
        String newRefreshToken = rotateRefreshToken(user); // rotação: invalida o anterior

        return ServiceResponse.success(new LoginResponseDTO(
                newAccessToken,
                ACCESS_TOKEN_EXPIRY,
                newRefreshToken,
                REFRESH_TOKEN_EXPIRY
        ));
    }

    /** Revoga todos os refresh tokens do usuário (usado no logout). */
    @Transactional
    public void revokeRefreshTokens(UserModel user) {
        refreshTokenRepository.deleteByUser(user);
    }

    // --- privados ---

    private String generateAccessToken(UserModel user) {
        Instant now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("e-commerce-api")
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRY))
                .claim("id",   user.getId())
                .claim("role", "ROLE_" + user.getRole())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /** Apaga token anterior do usuário e gera um novo (rotação). */
    private String rotateRefreshToken(UserModel user) {
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        var entity   = new RefreshTokenModel();
        entity.setToken(token);
        entity.setUser(user);
        entity.setExpiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY));
        entity.setRevoked(false);
        refreshTokenRepository.saveAndFlush(entity);

        return token;
    }
}
