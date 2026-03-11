package org.swam.publishing_house.dto.publication;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.swam.publishing_house.dto.author.AuthorResponseDTO;
import org.swam.publishing_house.model.PublicationType;
import org.swam.publishing_house.model.PublicationStatus;

import java.time.LocalDate;
import java.util.Set;

public class PublicationResponseDTO {

    private Long id;
    private String title;
    private String isbn;
    private String description;
    private Double price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private Integer edition;
    private Integer pages;
    private PublicationType type;
    private PublicationStatus status;
    private Set<AuthorResponseDTO> authors;

    // Constructors
    public PublicationResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public Integer getEdition() { return edition; }
    public void setEdition(Integer edition) { this.edition = edition; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public PublicationType getType() { return type; }
    public void setType(PublicationType type) { this.type = type; }

    public PublicationStatus getStatus() { return status; }
    public void setStatus(PublicationStatus status) { this.status = status; }

    public Set<AuthorResponseDTO> getAuthors() { return authors; }
    public void setAuthors(Set<AuthorResponseDTO> authors) { this.authors = authors; }
}