package org.swam.publishing_house.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.dto.publication.PublicationCreateRequestDTO;
import org.swam.publishing_house.dto.publication.PublicationPatchRequestDTO;
import org.swam.publishing_house.dto.publication.PublicationResponseDTO;
import org.swam.publishing_house.model.PublicationStatus;
import org.swam.publishing_house.model.PublicationType;
import org.swam.publishing_house.security.JWTTokenNeeded;
import org.swam.publishing_house.service.PublicationService;
import org.swam.publishing_house.dto.publication.PublicationFilterRequest;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Path("/publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicationRest {

    @Inject
    private PublicationService publicationService;

    // No-arg constructor for CDI
    public PublicationRest() {
        // Empty constructor for CDI
    }

    /**
     * Create a new publication
     */
    @POST
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER", "EDITOR"})
    public Response createPublication(@Valid PublicationCreateRequestDTO request) {
        try {
            PublicationResponseDTO publication = publicationService.createPublication(request);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponseDTO.created("Publication created successfully", publication))
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponseDTO.badRequest(e.getMessage()))
                    .build();
        }
    }

    /**
     * Get publication by ID
     */
    @GET
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"USER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getPublicationById(@PathParam("id") Long id) {
        Optional<PublicationResponseDTO> publication = publicationService.getPublicationById(id);
        if (publication.isPresent()) {
            return Response.ok(ApiResponseDTO.success("Publication retrieved successfully", publication.get()))
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponseDTO.notFound("Publication not found"))
                .build();
    }

    /**
     * Get publications with filters, pagination and sorting
     */
    @GET
    @JWTTokenNeeded(roles = {"USER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getPublicationsWithFilters(@QueryParam("query") String query,
                                               @QueryParam("title") String title,
                                               @QueryParam("type") String type,
                                               @QueryParam("isbn") String isbn,
                                               @QueryParam("status") String status,
                                               @QueryParam("authorId") Long authorId,
                                               @QueryParam("minPrice") Double minPrice,
                                               @QueryParam("maxPrice") Double maxPrice,
                                               @QueryParam("createdAfter") String createdAfter,
                                               @QueryParam("createdBefore") String createdBefore,
                                               @QueryParam("page") @DefaultValue("0") int page,
                                               @QueryParam("limit") @DefaultValue("20") int limit,
                                               @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
                                               @QueryParam("sortDir") @DefaultValue("desc") String sortDir) {
        try {
            // Build filter request
            PublicationFilterRequest filters = new PublicationFilterRequest(query, page, limit, sortBy, sortDir, title, isbn, authorId, minPrice, maxPrice);


            // Parse enums
            if (type != null && !type.trim().isEmpty()) {
                try {
                    filters.setType(PublicationType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponseDTO.badRequest("Invalid publication type"))
                            .build();
                }
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    filters.setStatus(PublicationStatus.valueOf(status.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponseDTO.badRequest("Invalid publication status"))
                            .build();
                }
            }

            // Parse dates
            if (createdAfter != null && !createdAfter.trim().isEmpty()) {
                try {
                    filters.setCreatedAfter(LocalDate.parse(createdAfter));
                } catch (Exception e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponseDTO.badRequest("Invalid createdAfter date format. Use yyyy-MM-dd"))
                            .build();
                }
            }

            if (createdBefore != null && !createdBefore.trim().isEmpty()) {
                try {
                    filters.setCreatedBefore(LocalDate.parse(createdBefore));
                } catch (Exception e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(ApiResponseDTO.badRequest("Invalid createdBefore date format. Use yyyy-MM-dd"))
                            .build();
                }
            }


            PagedResult<PublicationResponseDTO> result = publicationService.getPublicationsWithFilters(filters);

            return Response.ok(ApiResponseDTO.success("Publications retrieved successfully", result))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponseDTO.internalServerError(e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete publication
     */
    @DELETE
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response deletePublication(@PathParam("id") Long id) {
        try {
            Optional<PublicationResponseDTO> deletedPublication = publicationService.deletePublication(id);
            if (deletedPublication.isPresent()) {
                return Response.status(Response.Status.OK)
                        .entity(ApiResponseDTO.success("Publication deleted successfully", deletedPublication.get()))
                        .build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponseDTO.notFound("Publication not found"))
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponseDTO.internalServerError("Error deleting publication: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Patch publication - update only provided fields (excludes ISBN)
     */
    @PATCH
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER", "EDITOR"})
    public Response patchPublication(@PathParam("id") Long id, @Valid PublicationPatchRequestDTO request) {
        try {
            PublicationResponseDTO updatedPublication = publicationService.patchPublication(id, request);
            return Response.ok(ApiResponseDTO.success("Publication updated successfully", updatedPublication))
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponseDTO.notFound(e.getMessage()))
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponseDTO.badRequest(e.getMessage()))
                    .build();
        }
    }
}