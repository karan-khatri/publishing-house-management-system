package org.swam.publishing_house.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private Object error;
    private Object metadata;

    // Default constructor
    public ApiResponseDTO() {}

    // Constructor with success flag and message
    public ApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Constructor with success flag, message, and data/error
    public ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        if (success) {
            this.data = data;
        } else {
            this.error = data;
        }
    }

    // Constructor with all fields
    public ApiResponseDTO(boolean success, String message, T data, Object metadata) {
        this.success = success;
        this.message = message;
        this.metadata = metadata;
        if (success) {
            this.data = data;
        } else {
            this.error = data;
        }
    }

    // Static factory methods for success responses
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static <T> ApiResponseDTO<T> created(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static ApiResponseDTO<Void> ok(String message) {
        return new ApiResponseDTO<>(true, message);
    }

    public static ApiResponseDTO<Void> noContent(String message) {
        return new ApiResponseDTO<>(true, message);
    }

    // Static factory methods for error responses
    public static ApiResponseDTO<Object> error(String message, Object errorDetails) {
        return new ApiResponseDTO<>(false, message, errorDetails);
    }

    public static ApiResponseDTO<Map<String, String>> validationError(Map<String, String> validationMessages) {
        return new ApiResponseDTO<>(false, "Request validation failed", validationMessages);
    }

    public static ApiResponseDTO<Object> badRequest(String message) {
        return new ApiResponseDTO<>(false, message);
    }

    public static ApiResponseDTO<Object> unauthorized(String message) {
        return new ApiResponseDTO<>(false, message);
    }

    public static ApiResponseDTO<Object> forbidden(String message) {
        return new ApiResponseDTO<>(false, message);
    }

    public static ApiResponseDTO<Object> notFound(String message) {
        return new ApiResponseDTO<>(false, message);
    }

    public static ApiResponseDTO<Object> internalServerError(String message) {
        return new ApiResponseDTO<>(false, message);
    }

    public static ApiResponseDTO<Object> conflict(String message) { return new ApiResponseDTO<>(false, message);}

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
}