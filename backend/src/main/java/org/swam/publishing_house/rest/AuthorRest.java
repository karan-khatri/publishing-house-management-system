package org.swam.publishing_house.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.swam.publishing_house.dto.author.AuthorCreateRequestDTO;
import org.swam.publishing_house.dto.author.AuthorPatchRequestDTO;
import org.swam.publishing_house.dto.author.AuthorResponseDTO;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.security.JWTTokenNeeded;
import org.swam.publishing_house.service.AuthorService;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorRest {

    @Inject
    private AuthorService authorService;

    // No-arg constructor for CDI
    public AuthorRest() {
        // Empty constructor for CDI
    }

    /**
     * Create a new author
     */
    @POST
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER", "EDITOR"})
    public Response createAuthor(@Valid AuthorCreateRequestDTO request) {
        try {
            AuthorResponseDTO author = authorService.createAuthor(request);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponseDTO.created("Author created successfully", author))
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponseDTO.badRequest(e.getMessage()))
                    .build();
        }
    }

    /**
     * Get author by ID
     */
    @GET
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"USER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getAuthorById(@PathParam("id") Long id) {
        Optional<AuthorResponseDTO> author = authorService.getAuthorById(id);
        if (author.isPresent()) {
            return Response.ok(ApiResponseDTO.success("Author retrieved successfully", author.get()))
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponseDTO.notFound("Author not found"))
                .build();
    }

    /**
     * Delete author
     */
    @DELETE
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response deleteAuthor(@PathParam("id") Long id) {
        try {
            Optional<AuthorResponseDTO> deletedAuthor = authorService.deleteAuthor(id);
            if (deletedAuthor.isPresent()) {
                return Response.status(Response.Status.OK)
                        .entity(ApiResponseDTO.success("Author deleted successfully", deletedAuthor.get()))
                        .build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponseDTO.notFound("Author not found"))
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("associated publications")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(ApiResponseDTO.conflict(e.getMessage()))
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponseDTO.internalServerError("Error deleting author: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Patch author - update only provided fields
     */
    @PATCH
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER", "EDITOR"})
    public Response patchAuthor(@PathParam("id") Long id, @Valid AuthorPatchRequestDTO request) {
        try {
            AuthorResponseDTO updatedAuthor = authorService.patchAuthor(id, request);
            return Response.ok(ApiResponseDTO.success("Author updated successfully", updatedAuthor))
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

    /**
     * Get authors with filters, pagination and sorting
     */
    @GET
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER", "EDITOR"})
    public Response getAuthorsWithFilters(@QueryParam("query") String query,
                                          @QueryParam("page") @DefaultValue("0") int page,
                                          @QueryParam("limit") @DefaultValue("10") int limit,
                                          @QueryParam("sortBy") @DefaultValue("name") String sortBy,
                                          @QueryParam("sortDir") @DefaultValue("asc") String sortDir) {
        try {
            // Build filter request
            BaseFiltersDTO filters = new BaseFiltersDTO(query,page,limit,sortBy,sortDir);

            PagedResult<AuthorResponseDTO> result = authorService.getAuthorsWithFilters(filters);

            return Response.ok(ApiResponseDTO.success("Authors retrieved successfully", result))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponseDTO.internalServerError(e.getMessage()))
                    .build();
        }
    }
}