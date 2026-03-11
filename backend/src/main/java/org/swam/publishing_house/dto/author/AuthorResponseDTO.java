package org.swam.publishing_house.dto.author;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.swam.publishing_house.dto.common.PersonResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AuthorResponseDTO extends PersonResponseDTO {

    private String biography;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String nationality;

    private Integer publicationCount = 0;

    // Constructors
    public AuthorResponseDTO() {}

    public AuthorResponseDTO(Long id, String name, String email, String biography, LocalDate birthDate,
                             String nationality, LocalDateTime createdAt, LocalDateTime updatedAt, Integer publicationCount) {
        super(id, name, email, createdAt, updatedAt);
        this.biography = biography;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.publicationCount = publicationCount;
    }

    // Getters and Setters
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public Integer getPublicationCount() { return publicationCount; }
    public void setPublicationCount(Integer publicationCount) { this.publicationCount = publicationCount; }
}