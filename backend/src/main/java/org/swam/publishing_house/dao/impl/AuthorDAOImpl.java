package org.swam.publishing_house.dao.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.swam.publishing_house.dao.AuthorDAO;
import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.model.AuthorModel;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthorDAOImpl implements AuthorDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public AuthorModel save(AuthorModel author) {
        try {
            em.persist(author);
            return author;
        } catch (Exception e) {
            throw new RuntimeException("Error saving author", e);
        }
    }

    @Override
    public AuthorModel update(AuthorModel author) {
        try {
            return em.merge(author);
        } catch (Exception e) {
            throw new RuntimeException("Error updating author", e);
        }
    }

    @Override
    public Optional<AuthorModel> findById(Long id) {
        try {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT a, " +
                            "(SELECT COUNT(p) FROM PublicationModel p JOIN p.authors auth WHERE auth.id = a.id) " +
                            "FROM AuthorModel a WHERE a.id = :id",
                    Object[].class);
            query.setParameter("id", id);

            List<Object[]> results = query.getResultList();
            if (results.isEmpty()) {
                return Optional.empty();
            }

            Object[] result = results.get(0);
            AuthorModel author = (AuthorModel) result[0];
            Long publicationCount = (Long) result[1];
            author.setPublicationCount(publicationCount);

            return Optional.of(author);
        } catch (Exception e) {
            throw new RuntimeException("Error finding author by id", e);
        }
    }

    @Override
    public List<AuthorModel> findAll() {
        try {
            // Single query to get authors with their publication counts
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT a, " +
                            "(SELECT COUNT(p) FROM PublicationModel p JOIN p.authors auth WHERE auth.id = a.id) " +
                            "FROM AuthorModel a " +
                            "ORDER BY a.createdAt DESC",
                    Object[].class);

            List<Object[]> results = query.getResultList();

            return results.stream().map(result -> {
                AuthorModel author = (AuthorModel) result[0];
                Long publicationCount = (Long) result[1];
                author.setPublicationCount(publicationCount);
                return author;
            }).toList();

        } catch (Exception e) {
            throw new RuntimeException("Error finding all authors", e);
        }
    }

    @Override
    public Optional<AuthorModel> deleteById(Long id) {
        try {
            // First get the author with publication count
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT a, " +
                            "(SELECT COUNT(p) FROM PublicationModel p JOIN p.authors auth WHERE auth.id = a.id) " +
                            "FROM AuthorModel a WHERE a.id = :id",
                    Object[].class);
            query.setParameter("id", id);

            List<Object[]> results = query.getResultList();
            if (results.isEmpty()) {
                return Optional.empty();
            }

            Object[] result = results.get(0);
            AuthorModel author = (AuthorModel) result[0];
            Long publicationCount = (Long) result[1];
            author.setPublicationCount(publicationCount);

            // Check if author has publications
            if (publicationCount > 0) {
                throw new RuntimeException("Cannot delete author " + author.getName() +
                        " because they have " + publicationCount + " associated publication(s). \n" +
                        "Please remove the author from all publications first.");
            }

            em.remove(author);
            return Optional.of(author);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM AuthorModel a WHERE a.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking if email exists", e);
        }
    }

    @Override
    public PagedResult<AuthorModel> findWithFilters(BaseFiltersDTO filters) {
        try {
            // Build the base query with publication count
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT a, ");
            queryBuilder.append("(SELECT COUNT(p) FROM PublicationModel p JOIN p.authors auth WHERE auth.id = a.id) ");
            queryBuilder.append("FROM AuthorModel a WHERE 1=1");

            // Add filter conditions
            if (filters.getQuery() != null && !filters.getQuery().trim().isEmpty()) {
                queryBuilder.append(" AND (LOWER(a.name) LIKE LOWER(:query) OR LOWER(a.email) LIKE LOWER(:query) OR LOWER(a.nationality) LIKE LOWER(:query))");
            }

            // Add sorting
            queryBuilder.append(" ORDER BY a.").append(filters.getSortBy()).append(" ").append(filters.getSortDir());

            // Create the main query
            TypedQuery<Object[]> query = em.createQuery(queryBuilder.toString(), Object[].class);
            setQueryParameters(query, filters);

            // Get total count (separate query for counting)
            String countQuery = queryBuilder.toString()
                    .replace("SELECT a, (SELECT COUNT(p) FROM PublicationModel p JOIN p.authors auth WHERE auth.id = a.id) FROM AuthorModel a",
                            "SELECT COUNT(a) FROM AuthorModel a");
            int orderByIndex = countQuery.indexOf(" ORDER BY");
            if (orderByIndex != -1) {
                countQuery = countQuery.substring(0, orderByIndex);
            }

            TypedQuery<Long> totalQuery = em.createQuery(countQuery, Long.class);
            setQueryParameters(totalQuery, filters);
            long totalElements = totalQuery.getSingleResult();

            // Apply pagination
            query.setFirstResult(filters.getPage() * filters.getLimit());
            query.setMaxResults(filters.getLimit());

            // Execute query and map results
            List<Object[]> results = query.getResultList();
            List<AuthorModel> authors = results.stream().map(result -> {
                AuthorModel author = (AuthorModel) result[0];
                Long publicationCount = (Long) result[1];
                author.setPublicationCount(publicationCount);
                return author;
            }).toList();

            return new PagedResult<>(authors, filters.getPage(), filters.getLimit(), totalElements);

        } catch (Exception e) {
            throw new RuntimeException("Error finding authors with filters", e);
        }
    }

    private void setQueryParameters(TypedQuery<?> query, BaseFiltersDTO filters) {
        if (filters.getQuery() != null && !filters.getQuery().trim().isEmpty()) {
            query.setParameter("query", "%" + filters.getQuery().trim() + "%");
        }
    }

}