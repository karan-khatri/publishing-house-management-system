package org.swam.publishing_house.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.dto.user.LoginRequestDTO;
import org.swam.publishing_house.service.AuthService;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthRest {

    @Inject
    private AuthService authService;

    // No-arg constructor for CDI
    public AuthRest() {
        // Empty constructor for CDI
    }

    @POST
    @Path("/login")
    public Response loginUser(@Valid LoginRequestDTO loginRequest) {
        try {
            Map<String, Object> response = authService.authenticate(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                );
            ApiResponseDTO<Map<String, Object>> successResponse = ApiResponseDTO.success(
                    "Login successful",
                    response
            );
            return Response.ok(successResponse).build();
        } catch (RuntimeException e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.unauthorized(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }

    @POST
    @Path("/validate-token")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ApiResponseDTO<Object> error = ApiResponseDTO.unauthorized("Invalid authorization header");
                return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
            }

            String token = authHeader.substring(7);
            boolean isValid = authService.isValidToken(token);

            if (isValid) {
                ApiResponseDTO<Boolean> response = ApiResponseDTO.success("Token is valid", true);
                return Response.ok(response).build();
            } else {
                ApiResponseDTO<Object> error = ApiResponseDTO.unauthorized("Invalid token");
                return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
            }
        } catch (Exception e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.unauthorized("Token validation failed");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }
}