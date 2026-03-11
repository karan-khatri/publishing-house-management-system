package org.swam.publishing_house.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.swam.publishing_house.util.HelperUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PersonCreateRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    protected String name;

    @NotBlank(message = "Email is required")
    @Size(max = 150, message = "Email must be less than 150 characters")
    @Pattern(regexp = HelperUtil.EMAIL_REGEX, message = "Email format is invalid")
    protected String email;

    // Constructors
    public PersonCreateRequestDTO() {}

    public PersonCreateRequestDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}