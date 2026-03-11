package org.swam.publishing_house.mapper;

import org.swam.publishing_house.dto.employee.EmployeeCreateRequestDTO;
import org.swam.publishing_house.dto.employee.EmployeePatchRequestDTO;
import org.swam.publishing_house.dto.employee.EmployeeResponseDTO;
import org.swam.publishing_house.dto.user.UserCreateRequestDTO;
import org.swam.publishing_house.dto.user.UserPatchRequestDTO;
import org.swam.publishing_house.model.EmployeeModel;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.model.RoleModel;

public class EmployeeMapper {

    // Convert EmployeeModel to EmployeeResponseDTO (for API responses)
    public static EmployeeResponseDTO toResponseDTO(EmployeeModel employee) {
        if (employee == null) {
            return null;
        }

        return new EmployeeResponseDTO(
                employee.getId(),
                UserMapper.toResponseDTO(employee.getUser()),
                employee.getEmployeeId(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getPhone(),
                employee.getAddress(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    // Convert EmployeeCreateRequestDTO to UserCreateRequestDTO
    public static UserCreateRequestDTO toUserCreateRequestDTO(EmployeeCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return new UserCreateRequestDTO(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getRoleId()
        );
    }

    public static UserPatchRequestDTO toUserPatchRequestDTO(EmployeePatchRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return new UserPatchRequestDTO(
                dto.getName(),
                dto.getEmail(),
                dto.getRoleId()
        );
    }

    // Convert EmployeeCreateRequestDTO to EmployeeModel (for creating new employees)
    public static EmployeeModel toEntity(EmployeeCreateRequestDTO dto, UserModel user) {
        if (dto == null) {
            return null;
        }

        EmployeeModel employee = new EmployeeModel();
        employee.setUser(user);
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());

        return employee;
    }

    // Update existing EmployeeModel with data from EmployeeCreateRequestDTO
    public static void updateEntityFromDTO(EmployeeModel employee, EmployeePatchRequestDTO dto, UserModel user) {
        if (employee == null || dto == null) {
            return;
        }

        if (user != null) {
            employee.setUser(user);
        }
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());
    }
}