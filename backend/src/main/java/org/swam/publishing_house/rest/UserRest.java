package org.swam.publishing_house.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.dto.user.UserCreateRequestDTO;
import org.swam.publishing_house.dto.user.UserPatchRequestDTO;
import org.swam.publishing_house.dto.user.UserResponseDTO;
import org.swam.publishing_house.dto.user.ChangePasswordRequestDTO;
import org.swam.publishing_house.security.JWTTokenNeeded;
import org.swam.publishing_house.security.SecurityContext;
import org.swam.publishing_house.service.UserService;
import org.swam.publishing_house.util.HelperUtil;

import java.util.List;
import java.util.Optional;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRest {

    @Inject private UserService userService;

    // No-arg constructor for CDI
    public UserRest() {
        // Empty constructor for CDI
    }

    @GET
    @Path("/me")
    @JWTTokenNeeded(roles = {"USER", "REVIEWER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getCurrentUser() {
        String email = SecurityContext.getCurrentUserEmail();

        if (email == null) {
            ApiResponseDTO<Object> error = ApiResponseDTO.unauthorized("User not authenticated");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }

        Optional<UserResponseDTO> user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.success("User retrieved successfully", user.get());
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("User not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    /**
     * Create a new user (Admin only)
     * POST /api/users
     */
    @POST
    // @JWTTokenNeeded(roles = {"ADMIN"})
    public Response createUser(@Valid UserCreateRequestDTO request) {
        try {
            UserResponseDTO createdUser = userService.registerUser(request);
            ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.success(
                    "User created successfully", 
                    createdUser
            );
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            // Handle specific error cases
            if (e.getMessage().contains("Email already exists")) {
                ApiResponseDTO<Object> error = ApiResponseDTO.badRequest("Email already exists");
                return Response.status(Response.Status.CONFLICT).entity(error).build();
            } else if (e.getMessage().contains("Invalid role ID")) {
                ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            } else {
                ApiResponseDTO<Object> error = ApiResponseDTO.internalServerError("Failed to create user: " + e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
            }
        }
    }

    @GET
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        ApiResponseDTO<List<UserResponseDTO>> response = ApiResponseDTO.success("Users retrieved successfully", users);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"USER", "REVIEWER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getUserById(@PathParam("id") Long id) {
        String currentUserEmail = SecurityContext.getCurrentUserEmail();
        String currentUserRole = SecurityContext.getCurrentUserRole();

        Optional<UserResponseDTO> user = userService.getUserById(id);

        if (user.isPresent()) {
            UserResponseDTO foundUser = user.get();

            // Users can only access their own data, admins can access any
            if (!"ADMIN".equals(currentUserRole) &&
                    !foundUser.getEmail().equals(currentUserEmail)) {
                ApiResponseDTO<Object> error = ApiResponseDTO.forbidden("Access denied");
                return Response.status(Response.Status.FORBIDDEN).entity(error).build();
            }

            ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.success("User retrieved successfully", foundUser);
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("User not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    @GET
    @Path("/role/{roleId}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getUsersByRoleId(@PathParam("roleId") Long roleId) {
        try {
            List<UserResponseDTO> users = userService.getUsersByRoleId(roleId);
            ApiResponseDTO<List<UserResponseDTO>> response = ApiResponseDTO.success(
                    "Users retrieved successfully", users);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @GET
    @Path("/role/name/{roleName}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getUsersByRoleName(@PathParam("roleName") String roleName) {
        try {
            List<UserResponseDTO> users = userService.getUsersByRoleName(roleName);
            ApiResponseDTO<List<UserResponseDTO>> response = ApiResponseDTO.success(
                    "Users retrieved successfully", users);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            ApiResponseDTO<Void> response = ApiResponseDTO.noContent("User deleted successfully");
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("User not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    @PUT
    @Path("/change-password")
    @JWTTokenNeeded(roles = {"USER", "REVIEWER", "EDITOR", "MANAGER", "ADMIN"})
    public Response changePassword(@Valid ChangePasswordRequestDTO request) {
        String email = SecurityContext.getCurrentUserEmail();

        // Check if the new password is different from the old password
        if (request.getOldPassword().equals(request.getNewPassword())) {
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest("New password must be different from current password");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
            boolean success = userService.changePassword(
                    email,
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            if (success) {
                ApiResponseDTO<Void> response = ApiResponseDTO.ok("Password changed successfully");
                return Response.ok(response).build();
            } else {
                ApiResponseDTO<Object> error = ApiResponseDTO.badRequest("Invalid current password");
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            }
        } catch (RuntimeException e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    /**
     * Patch user - update only provided fields (excludes password)
     */
    @PATCH
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"USER", "REVIEWER", "EDITOR", "MANAGER", "ADMIN"})
    public Response patchUser(@PathParam("id") Long id, @Valid UserPatchRequestDTO request) {
        String currentUserEmail = SecurityContext.getCurrentUserEmail();
        String currentUserRole = SecurityContext.getCurrentUserRole();

        // Check if user exists and get access permissions
        Optional<UserResponseDTO> existingUser = userService.getUserById(id);
        if (!existingUser.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponseDTO.notFound("User not found"))
                    .build();
        }

        // Users can only update their own data, admins can update any
        if (!"ADMIN".equals(currentUserRole) &&
                !existingUser.get().getEmail().equals(currentUserEmail)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(ApiResponseDTO.forbidden("Access denied"))
                    .build();
        }

        try {
            UserResponseDTO updatedUser = userService.patchUser(id, request);
            return Response.ok(ApiResponseDTO.success("User updated successfully", updatedUser))
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponseDTO.notFound(e.getMessage()))
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponseDTO.badRequest(e.getMessage()))
                    .build();
        }
    }
}