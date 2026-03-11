package org.swam.publishing_house.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.swam.publishing_house.dao.EmployeeDAO;
import org.swam.publishing_house.dao.UserDAO;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.dto.employee.EmployeeCreateRequestDTO;
import org.swam.publishing_house.dto.employee.EmployeePatchRequestDTO;
import org.swam.publishing_house.dto.employee.EmployeeResponseDTO;
import org.swam.publishing_house.dto.user.UserCreateRequestDTO;
import org.swam.publishing_house.dto.user.UserPatchRequestDTO;
import org.swam.publishing_house.mapper.EmployeeMapper;
import org.swam.publishing_house.model.EmployeeModel;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.util.PagedResult;
import org.swam.publishing_house.util.PaginationUtil;
import org.swam.publishing_house.util.PatchUtil;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class EmployeeService {

    @Inject
    private EmployeeDAO employeeDAO;
    
    @Inject
    private UserDAO userDAO;
    
    @Inject
    private UserService userService;

    // CDI requires a no-arg constructor
    public EmployeeService() {
        // Empty constructor for CDI
    }

    /**
     * Create new employee with associated user
     */
    public EmployeeResponseDTO createEmployee(EmployeeCreateRequestDTO request) {
        // Check if employee ID already exists
        if (employeeDAO.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        // Check if email already exists
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        try {
            // Create user first
            UserCreateRequestDTO userRequest = EmployeeMapper.toUserCreateRequestDTO(request);
            userService.registerUser(userRequest);

            // Get the created user
            UserModel user = userDAO.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Failed to create user"));

            // Create employee with the user
            EmployeeModel employee = EmployeeMapper.toEntity(request, user);
            EmployeeModel savedEmployee = employeeDAO.save(employee);

            return EmployeeMapper.toResponseDTO(savedEmployee);

        } catch (Exception e) {
            // If employee creation fails, clean up the user
            userDAO.findByEmail(request.getEmail()).ifPresent(user -> {
                try {
                    userDAO.deleteById(user.getId());
                } catch (Exception cleanupException) {
                    // Log cleanup failure but don't throw
                }
            });
            throw new RuntimeException("Failed to create employee: " + e.getMessage(), e);
        }
    }

    /**
     * Get employee by ID
     */
    public Optional<EmployeeResponseDTO> getEmployeeById(Long id) {
        return employeeDAO.findById(id).map(EmployeeMapper::toResponseDTO);
    }

    /**
     * Get employee by employee ID
     */
    public Optional<EmployeeResponseDTO> getEmployeeByEmployeeId(String employeeId) {
        return employeeDAO.findByEmployeeId(employeeId).map(EmployeeMapper::toResponseDTO);
    }

    /**
     * Get employee by user ID
     */
    public Optional<EmployeeResponseDTO> getEmployeeByUserId(Long userId) {
        Optional<UserModel> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return Optional.empty();
        }

        return employeeDAO.findByUser(userOpt.get()).map(EmployeeMapper::toResponseDTO);
    }

    /**
     * Get employees with filters
     */
    public PagedResult<EmployeeResponseDTO> getEmployeesWithFilters(BaseFiltersDTO filters) {
        filters.setPage(PaginationUtil.validatePage(filters.getPage()));
        filters.setLimit(PaginationUtil.validateSize(filters.getLimit()));
        filters.setSortDir(PaginationUtil.validateSortDirection(filters.getSortDir()));

        String[] allowedSortFields = {"name", "email", "role", "department", "position", "createdAt"};
        filters.setSortBy(PaginationUtil.validateSortField(filters.getSortBy(), allowedSortFields, "createdAt"));

        PagedResult<EmployeeModel> pagedResult = employeeDAO.findWithFilters(filters);

        List<EmployeeResponseDTO> dtoContent = pagedResult.getContent().stream()
                .map(EmployeeMapper::toResponseDTO)
                .toList();

        return new PagedResult<>(dtoContent, pagedResult.getPage(),
                pagedResult.getLimit(), pagedResult.getTotalItems());
    }

    /**
     * Patch employee with only provided fields
     */
    public EmployeeResponseDTO patchEmployee(Long id, @Valid EmployeePatchRequestDTO patchRequestDTO) {
        // Find existing employee
        EmployeeModel existingEmployee = employeeDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Apply patch with custom handlers
        PatchUtil.patch(patchRequestDTO, existingEmployee)
                .exclude("user", "createdAt", "updatedAt") // Exclude sensitive/system fields
                .handle("employeeId", this::handleEmployeeIdUpdate)
                .apply();

        UserPatchRequestDTO userPatchRequestDTO = EmployeeMapper.toUserPatchRequestDTO(patchRequestDTO);




        userService.patchUser(existingEmployee.getUser().getId(), userPatchRequestDTO);

        // Save and return
        EmployeeModel updatedEmployee = employeeDAO.update(existingEmployee);
        return EmployeeMapper.toResponseDTO(updatedEmployee);
    }

    /**
     * Delete employee by ID (this will also handle user deletion if needed)
     */
    public Optional<EmployeeResponseDTO> deleteEmployee(Long id) {
        Optional<EmployeeModel> employeeOpt = employeeDAO.findById(id);
        if (!employeeOpt.isPresent()) {
            return Optional.empty();
        }

        EmployeeModel employee = employeeOpt.get();
        UserModel user = employee.getUser();

        try {
            // Delete employee first
            Optional<EmployeeModel> deletedEmployee = employeeDAO.deleteById(id);


            if (deletedEmployee.isPresent() && user != null) {
                // Optionally delete the associated user
                // You might want to keep the user if they might be used elsewhere
                // userDAO.deleteById(user.getId());
            }

            return deletedEmployee.map(EmployeeMapper::toResponseDTO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    // Custom handler for Employee-specific validation
    private void handleEmployeeIdUpdate(EmployeePatchRequestDTO patchDto, EmployeeModel entity) {
        if (patchDto.getEmployeeId() != null) {
            // Check employee ID uniqueness if employee ID is being changed
            if (!entity.getEmployeeId().equals(patchDto.getEmployeeId()) &&
                    employeeDAO.existsByEmployeeId(patchDto.getEmployeeId())) {
                throw new RuntimeException("Employee ID already exists");
            }

            entity.setEmployeeId(patchDto.getEmployeeId());
        }
    }
}