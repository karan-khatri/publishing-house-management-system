package org.swam.publishing_house.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.swam.publishing_house.dto.common.ApiResponseDTO;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.dto.employee.EmployeeCreateRequestDTO;
import org.swam.publishing_house.dto.employee.EmployeePatchRequestDTO;
import org.swam.publishing_house.dto.employee.EmployeeResponseDTO;
import org.swam.publishing_house.security.JWTTokenNeeded;
import org.swam.publishing_house.security.SecurityContext;
import org.swam.publishing_house.service.EmployeeService;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeRest {

    @Inject
    private EmployeeService employeeService;

    // No-arg constructor for CDI (can be implicit)
    public EmployeeRest() {
    }

    @POST
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response createEmployee(@Valid EmployeeCreateRequestDTO request) {
        try {
            EmployeeResponseDTO employee = employeeService.createEmployee(request);
            ApiResponseDTO<EmployeeResponseDTO> response = ApiResponseDTO.success(
                    "Employee created successfully", employee);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @GET
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getEmployees(@QueryParam("query") String query,
                                    @QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("limit") @DefaultValue("20") int limit,
                                    @QueryParam("sortBy") @DefaultValue("name") String sortBy,
                                    @QueryParam("sortDir") @DefaultValue("asc") String sortDir) {
        BaseFiltersDTO filters = new BaseFiltersDTO(query, page, limit, sortBy, sortDir);
        PagedResult<EmployeeResponseDTO> result = employeeService.getEmployeesWithFilters(filters);
        return Response.ok(ApiResponseDTO.success("Employees retrieved successfully", result))
                .build();
    }

    @GET
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"USER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getEmployeeById(@PathParam("id") Long id) {
        String currentUserRole = SecurityContext.getCurrentUserRole();

        Optional<EmployeeResponseDTO> employee = employeeService.getEmployeeById(id);

        if (employee.isPresent()) {
            EmployeeResponseDTO foundEmployee = employee.get();

            // Only allow access if user is admin/manager or accessing their own employee record
            if (!isAuthorizedToViewEmployee(foundEmployee)) {
                ApiResponseDTO<Object> error = ApiResponseDTO.forbidden("Access denied");
                return Response.status(Response.Status.FORBIDDEN).entity(error).build();
            }

            ApiResponseDTO<EmployeeResponseDTO> response = ApiResponseDTO.success("Employee retrieved successfully", foundEmployee);
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Employee not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    @GET
    @Path("/employee-id/{employeeId}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response getEmployeeByEmployeeId(@PathParam("employeeId") String employeeId) {
        Optional<EmployeeResponseDTO> employee = employeeService.getEmployeeByEmployeeId(employeeId);

        if (employee.isPresent()) {
            ApiResponseDTO<EmployeeResponseDTO> response = ApiResponseDTO.success("Employee retrieved successfully", employee.get());
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Employee not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    @GET
    @Path("/user/{userId}")
    @JWTTokenNeeded(roles = {"USER", "EDITOR", "MANAGER", "ADMIN"})
    public Response getEmployeeByUserId(@PathParam("userId") Long userId) {
        Optional<EmployeeResponseDTO> employee = employeeService.getEmployeeByUserId(userId);

        if (employee.isPresent()) {
            EmployeeResponseDTO foundEmployee = employee.get();

            // Only allow access if user is admin/manager or accessing their own employee record
            if (!isAuthorizedToViewEmployee(foundEmployee)) {
                ApiResponseDTO<Object> error = ApiResponseDTO.forbidden("Access denied");
                return Response.status(Response.Status.FORBIDDEN).entity(error).build();
            }

            ApiResponseDTO<EmployeeResponseDTO> response = ApiResponseDTO.success("Employee retrieved successfully", foundEmployee);
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Employee not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }


    @PATCH
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN", "MANAGER"})
    public Response patchEmployee(@PathParam("id") Long id, @Valid EmployeePatchRequestDTO request) {
        try {
            EmployeeResponseDTO updatedEmployee = employeeService.patchEmployee(id, request);
            ApiResponseDTO<EmployeeResponseDTO> response = ApiResponseDTO.success("Employee updated successfully", updatedEmployee);
            return Response.ok(response).build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                ApiResponseDTO<Object> error = ApiResponseDTO.notFound(e.getMessage());
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
            ApiResponseDTO<Object> error = ApiResponseDTO.badRequest(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @JWTTokenNeeded(roles = {"ADMIN"})
    public Response deleteEmployee(@PathParam("id") Long id) {
        Optional<EmployeeResponseDTO> deletedEmployee = employeeService.deleteEmployee(id);

        if (deletedEmployee.isPresent()) {
            ApiResponseDTO<EmployeeResponseDTO> response = ApiResponseDTO.success("Employee deleted successfully", deletedEmployee.get());
            return Response.ok(response).build();
        } else {
            ApiResponseDTO<Object> error = ApiResponseDTO.notFound("Employee not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }




    // Helper method to check authorization
    private boolean isAuthorizedToViewEmployee(EmployeeResponseDTO employee) {
        String currentUserEmail = SecurityContext.getCurrentUserEmail();
        String currentUserRole = SecurityContext.getCurrentUserRole();

        // Admins and managers can view any employee
        if ("ADMIN".equals(currentUserRole) || "MANAGER".equals(currentUserRole)) {
            return true;
        }

        // Users can only view their own employee record
        return employee.getUser().getEmail().equals(currentUserEmail);
    }
}