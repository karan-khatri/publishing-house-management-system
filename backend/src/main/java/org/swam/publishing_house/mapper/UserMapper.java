package org.swam.publishing_house.mapper;

import org.swam.publishing_house.dto.role.RoleResponseDTO;
import org.swam.publishing_house.dto.user.UserCreateRequestDTO;
import org.swam.publishing_house.dto.user.UserResponseDTO;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.model.RoleModel;

public class UserMapper {

    // Convert UserModel to UserResponseDTO (for API responses)
    public static UserResponseDTO toResponseDTO(UserModel user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO(
                user.getId(),
                RoleMapper.toResponseDTO(user.getRole()),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Convert UserCreateRequestDTO to UserModel (for creating new users)
    public static UserModel toEntity(UserCreateRequestDTO dto, RoleModel role) {
        if (dto == null) {
            return null;
        }

        UserModel user = new UserModel();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(role);

        return user;
    }

    // Update existing UserModel with data from UserCreateRequestDTO
    public static void updateEntityFromDTO(UserModel user, UserCreateRequestDTO dto, RoleModel role) {
        if (user == null || dto == null) {
            return;
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        if (role != null) {
            user.setRole(role);
        }
    }
}