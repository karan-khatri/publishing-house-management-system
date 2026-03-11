package org.swam.publishing_house.dto.user;

import org.swam.publishing_house.dto.common.PersonResponseDTO;
import org.swam.publishing_house.dto.role.RoleResponseDTO;
import org.swam.publishing_house.model.RoleModel;

import java.time.LocalDateTime;

public class UserResponseDTO extends PersonResponseDTO {

    private RoleResponseDTO role;

    // Constructors
    public UserResponseDTO() {}

    public UserResponseDTO(Long id, RoleResponseDTO role, String name, String email,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, name, email, createdAt, updatedAt);
        this.role = role;
    }

    public RoleResponseDTO getRole() { return role; }
    public void setRole(RoleResponseDTO role) { this.role = role; }

}