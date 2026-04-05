package com.wesleyruan.e_commerce.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wesleyruan.e_commerce.domain.model.UserModel;
import com.wesleyruan.e_commerce.dto.request.UserRequestDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.dto.response.UserResponseDTO;
import com.wesleyruan.e_commerce.repository.UserRepository;
import com.wesleyruan.e_commerce.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecurityUtils securityUtils;

    @Transactional
    public ServiceResponse<UserResponseDTO> createUser(UserRequestDTO user){
        if (getUserByEmail(user.email()).isPresent()){
            return ServiceResponse.conflict("Email already in use: " + user.email());
        }

        UserModel newUser = new UserModel();

        newUser.setName(user.name());
        newUser.setEmail(user.email());
        newUser.setPassword(passwordEncoder.encode(user.password())); // vou adicionar a senha em formato de hash depois, por enquanto é só pra testar mesmo.
        newUser.setPhone(user.phone());
        newUser.setDateOfBirth(user.dateOfBirth());

        newUser = userRepository.saveAndFlush(newUser);

        UserResponseDTO responseData = new UserResponseDTO(
            newUser.getId(),
            newUser.getName(),
            newUser.getEmail(),
            newUser.getPhone(),
            newUser.getRole(),
            newUser.getDateOfBirth(),
            newUser.getCreatedAt(),
            newUser.getUpdatedAt()
        );

        return ServiceResponse.created(responseData, "User created successfully.");
    }

    public Optional<UserModel> getUserById(Long id){
        Optional<UserModel> userOptional = userRepository.findById(id);
        
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        UserModel user = userOptional.get();
        
        return user.isDeleted() ? Optional.empty() : Optional.of(user);
    }

    public ServiceResponse<UserResponseDTO> updateUser(UserRequestDTO user){
        Long id = securityUtils.getUserId();

        Optional<UserModel> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ServiceResponse.notFound("User not found with id: " + id);
        }

        UserModel existingUser = userOptional.get();
        existingUser.setName(user.name());
        existingUser.setEmail(user.email());
        existingUser.setPassword(passwordEncoder.encode(user.password()));
        existingUser.setPhone(user.phone());
        existingUser.setDateOfBirth(user.dateOfBirth());

        existingUser = userRepository.saveAndFlush(existingUser);

        UserResponseDTO responseData = new UserResponseDTO(
            existingUser.getId(),
            existingUser.getName(),
            existingUser.getEmail(),
            existingUser.getPhone(),
            existingUser.getRole(),
            existingUser.getDateOfBirth(),
            existingUser.getCreatedAt(),
            existingUser.getUpdatedAt()
        );

        return ServiceResponse.success(responseData);
    }

    protected Optional<UserModel> getUserByEmail(String email){ // de momento, função utilizada apenas para a classe atual para verificar se ja tem um usuário com aquele email.
        return userRepository.findByEmail(email);
    }

    public ServiceResponse<Void> deleteUser(){
        Long id = securityUtils.getUserId();

        Optional<UserModel> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            return ServiceResponse.notFound("User not found with id: " + id);
        }else if (userOptional.get().isDeleted()){
            return ServiceResponse.notFound("User not found with id: " + id);
        }

        UserModel user = userOptional.get();
        user.setDeleted(true);
        userRepository.saveAndFlush(user);
        return ServiceResponse.noContent();

    }

    public ServiceResponse<UserResponseDTO> getUserProfile() {
        Long userId = securityUtils.getUserId();

        Optional<UserModel> user = getUserById(userId);

        if (user.isEmpty()) {
            return ServiceResponse.notFound("User not found");
        }

        UserModel userModel = user.get();

        UserResponseDTO responseData = new UserResponseDTO(
            userModel.getId(),
            userModel.getName(),
            userModel.getEmail(),
            userModel.getPhone(),
            userModel.getRole(),
            userModel.getDateOfBirth(),
            userModel.getCreatedAt(),
            userModel.getUpdatedAt()
        );

        return ServiceResponse.success(responseData);
    }


    
}
