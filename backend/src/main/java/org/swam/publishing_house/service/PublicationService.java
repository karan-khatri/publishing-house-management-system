package org.swam.publishing_house.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.swam.publishing_house.dao.AuthorDAO;
import org.swam.publishing_house.dao.PublicationDAO;
import org.swam.publishing_house.dto.publication.PublicationCreateRequestDTO;
import org.swam.publishing_house.dto.publication.PublicationPatchRequestDTO;
import org.swam.publishing_house.dto.publication.PublicationResponseDTO;
import org.swam.publishing_house.mapper.PublicationMapper;
import org.swam.publishing_house.model.AuthorModel;
import org.swam.publishing_house.model.PublicationModel;
import org.swam.publishing_house.util.PatchUtil;
import org.swam.publishing_house.dto.publication.PublicationFilterRequest;
import org.swam.publishing_house.util.PagedResult;
import org.swam.publishing_house.util.PaginationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
@Transactional
public class PublicationService {

    @Inject
    private PublicationDAO publicationDAO;
    
    @Inject
    private AuthorDAO authorDAO;

    // CDI requires a no-arg constructor
    public PublicationService() {
        // Empty constructor for CDI
    }

    /**
     * Create a new publication
     */
    public PublicationResponseDTO createPublication(PublicationCreateRequestDTO request) {
        // Validation
        validatePublicationInput(request);

        if (publicationDAO.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Publication with ISBN " + request.getIsbn() + " already exists");
        }

        // Create publication model
        PublicationModel publication = PublicationMapper.toModel(request);

        // Add authors if provided
        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            Set<AuthorModel> authors = getAuthorsByIds(request.getAuthorIds());
            publication.setAuthors(authors);
        }

        PublicationModel savedPublication = publicationDAO.save(publication);
        return PublicationMapper.toResponseDTO(savedPublication);
    }

    /**
     * Get publication by ID
     */
    public Optional<PublicationResponseDTO> getPublicationById(Long id) {
        return publicationDAO.findById(id).map(PublicationMapper::toResponseDTO);
    }

    /**
     * Delete publication by ID
     */
    public Optional<PublicationResponseDTO> deletePublication(Long id) {
        Optional<PublicationModel> deletedPublication = publicationDAO.deleteById(id);
        return deletedPublication.map(PublicationMapper::toResponseDTO);
    }

    // Private helper methods
    private Set<AuthorModel> getAuthorsByIds(Set<Long> authorIds) {
        Set<AuthorModel> authors = new HashSet<>();
        for (Long authorId : authorIds) {
            AuthorModel author = authorDAO.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
            authors.add(author);
        }
        return authors;
    }

    private void validatePublicationInput(PublicationCreateRequestDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Publication title is required");
        }

        if (request.getIsbn() == null || request.getIsbn().trim().isEmpty()) {
            throw new RuntimeException("ISBN is required");
        }

        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new RuntimeException("Valid price is required");
        }

        if (request.getType() == null) {
            throw new RuntimeException("Publication type is required");
        }

        if (request.getEdition() != null && request.getEdition() < 1) {
            throw new RuntimeException("Edition must be at least 1");
        }

        if (request.getPages() != null && request.getPages() < 1) {
            throw new RuntimeException("Pages must be at least 1");
        }
    }

    private void validatePublicationUpdateInput(PublicationCreateRequestDTO request) {
        validatePublicationInput(request); // Same validation rules for update
    }

    /**
     * Patch publication with only provided fields (excludes ISBN for data integrity)
     */
    public PublicationResponseDTO patchPublication(Long id, PublicationPatchRequestDTO patchDto) {
        // Find existing publication
        PublicationModel existingPublication = publicationDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found"));

        // Apply patch with custom handlers, explicitly exclude system fields
        PatchUtil.patch(patchDto, existingPublication)
                .exclude("id", "isbn") // Exclude ID and ISBN from patching
                .handle("price", this::handlePriceUpdate)
                .handle("authorIds", this::handleAuthorsUpdate)
                .handle("title", this::handleTitleUpdate)
                .apply();

        // Save and return
        PublicationModel updatedPublication = publicationDAO.update(existingPublication);
        return PublicationMapper.toResponseDTO(updatedPublication);
    }

    // Custom handlers for Publication-specific validation
    private void handleTitleUpdate(PublicationPatchRequestDTO patchDto, PublicationModel entity) {
        if (patchDto.getTitle() != null) {
            String trimmedTitle = patchDto.getTitle().trim();
            if (trimmedTitle.isEmpty()) {
                throw new RuntimeException("Title cannot be empty");
            }
            entity.setTitle(trimmedTitle);
        }
    }

    private void handlePriceUpdate(PublicationPatchRequestDTO patchDto, PublicationModel entity) {
        if (patchDto.getPrice() != null) {
            if (patchDto.getPrice() <= 0) {
                throw new RuntimeException("Price must be greater than 0");
            }
            entity.setPrice(patchDto.getPrice());
        }
    }

    private void handleAuthorsUpdate(PublicationPatchRequestDTO patchDto, PublicationModel entity) {
        if (patchDto.getAuthorIds() != null) {
            if (patchDto.getAuthorIds().isEmpty()) {
                entity.getAuthors().clear();
            } else {
                Set<AuthorModel> authors = getAuthorsByIds(patchDto.getAuthorIds());
                entity.setAuthors(authors);
            }
        }
    }

    /**
     * Get publications with filters, pagination and sorting
     */
    public PagedResult<PublicationResponseDTO> getPublicationsWithFilters(PublicationFilterRequest filters) {

        // Validate pagination parameters
        filters.setPage(PaginationUtil.validatePage(filters.getPage()));
        filters.setLimit(PaginationUtil.validateSize(filters.getLimit()));
        filters.setSortDir(PaginationUtil.validateSortDirection(filters.getSortDir()));

        // Validate sort field
        String[] allowedSortFields = {"title", "price", "publicationDate", "createdAt", "updatedAt", "type", "status"};
        filters.setSortBy(PaginationUtil.validateSortField(filters.getSortBy(), allowedSortFields, "createdAt"));

        PagedResult<PublicationModel> pagedResult = publicationDAO.findWithFilters(filters);

        List<PublicationResponseDTO> dtoContent = pagedResult.getContent().stream()
                .map(PublicationMapper::toResponseDTO)
                .toList();

        return new PagedResult<>(dtoContent, pagedResult.getPage(),
                pagedResult.getLimit(), pagedResult.getTotalItems());
    }
}