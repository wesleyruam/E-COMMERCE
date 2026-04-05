package com.wesleyruan.e_commerce.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wesleyruan.e_commerce.domain.model.CartItemModel;
import com.wesleyruan.e_commerce.domain.model.CartModel;
import com.wesleyruan.e_commerce.domain.model.ProductModel;
import com.wesleyruan.e_commerce.domain.model.UserModel;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.exception.BadRequestException;
import com.wesleyruan.e_commerce.exception.NotFoundException;
import com.wesleyruan.e_commerce.repository.CartItemRepository;
import com.wesleyruan.e_commerce.repository.CartRepository;
import com.wesleyruan.e_commerce.repository.ProductRepository;
import com.wesleyruan.e_commerce.repository.UserRepository;
import com.wesleyruan.e_commerce.util.SecurityUtils;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ProductRepository productRepository;

    public ServiceResponse<String> addToCart(Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        CartModel cart = getUserCart();

        ProductModel product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        CartItemModel item = cartItemRepository
            .findByCartIdAndProductId(cart.getId(), productId)
            .orElse(null);

        int newQuantity = (item != null ? item.getQuantity() : 0) + quantity;

        if (product.getStock() < newQuantity) {
            throw new BadRequestException("Not enough stock for product");
        }

        if (item != null) {
            item.setQuantity(newQuantity);
        } else {
            item = new CartItemModel(); 
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
        }

        cartItemRepository.save(item);

        return ServiceResponse.success("Product added to cart");
    }


    public CartModel createCart(){
        UserModel user = userRepository.findById(securityUtils.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        CartModel cart = new CartModel();
        cart.setUser(user);

        CartModel newCart = cartRepository.save(cart);

        return newCart;
    }

    public CartModel getUserCart(){
        Optional<CartModel> cart = cartRepository.findByUserId(securityUtils.getUserId());

        if (cart.isEmpty()) {
            return createCart();
        }
        return cart.get();

    }

    public Boolean stockValidate(Long productId, Integer quantity, CartModel cart) {
        ProductModel product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            return false;
        }

        CartItemModel existingItem = cartItemRepository
            .findByCartIdAndProductId(cart.getId(), productId)
            .orElse(null);

        if (existingItem != null) {
            return existingItem.getQuantity() + quantity <= product.getStock();
        }

        return true;
    }

}
