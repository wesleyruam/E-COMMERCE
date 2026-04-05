package com.wesleyruan.e_commerce.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.wesleyruan.e_commerce.domain.model.UserModel;
import com.wesleyruan.e_commerce.dto.request.LoginRequestDTO;
import com.wesleyruan.e_commerce.dto.response.LoginResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.exception.NotFoundException;


@Service
public class AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    Instant now = Instant.now();
    Long expiresIn = 300L; // 5 minutos


    public ServiceResponse<LoginResponseDTO> login(LoginRequestDTO request){
        var user = userService.getUserByEmail(request.email()).orElseThrow(() -> new NotFoundException("Email or password is incorrect"));


        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            return ServiceResponse.error("Email or password is incorrect");
        }

        if (user.isDeleted()) {
            return ServiceResponse.error("User not found");
        }

        String token = generateToken(user);



        LoginResponseDTO response = new LoginResponseDTO(
            token,
            expiresIn
        );  
        return ServiceResponse.success(response);
    }

    private String generateToken(UserModel userModel){
        Instant now = Instant.now();

        var claims = JwtClaimsSet.builder()
            .issuer("e-commerce-api") // quem emitiu o token
            .subject(userModel.getEmail()) // quem é o dono do token (normalmente o email ou id do usuário)
            .issuedAt(now) // quando o token foi emitido
            .expiresAt(now.plusSeconds(300)) // quando o token expira
            .claim("id", userModel.getId()) // informações adicionais que queremos incluir no token, como o id do usuário e o papel (role)
            .claim("role", "ROLE_" +  userModel.getRole()) // o papel do usuário ("USER" ou "ADMIN")
            .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return jwtValue;
    }
}
