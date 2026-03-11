package org.swam.publishing_house.dto.publication;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.model.PublicationType;
import org.swam.publishing_house.model.PublicationStatus;

import java.time.LocalDate;

public class PublicationFilterRequest extends BaseFiltersDTO {

    private String title;
    private String isbn;
    private PublicationType type;
    private PublicationStatus status;
    private Long authorId;
    private Double minPrice;
    private Double maxPrice;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAfter;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdBefore;

    public PublicationFilterRequest() {}

    public PublicationFilterRequest(String query, int page, int limit, String sortBy, String sortDir, String title, String isbn, Long authorId, Double minPrice, Double maxPrice) {
        this.setQuery(query);
        this.setPage(page);
        this.setLimit(limit);
        this.setSortBy(sortBy);
        this.setSortDir(sortDir);
        this.title = title;
        this.isbn = isbn;
        this.authorId = authorId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public PublicationType getType() { return type; }
    public void setType(PublicationType type) { this.type = type; }

    public PublicationStatus getStatus() { return status; }
    public void setStatus(PublicationStatus status) { this.status = status; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public LocalDate getCreatedAfter() { return createdAfter; }
    public void setCreatedAfter(LocalDate createdAfter) { this.createdAfter = createdAfter; }

    public LocalDate getCreatedBefore() { return createdBefore; }
    public void setCreatedBefore(LocalDate createdBefore) { this.createdBefore = createdBefore; }
}