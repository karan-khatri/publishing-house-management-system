package org.swam.publishing_house.dto.publication;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.swam.publishing_house.model.PublicationType;
import org.swam.publishing_house.model.PublicationStatus;

import java.time.LocalDate;
import java.util.Set;

public class PublicationCreateRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:978|979)[0-9]{10}$",
            message = "Invalid ISBN format")
    private String isbn;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99")
    private Double price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    @Min(value = 1, message = "Edition must be at least 1")
    private Integer edition = 1;

    @Min(value = 1, message = "Pages must be at least 1")
    private Integer pages;

    @NotNull(message = "Publication type is required")
    private PublicationType type;

    private PublicationStatus status = PublicationStatus.DRAFT;

    private Set<Long> authorIds;

    // Constructors
    public PublicationCreateRequestDTO() {}

    // Getters and Setters
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

    public Set<Long> getAuthorIds() { return authorIds; }
    public void setAuthorIds(Set<Long> authorIds) { this.authorIds = authorIds; }
}