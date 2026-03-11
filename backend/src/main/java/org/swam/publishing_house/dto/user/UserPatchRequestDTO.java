package org.swam.publishing_house.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPatchRequestDTO {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Positive(message = "Role ID must be positive")
    private Long roleId;

    // Note: Password is intentionally excluded from patch DTO for security
    // Use dedicated change password endpoint instead

    // Constructors
    public UserPatchRequestDTO() {}

    public UserPatchRequestDTO(String name, String email, Long roleId) {
        this.name = name;
        this.email = email;
        this.roleId = roleId;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}