package org.swam.publishing_house.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
public class AuthorModel extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "birth_date")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    private String nationality;

    // Add the many-to-many relationship
    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<PublicationModel> publications = new HashSet<>();

    // Add transient field for publication count
    @Transient
    @JsonProperty("publicationCount")
    private Long publicationCount;

    // Constructors
    public AuthorModel() {}

    public AuthorModel(String name, String email, String biography, LocalDate birthDate, String nationality) {
        this.name = name;
        this.email = email;
        this.biography = biography;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public Set<PublicationModel> getPublications() { return publications; }
    public void setPublications(Set<PublicationModel> publications) { this.publications = publications; }

    public Long getPublicationCount() { return publicationCount; }
    public void setPublicationCount(Long publicationCount) { this.publicationCount = publicationCount; }
}