package org.swam.publishing_house.dto.author;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;

import java.time.LocalDate;

public class AuthorFilterRequestDTO extends BaseFiltersDTO {

    private String name;
    private String email;
    private String nationality;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAfter;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdBefore;

    public AuthorFilterRequestDTO() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}