package com.wesleyruan.e_commerce.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authorization.AuthorizationDeniedException;

import com.wesleyruan.e_commerce.dto.response.ServiceResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ServiceResponse<Void>> handleNotFound(NotFoundException ex) {
        ServiceResponse<Void> response = ServiceResponse.notFound(ex.getMessage());
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ServiceResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        ServiceResponse<Void> response = ServiceResponse.unauthorized(ex.getMessage());
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ServiceResponse<Void>> handleBadRequest(BadRequestException ex) {
        ServiceResponse<Void> response = ServiceResponse.badRequest(ex.getMessage());
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceResponse<Void>> handleGeneric(Exception ex) {
        ServiceResponse<Void> response = ServiceResponse.internalServerError("Unexpected error");
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ServiceResponse<Void>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ServiceResponse<Void> response = ServiceResponse.forbidden("Access Denied");
        return new ResponseEntity<>(response, response.getStatusCode());
    }
}
