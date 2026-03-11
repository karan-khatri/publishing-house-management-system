package org.swam.publishing_house.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.swam.publishing_house.dao.AuthorDAO;
import org.swam.publishing_house.dto.author.AuthorCreateRequestDTO;
import org.swam.publishing_house.dto.author.AuthorPatchRequestDTO;
import org.swam.publishing_house.dto.author.AuthorResponseDTO;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.mapper.AuthorMapper;
import org.swam.publishing_house.model.AuthorModel;
import org.swam.publishing_house.util.HelperUtil;
import org.swam.publishing_house.util.PatchUtil;
import org.swam.publishing_house.dto.author.AuthorFilterRequestDTO;
import org.swam.publishing_house.util.PagedResult;
import org.swam.publishing_house.util.PaginationUtil;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class AuthorService {

    @Inject
    private AuthorDAO authorDAO;

    // CDI requires a no-arg constructor
    public AuthorService() {
        // Empty constructor for CDI
    }

    /**
     * Register a new author
     */
    public AuthorResponseDTO createAuthor(AuthorCreateRequestDTO request) {
        // Validation
        validateAuthorInput(request);

        if (authorDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Author with email " + request.getEmail() + " already exists");
        }

        // Create author model
        AuthorModel author = AuthorMapper.toEntity(request);
        AuthorModel savedAuthor = authorDAO.save(author);

        return AuthorMapper.toResponseDTO(savedAuthor);
    }

    /**
     * Get author by ID
     */
    public Optional<AuthorResponseDTO> getAuthorById(Long id) {
        return authorDAO.findById(id).map(AuthorMapper::toResponseDTO);
    }


    /**
     * Delete author by ID and return the deleted author
     */
    public Optional<AuthorResponseDTO> deleteAuthor(Long id) {
        Optional<AuthorModel> deletedAuthor = authorDAO.deleteById(id);
        return deletedAuthor.map(AuthorMapper::toResponseDTO);
    }

    // Private helper methods
    private void validateAuthorInput(AuthorCreateRequestDTO request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Author name is required");
        }

        if (!HelperUtil.isValidEmail(request.getEmail())) {
            throw new RuntimeException("Invalid email format");
        }
    }

    /**
     * Patch author with only provided fields
     */
    public AuthorResponseDTO patchAuthor(Long id, AuthorPatchRequestDTO patchDto) {
        // Find existing author
        AuthorModel existingAuthor = authorDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        // Apply patch with custom handlers
        PatchUtil.patch(patchDto, existingAuthor)
                .handle("email", this::handleEmailUpdate)
                .handle("name", this::handleNameUpdate)
                .apply();

        // Save and return
        AuthorModel updatedAuthor = authorDAO.update(existingAuthor);
        return AuthorMapper.toResponseDTO(updatedAuthor);
    }

    // Custom handlers for special validation
    private void handleEmailUpdate(AuthorPatchRequestDTO patchDto, AuthorModel entity) {
        if (patchDto.getEmail() != null) {
            // Validate email format
            if (!HelperUtil.isValidEmail(patchDto.getEmail())) {
                throw new RuntimeException("Invalid email format");
            }

            // Check email uniqueness if email is being changed
            if (!entity.getEmail().equals(patchDto.getEmail()) &&
                    authorDAO.existsByEmail(patchDto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            entity.setEmail(patchDto.getEmail());
        }
    }

    private void handleNameUpdate(AuthorPatchRequestDTO patchDto, AuthorModel entity) {
        if (patchDto.getName() != null) {
            if (patchDto.getName().trim().isEmpty()) {
                throw new RuntimeException("Name cannot be empty");
            }
            entity.setName(patchDto.getName().trim());
        }
    }

    /**
     * Get authors with filters, pagination and sorting
     */
    public PagedResult<AuthorResponseDTO> getAuthorsWithFilters(BaseFiltersDTO filters) {

        // Validate pagination parameters
        filters.setPage(PaginationUtil.validatePage(filters.getPage()));
        filters.setLimit(PaginationUtil.validateSize(filters.getLimit()));
        filters.setSortDir(PaginationUtil.validateSortDirection(filters.getSortDir()));

        // Validate sort field
        String[] allowedSortFields = {"name", "email", "nationality", "createdAt", "updatedAt"};
        filters.setSortBy(PaginationUtil.validateSortField(filters.getSortBy(), allowedSortFields, "createdAt"));

        PagedResult<AuthorModel> pagedResult = authorDAO.findWithFilters(filters);

        List<AuthorResponseDTO> dtoContent = pagedResult.getContent().stream()
                .map(AuthorMapper::toResponseDTO)
                .toList();

        return new PagedResult<>(dtoContent, pagedResult.getPage(),
                pagedResult.getLimit(), pagedResult.getTotalItems());
    }
}