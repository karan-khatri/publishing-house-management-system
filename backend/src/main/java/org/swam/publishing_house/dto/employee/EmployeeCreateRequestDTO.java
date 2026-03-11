package org.swam.publishing_house.dto.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.swam.publishing_house.util.HelperUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeCreateRequestDTO {

    // User fields
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Size(max = 150, message = "Email must be less than 150 characters")
    @Pattern(regexp = HelperUtil.EMAIL_REGEX, message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = HelperUtil.PASSWORD_REGEX, message = HelperUtil.PASSWORD_REQUIREMENTS_MESSAGE)
    private String password;

    @NotNull(message = "Role ID is required")
    @Positive(message = "Role ID must be positive")
    private Long roleId;

    // Employee fields
    @NotBlank(message = "Employee ID is required")
    @Size(max = 20, message = "Employee ID must be less than 20 characters")
    private String employeeId;

    @Size(max = 50, message = "Department must be less than 50 characters")
    private String department;

    @Size(max = 100, message = "Position must be less than 100 characters")
    private String position;

    @Size(max = 15, message = "Phone must be less than 15 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Invalid phone number format")
    private String phone;

    private String address;

    // Constructors
    public EmployeeCreateRequestDTO() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}