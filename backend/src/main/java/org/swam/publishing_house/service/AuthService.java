package org.swam.publishing_house.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.swam.publishing_house.dto.user.UserResponseDTO;
import org.swam.publishing_house.security.JWTUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class AuthService {

    @Inject
    private UserService userService;

    // CDI requires a no-arg constructor
    public AuthService() {
        // Empty constructor for CDI
    }

    /**
     * Authenticate user and return token with user info
     */
    public Map<String, Object> authenticate(String email, String password) {
        Optional<UserResponseDTO> userOpt = userService.loginUser(email, password);

        if (!userOpt.isPresent()) {
            throw new RuntimeException("Invalid credentials");
        }

        UserResponseDTO user = userOpt.get();
        String token = JWTUtil.generateToken(user.getEmail(), user.getRole().getName());

        // Return minimal response - just token and user data
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }

    /**
     * Validate token
     */
    public boolean isValidToken(String token) {
        return JWTUtil.validateToken(token);
    }
}