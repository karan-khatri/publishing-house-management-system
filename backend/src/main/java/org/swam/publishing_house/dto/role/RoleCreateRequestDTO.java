package org.swam.publishing_house.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class RoleCreateRequestDTO {

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Level is required")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 10, message = "Level must not exceed 10")
    private Integer level;

    // Constructors
    public RoleCreateRequestDTO() {}

    public RoleCreateRequestDTO(String name, String description, Integer level) {
        this.name = name;
        this.description = description;
        this.level = level;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
}