package org.swam.publishing_house.dao;

import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.model.EmployeeModel;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
    EmployeeModel save(EmployeeModel employee);
    Optional<EmployeeModel> findById(Long id);
    Optional<EmployeeModel> findByEmployeeId(String employeeId);
    Optional<EmployeeModel> findByUser(UserModel user);
    List<EmployeeModel> findAll();
    PagedResult<EmployeeModel> findWithFilters(BaseFiltersDTO searchFilters);
    EmployeeModel update(EmployeeModel employee);
    Optional<EmployeeModel> deleteById(Long id);
    boolean existsByEmployeeId(String employeeId);
}