package org.swam.publishing_house.mapper;

import org.swam.publishing_house.dto.role.RoleResponseDTO;
import org.swam.publishing_house.model.RoleModel;

public class RoleMapper {

    public static RoleResponseDTO toResponseDTO(RoleModel role) {
        if (role == null) {
            return null;
        }

        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getLevel(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}