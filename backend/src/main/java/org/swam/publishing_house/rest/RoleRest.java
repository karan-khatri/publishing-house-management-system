package org.swam.publishing_house.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.dto.role.RoleCreateRequestDTO;
import org.swam.publishing_house.dto.role.RoleResponseDTO;
import org.swam.publishing_house.mapper.RoleMapper;
import org.swam.publishing_house.model.RoleModel;
import org.swam.publishing_house.security.JWTTokenNeeded;
import org.swam.publishing_house.service.RoleService;

import java.util.List;
import java.util.Optional;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleRest {

    @Inject
    private RoleService roleService;

    // No-arg constructor for CDI
    public RoleRest() {
        // Empty constructor for CDI
    }

    @POST
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response createRole(@Valid RoleCreateRequestDTO request) {
        try {
            RoleModel createdRole = roleService.createRole(
                    request.getName(),
                    request.getDescription(),
                    request.getLevel()
            );
            RoleResponseDTO response = RoleMapper.toResponseDTO(createdRole);
            ApiResponseDTO<RoleResponseDTO> apiResponse = ApiResponseDTO.created(
                    "Role created successfully",
                    response
            );
            return Response.status(Response.Status.CREATED).entity(apiResponse).build();
        } catch (RuntimeException e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @GET
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getAllRoles() {
        try {
            List<RoleModel> roles = roleService.getAllRoles();
            List<RoleResponseDTO> roleResponses = roles.stream()
                    .map(RoleMapper::toResponseDTO)
                    .toList();
            ApiResponseDTO<List<RoleResponseDTO>> response = ApiResponseDTO.success(
                    "Roles retrieved successfully",
                    roleResponses
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.internalServerError("Error retrieving roles");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @GET
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getRoleById(@PathParam("id") Long id) {
        try {
            Optional<RoleModel> roleOpt = roleService.getRoleById(id);
            if (roleOpt.isPresent()) {
                RoleResponseDTO response = RoleMapper.toResponseDTO(roleOpt.get());
                ApiResponseDTO<RoleResponseDTO> apiResponse = ApiResponseDTO.success(
                        "Role retrieved successfully",
                        response
                );
                return Response.ok(apiResponse).build();
            } else {
                ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Role not found");
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
        } catch (Exception e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.internalServerError("Error retrieving role");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @GET
    @Path("/name/{name}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getRoleByName(@PathParam("name") String name) {
        try {
            Optional<RoleModel> roleOpt = roleService.getRoleByName(name);
            if (roleOpt.isPresent()) {
                RoleResponseDTO response = RoleMapper.toResponseDTO(roleOpt.get());
                ApiResponseDTO<RoleResponseDTO> apiResponse = ApiResponseDTO.success(
                        "Role retrieved successfully",
                        response
                );
                return Response.ok(apiResponse).build();
            } else {
                ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Role not found with name: " + name);
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
        } catch (Exception e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.internalServerError("Error retrieving role");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response deleteRole(@PathParam("id") Long id) {
        try {
            boolean deleted = roleService.deleteRole(id);
            if (deleted) {
                ApiResponseDTO<Void> response = ApiResponseDTO.noContent("Role deleted successfully");
                return Response.ok(response).build();
            } else {
                ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Role not found");
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
        } catch (Exception e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.internalServerError("Error deleting role");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }
}