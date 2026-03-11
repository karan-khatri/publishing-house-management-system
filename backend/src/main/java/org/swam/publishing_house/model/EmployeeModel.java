package org.swam.publishing_house.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class EmployeeModel extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

    @Column(name = "employee_id", unique = true, nullable = false, length = 20)
    @JsonProperty("employeeId")
    private String employeeId;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    // Constructors
    public EmployeeModel() {}

    public EmployeeModel(UserModel user, String employeeId, String department, String position) {
        this.user = user;
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
    }

    // Getters and Setters (remove id, createdAt, updatedAt, PrePersist, PreUpdate)
    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }

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