package org.swam.publishing_house.dto.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.swam.publishing_house.dto.user.UserPatchRequestDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeePatchRequestDTO extends UserPatchRequestDTO {

    @Size(max = 20, message = "Employee ID cannot exceed 20 characters")
    private String employeeId;

    @Size(max = 50, message = "Department cannot exceed 50 characters")
    private String department;

    @Size(max = 100, message = "Position cannot exceed 100 characters")
    private String position;

    @Size(max = 15, message = "Phone cannot exceed 15 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Invalid phone number format")
    private String phone;

    private String address;

    // Constructors
    public EmployeePatchRequestDTO() {}

    // Getters and Setters
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