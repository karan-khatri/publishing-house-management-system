package org.swam.publishing_house.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "publications")
public class PublicationModel extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "isbn", unique = true, nullable = false, length = 17)
    private String isbn;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "publication_date")
    @JsonProperty("publicationDate")
    private LocalDate publicationDate;

    @Column(name = "edition", nullable = false)
    private Integer edition = 1;

    @Column(name = "pages")
    private Integer pages;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PublicationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PublicationStatus status = PublicationStatus.DRAFT;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "publication_authors",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorModel> authors = new HashSet<>();

    // Constructors
    public PublicationModel() {}

    public PublicationModel(String title, String isbn, Double price, PublicationType type) {
        this.title = title;
        this.isbn = isbn;
        this.price = price;
        this.type = type;
    }

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

    public Set<AuthorModel> getAuthors() { return authors; }
    public void setAuthors(Set<AuthorModel> authors) { this.authors = authors; }

    public void addAuthor(AuthorModel author) {
        this.authors.add(author);
    }

    public void removeAuthor(AuthorModel author) {
        this.authors.remove(author);
    }
}