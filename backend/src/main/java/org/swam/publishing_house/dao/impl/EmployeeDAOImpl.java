package org.swam.publishing_house.dao.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.swam.publishing_house.dao.EmployeeDAO;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.model.EmployeeModel;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EmployeeDAOImpl implements EmployeeDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public EmployeeModel save(EmployeeModel employee) {
        try {
            em.persist(employee);
            return employee;
        } catch (Exception e) {
            throw new RuntimeException("Error saving employee", e);
        }
    }

    @Override
    public Optional<EmployeeModel> findById(Long id) {
        try {
            EmployeeModel employee = em.find(EmployeeModel.class, id);
            return Optional.ofNullable(employee);
        } catch (Exception e) {
            throw new RuntimeException("Error finding employee by id", e);
        }
    }

    @Override
    public Optional<EmployeeModel> findByEmployeeId(String employeeId) {
        try {
            TypedQuery<EmployeeModel> query = em.createQuery(
                    "SELECT e FROM EmployeeModel e WHERE e.employeeId = :employeeId", EmployeeModel.class);
            query.setParameter("employeeId", employeeId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding employee by employee id", e);
        }
    }

    @Override
    public Optional<EmployeeModel> findByUser(UserModel user) {
        try {
            TypedQuery<EmployeeModel> query = em.createQuery(
                    "SELECT e FROM EmployeeModel e WHERE e.user = :user", EmployeeModel.class);
            query.setParameter("user", user);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding employee by user", e);
        }
    }

    @Override
    public List<EmployeeModel> findAll() {
        try {
            TypedQuery<EmployeeModel> query = em.createQuery(
                    "SELECT e FROM EmployeeModel e ORDER BY e.createdAt DESC", EmployeeModel.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all employees", e);
        }
    }

    @Override
    public EmployeeModel update(EmployeeModel employee) {
        try {
            return em.merge(employee);
        } catch (Exception e) {
            throw new RuntimeException("Error updating employee", e);
        }
    }

    @Override
    public Optional<EmployeeModel> deleteById(Long id) {
        try {
            EmployeeModel employee = em.find(EmployeeModel.class, id);
            if (employee != null) {
                em.remove(employee);
                return Optional.of(employee);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    @Override
    public boolean existsByEmployeeId(String employeeId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM EmployeeModel e WHERE e.employeeId = :employeeId", Long.class);
            query.setParameter("employeeId", employeeId);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking if employee id exists", e);
        }
    }


    private void setQueryParameters(TypedQuery<?> query, BaseFiltersDTO filters) {
        if (filters.getQuery() != null && !filters.getQuery().trim().isEmpty()) {
            query.setParameter("query", "%" + filters.getQuery().trim() + "%");
        }
    }

    private String getSortByColumn(String sortByRaw) {
        String sortBy = (sortByRaw == null) ? "" : sortByRaw.trim().toLowerCase();
        switch (sortBy) {
            case "name":
                return "user.name";
            case "email":
                return "user.email";
            case "role":
                return "user.role.name";
            case "department":
                return "department";
            case "created_at":
            case "createdat":
                return "createdAt";
            default:
                return "createdAt";
        }
    }


    @Override
    public PagedResult<EmployeeModel> findWithFilters(BaseFiltersDTO filters) {
        try {
            // --- Build the main query ---
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT e FROM EmployeeModel e WHERE 1=1");

            if (filters.getQuery() != null && !filters.getQuery().trim().isEmpty()) {
                queryBuilder.append(" AND (LOWER(e.user.name) LIKE LOWER(:query) OR LOWER(e.user.email) LIKE LOWER(:query) OR LOWER(e.department) LIKE LOWER(:query) OR LOWER(e.user.role.name) LIKE LOWER(:query))");
            }

            // --- Add sorting ---
            queryBuilder.append(" ORDER BY e.")
                    .append(getSortByColumn(filters.getSortBy()))
                    .append(" ")
                    .append(filters.getSortDir());

            // --- Create main query with pagination ---
            TypedQuery<EmployeeModel> query = em.createQuery(queryBuilder.toString(), EmployeeModel.class);
            setQueryParameters(query, filters);

            query.setFirstResult(filters.getPage() * filters.getLimit());
            query.setMaxResults(filters.getLimit());

            List<EmployeeModel> results = query.getResultList();

            // --- Build the count query (same filters but SELECT COUNT) ---
            String countQueryStr = queryBuilder.toString()
                    .replaceFirst("SELECT e FROM EmployeeModel e", "SELECT COUNT(e) FROM EmployeeModel e")
                    .replaceFirst("ORDER BY.*", ""); // remove ORDER BY for count

            TypedQuery<Long> countQuery = em.createQuery(countQueryStr, Long.class);
            setQueryParameters(countQuery, filters);

            long totalItems = countQuery.getSingleResult();

            // --- Return paged result ---
            return new PagedResult<>(results, filters.getPage(), filters.getLimit(), totalItems);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}