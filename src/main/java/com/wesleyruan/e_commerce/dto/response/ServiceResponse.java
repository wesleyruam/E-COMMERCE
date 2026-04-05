package com.wesleyruan.e_commerce.dto.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> {
    private String message;
    private Boolean success;
    private T data;

    @JsonIgnore
    private HttpStatus statusCode;

    public ServiceResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    public ServiceResponse(String message, Boolean success, HttpStatus statusCode) {
        this.message = message;
        this.success = success;
        this.statusCode = statusCode;
    }

    // Success responses
    public static <T> ServiceResponse<T> success(T data) {
        return new ServiceResponse<>("Operation completed successfully.", true, data, HttpStatus.OK);
    }

    public static <T> ServiceResponse<T> success(T data, HttpStatus statusCode) {
        return new ServiceResponse<>("Operation completed successfully.", true, data, statusCode);
    }

    public static <T> ServiceResponse<T> created(T data, String message) {
        return new ServiceResponse<>(message, true, data, HttpStatus.CREATED);
    }

    public static <T> ServiceResponse<T> noContent() {
        return new ServiceResponse<>("No content.", true, null, HttpStatus.NO_CONTENT);
    }

    // Error responses
    public static <T> ServiceResponse<T> error(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.BAD_REQUEST);
    }

    public static <T> ServiceResponse<T> error(String message, HttpStatus statusCode) {
        return new ServiceResponse<>(message, false, statusCode);
    }

    public static <T> ServiceResponse<T> unauthorized(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ServiceResponse<T> forbidden(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.FORBIDDEN);
    }

    public static <T> ServiceResponse<T> notFound(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.NOT_FOUND);
    }

    public static <T> ServiceResponse<T> conflict(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.CONFLICT);
    }

    public static <T> ServiceResponse<T> badRequest(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.BAD_REQUEST);
    }

    public static <T> ServiceResponse<T> internalServerError(String message) {
        return new ServiceResponse<>(message, false, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
