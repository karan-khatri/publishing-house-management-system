package org.swam.publishing_house.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.swam.publishing_house.dto.common.ApiResponseDTO;

import java.util.HashMap;
import java.util.Map;

@Provider
public class ErrorHandlerUtil implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> fieldErrors = new HashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String fieldName = getFieldName(violation.getPropertyPath().toString());
            String errorMessage = violation.getMessage();

            // If field already has an error, combine them (or keep the first one)
            fieldErrors.putIfAbsent(fieldName, errorMessage);
        }

        ApiResponseDTO<Map<String, String>> error = ApiResponseDTO.validationError(fieldErrors);

        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    /**
     * Extract the field name from the property path
     * Handles paths like "methodName.parameterName.fieldName" or just "fieldName"
     */
    private String getFieldName(String propertyPath) {
        if (propertyPath == null || propertyPath.isEmpty()) {
            return "unknown";
        }

        // Split by dots and get the last part (the actual field name)
        String[] parts = propertyPath.split("\\.");
        return parts[parts.length - 1];
    }
}