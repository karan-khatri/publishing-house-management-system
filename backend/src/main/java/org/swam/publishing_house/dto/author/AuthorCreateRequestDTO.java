package org.swam.publishing_house.dto.author;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.swam.publishing_house.dto.common.PersonCreateRequestDTO;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorCreateRequestDTO extends PersonCreateRequestDTO {

    @Size(max = 2000, message = "Biography cannot exceed 2000 characters")
    private String biography;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull(message = "Nationality is required")
    @Size(max = 50, message = "Nationality cannot exceed 50 characters")
    private String nationality;

    // Constructors
    public AuthorCreateRequestDTO() {}

    public AuthorCreateRequestDTO(String name, String email, String biography, LocalDate birthDate, String nationality) {
        super(name, email);
        this.biography = biography;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    // Getters and Setters
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}