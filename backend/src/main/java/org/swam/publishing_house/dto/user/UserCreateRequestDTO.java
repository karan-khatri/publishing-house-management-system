package org.swam.publishing_house.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.swam.publishing_house.dto.common.PersonCreateRequestDTO;
import org.swam.publishing_house.util.HelperUtil;

public class UserCreateRequestDTO extends PersonCreateRequestDTO {

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = HelperUtil.PASSWORD_REGEX, message = HelperUtil.PASSWORD_REQUIREMENTS_MESSAGE)
    private String password;

    @NotNull(message = "Role ID is required")
    @Positive(message = "Role ID must be positive")
    private Long roleId;

    // Constructors
    public UserCreateRequestDTO() {}

    public UserCreateRequestDTO(String name, String email, String password, Long roleId) {
        super(name, email);
        this.password = password;
        this.roleId = roleId;
    }

    // Getters and Setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}