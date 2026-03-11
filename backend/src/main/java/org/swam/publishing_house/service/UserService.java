package org.swam.publishing_house.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.swam.publishing_house.dao.UserDAO;
import org.swam.publishing_house.dao.RoleDAO;
import org.swam.publishing_house.dto.user.UserCreateRequestDTO;
import org.swam.publishing_house.dto.user.UserPatchRequestDTO;
import org.swam.publishing_house.dto.user.UserResponseDTO;
import org.swam.publishing_house.mapper.UserMapper;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.model.RoleModel;
import org.swam.publishing_house.security.PasswordUtil;
import org.swam.publishing_house.util.HelperUtil;
import org.swam.publishing_house.util.PatchUtil;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class UserService {

    @Inject
    private UserDAO userDAO;
    
    @Inject
    private RoleDAO roleDAO;

    // CDI requires a no-arg constructor
    public UserService() {
        // Empty constructor for CDI
    }

    public UserResponseDTO registerUser(UserCreateRequestDTO request) {
        // Check if email already exists
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validate role exists
        Optional<RoleModel> roleOpt = roleDAO.findById(request.getRoleId());

        if (!roleOpt.isPresent()) {
            throw new RuntimeException("Invalid role ID: " + request.getRoleId());
        }



        // Create user
        UserModel user = new UserModel();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        user.setRole(roleOpt.get());

        // Save user
        UserModel savedUser = userDAO.save(user);

        return UserMapper.toResponseDTO(savedUser);
    }

    /**
     * Authenticate user login
     */
    public Optional<UserResponseDTO> loginUser(String email, String password) {
        if (!HelperUtil.isValidEmail(email)) {
            return Optional.empty();
        }

        return userDAO.findByEmail(email)
                .filter(user -> PasswordUtil.verifyPassword(password, user.getPassword()))
                .map(UserMapper::toResponseDTO);
    }

    /**
     * Get user by ID
     */
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userDAO.findById(id).map(UserMapper::toResponseDTO);
    }

    /**
     * Find user by email
     */
    public Optional<UserResponseDTO> findUserByEmail(String email) {
        return userDAO.findByEmail(email).map(UserMapper::toResponseDTO);
    }

    /**
     * Get all users
     */
    public List<UserResponseDTO> getAllUsers() {
        return userDAO.findAll().stream()
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    /**
     * Get users by role ID
     */
    public List<UserResponseDTO> getUsersByRoleId(Long roleId) {
        RoleModel role = roleDAO.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return userDAO.findByRole(role).stream()
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    /**
     * Get users by role name
     */
    public List<UserResponseDTO> getUsersByRoleName(String roleName) {
        RoleModel role = roleDAO.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

        return userDAO.findByRole(role).stream()
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    /**
     * Delete user by ID
     */
    public boolean deleteUser(Long id) {
        if (!userDAO.findById(id).isPresent()) {
            return false;
        }
        return userDAO.deleteById(id);
    }

    /**
     * Change user password
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        if (!HelperUtil.isValidPassword(newPassword)) {
            throw new RuntimeException("Invalid new password format. " + HelperUtil.PASSWORD_REQUIREMENTS_MESSAGE);
        }

        Optional<UserModel> userOpt = userDAO.findByEmail(email);
        if (!userOpt.isPresent()) {
            return false;
        }

        UserModel user = userOpt.get();
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userDAO.update(user);
        return true;
    }

    // Add this method to your existing UserService class

    /**
     * Patch user with only provided fields (excludes password for security)
     */
    public UserResponseDTO patchUser(Long id, UserPatchRequestDTO patchDto) {
        // Find existing user
        UserModel existingUser = userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Apply patch with custom handlers, explicitly exclude sensitive fields
        PatchUtil.patch(patchDto, existingUser)
                .exclude("password", "createdAt", "updatedAt") // Exclude sensitive/system fields
                .handle("email", this::handleEmailUpdate)
                .handle("roleId", this::handleRoleUpdate)
                .apply();

        // Save and return
        UserModel updatedUser = userDAO.update(existingUser);
        return UserMapper.toResponseDTO(updatedUser);
    }

    // Custom handlers for User-specific validation
    private void handleEmailUpdate(UserPatchRequestDTO patchDto, UserModel entity) {
        if (patchDto.getEmail() != null) {
            // Validate email format
            if (!HelperUtil.isValidEmail(patchDto.getEmail())) {
                throw new RuntimeException("Invalid email format");
            }

            // Check email uniqueness if email is being changed
            if (!entity.getEmail().equals(patchDto.getEmail()) &&
                    userDAO.existsByEmail(patchDto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            entity.setEmail(patchDto.getEmail());
        }
    }

    private void handleRoleUpdate(UserPatchRequestDTO patchDto, UserModel entity) {
        if (patchDto.getRoleId() != null) {
            // Validate role exists
            RoleModel role = roleDAO.findById(patchDto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + patchDto.getRoleId()));

            entity.setRole(role);
        }
    }
}