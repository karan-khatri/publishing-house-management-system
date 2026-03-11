package org.swam.publishing_house.dto.employee;

import org.swam.publishing_house.dto.common.BaseFiltersDTO;

public class EmployeeFilterRequestDTO extends BaseFiltersDTO {

    private String roleId;
    private String query;
    private String department;
    private String position;

    public EmployeeFilterRequestDTO() {}

    public EmployeeFilterRequestDTO(int page, int limit, String sortBy, String sortDir, String roleId, String query, String department, String position) {
        this.roleId = roleId;
        this.query = query;
        this.department = department;
        this.position = position;
        this.setPage(page);
        this.setLimit(limit);
        this.setSortBy(sortBy);
        this.setSortDir(sortDir);
    }

    //getters and setters
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}