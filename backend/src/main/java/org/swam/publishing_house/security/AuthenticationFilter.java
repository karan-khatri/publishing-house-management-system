package org.swam.publishing_house.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.service.RoleService;
import org.swam.publishing_house.model.RoleModel;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private RoleService roleService;

    // No-arg constructor for CDI
    public AuthenticationFilter() {
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        try {
            // Extract and validate token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new Exception("Missing or invalid Authorization header");
            }

            String token = authHeader.substring("Bearer ".length()).trim();

            if (!JWTUtil.validateToken(token)) {
                throw new Exception("Invalid token");
            }

            // Get method and check permissions
            Method method = resourceInfo.getResourceMethod();
            if (method != null) {
                JWTTokenNeeded jwtContext = method.getAnnotation(JWTTokenNeeded.class);
                if (jwtContext == null) {
                    // Check class level annotation
                    jwtContext = resourceInfo.getResourceClass().getAnnotation(JWTTokenNeeded.class);
                }

                if (jwtContext != null) {
                    String[] requiredRoles = jwtContext.roles();

                    // Get role from JWT
                    String roleString = JWTUtil.getRoleFromToken(token);
                    Optional<RoleModel> userRoleOpt = roleService.getRoleByName(roleString);

                    if (!userRoleOpt.isPresent()) {
                        throw new Exception("Invalid user role");
                    }

                    RoleModel userRole = userRoleOpt.get();

                    // Check if user has required role or level
                    if (!hasRequiredPermission(userRole, requiredRoles)) {
                        throw new Exception("Insufficient permissions");
                    }
                }
            }

            // Set security context for other parts of the app
            String email = JWTUtil.getEmailFromToken(token);
            String role = JWTUtil.getRoleFromToken(token);
            SecurityContext.setCurrentUser(email, role);

        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ApiResponseDTO.unauthorized("Access denied: " + e.getMessage()))
                            .build()
            );
        }
    }

    private boolean hasRequiredPermission(RoleModel userRole, String[] requiredRoles) {
        // Check by role name
        if (Arrays.asList(requiredRoles).contains(userRole.getName())) {
            return true;
        }
        return false;
    }
}