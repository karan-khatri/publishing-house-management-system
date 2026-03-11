package org.swam.publishing_house.dao.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.swam.publishing_house.dao.PublicationDAO;
import org.swam.publishing_house.model.PublicationModel;
import org.swam.publishing_house.dto.publication.PublicationFilterRequest;
import org.swam.publishing_house.util.PagedResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PublicationDAOImpl implements PublicationDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public PublicationModel save(PublicationModel publication) {
        try {
            em.persist(publication);
            return publication;
        } catch (Exception e) {
            throw new RuntimeException("Error saving publication", e);
        }
    }

    @Override
    public PublicationModel update(PublicationModel publication) {
        try {
            return em.merge(publication);
        } catch (Exception e) {
            throw new RuntimeException("Error updating publication", e);
        }
    }

    @Override
    public Optional<PublicationModel> findById(Long id) {
        try {
            TypedQuery<PublicationModel> query = em.createQuery(
                    "SELECT p FROM PublicationModel p LEFT JOIN FETCH p.authors WHERE p.id = :id",
                    PublicationModel.class);
            query.setParameter("id", id);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding publication by id", e);
        }
    }

    @Override
    public List<PublicationModel> findAll() {
        try {
            TypedQuery<PublicationModel> query = em.createQuery(
                    "SELECT DISTINCT p FROM PublicationModel p LEFT JOIN FETCH p.authors ORDER BY p.title",
                    PublicationModel.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all publications", e);
        }
    }

    @Override
    public Optional<PublicationModel> deleteById(Long id) {
        try {
            // First find the publication with authors
            TypedQuery<PublicationModel> query = em.createQuery(
                    "SELECT p FROM PublicationModel p LEFT JOIN FETCH p.authors WHERE p.id = :id",
                    PublicationModel.class);
            query.setParameter("id", id);

            try {
                PublicationModel publication = query.getSingleResult();
                em.remove(publication);
                return Optional.of(publication);
            } catch (NoResultException e) {
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error deleting publication", e);
        }
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(p) FROM PublicationModel p WHERE p.isbn = :isbn", Long.class);
            query.setParameter("isbn", isbn);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking if isbn exists", e);
        }
    }

    @Override
    public PagedResult<PublicationModel> findWithFilters(PublicationFilterRequest filters) {
        try {
            // Build dynamic query
            StringBuilder queryBuilder = new StringBuilder(
                    "SELECT DISTINCT p FROM PublicationModel p LEFT JOIN FETCH p.authors WHERE 1=1");
            StringBuilder countQueryBuilder = new StringBuilder(
                    "SELECT COUNT(DISTINCT p) FROM PublicationModel p WHERE 1=1");

            List<String> conditions = new ArrayList<>();

            if(filters.getQuery() != null && !filters.getQuery().trim().isEmpty()) {
                conditions.add(" AND (LOWER(p.title) LIKE LOWER(:query) OR LOWER(p.isbn) LIKE LOWER(:query) OR LOWER(p.type) LIKE LOWER(:query) OR LOWER(p.status) LIKE LOWER(:query))");
            }

            if (filters.getTitle() != null && !filters.getTitle().trim().isEmpty()) {
                conditions.add(" AND LOWER(p.title) LIKE LOWER(:title)");
            }

            if (filters.getIsbn() != null && !filters.getIsbn().trim().isEmpty()) {
                conditions.add(" AND p.isbn = :isbn");
            }

            if (filters.getType() != null) {
                conditions.add(" AND p.type = :type");
            }

            if (filters.getStatus() != null) {
                conditions.add(" AND p.status = :status");
            }

            if (filters.getAuthorId() != null) {
                conditions.add(" AND EXISTS (SELECT 1 FROM p.authors a WHERE a.id = :authorId)");
            }

            if (filters.getMinPrice() != null) {
                conditions.add(" AND p.price >= :minPrice");
            }

            if (filters.getMaxPrice() != null) {
                conditions.add(" AND p.price <= :maxPrice");
            }

            if (filters.getCreatedAfter() != null) {
                conditions.add(" AND DATE(p.createdAt) >= :createdAfter");
            }

            if (filters.getCreatedBefore() != null) {
                conditions.add(" AND DATE(p.createdAt) <= :createdBefore");
            }

            // Add conditions to queries
            for (String condition : conditions) {
                queryBuilder.append(condition);
                countQueryBuilder.append(condition);
            }

            // Add sorting
            if (filters.getSortBy() != null && !filters.getSortBy().trim().isEmpty()) {
                queryBuilder.append(" ORDER BY p.").append(filters.getSortBy())
                        .append(" ").append(filters.getSortDir() != null ? filters.getSortDir() : "asc");
            }

            // Create queries
            TypedQuery<PublicationModel> query = em.createQuery(queryBuilder.toString(), PublicationModel.class);
            TypedQuery<Long> countQuery = em.createQuery(countQueryBuilder.toString(), Long.class);

            // Set parameters for both queries
            setQueryParameters(query, filters);
            setQueryParameters(countQuery, filters);

            // Set pagination
            query.setFirstResult(filters.getPage() * filters.getLimit());
            query.setMaxResults(filters.getLimit());

            // Execute queries
            List<PublicationModel> content = query.getResultList();
            long totalItems = countQuery.getSingleResult();

            return new PagedResult<>(content, filters.getPage(), filters.getLimit(), totalItems);

        } catch (Exception e) {
            throw new RuntimeException("Error finding publications with filters", e);
        }
    }

    private void setQueryParameters(TypedQuery<?> query, PublicationFilterRequest filters) {
        if (filters.getQuery() != null && !filters.getQuery().trim().isEmpty()) {
            query.setParameter("query", "%" + filters.getQuery() + "%");
        }
        if (filters.getTitle() != null && !filters.getTitle().trim().isEmpty()) {
            query.setParameter("title", "%" + filters.getTitle() + "%");
        }
        if (filters.getIsbn() != null && !filters.getIsbn().trim().isEmpty()) {
            query.setParameter("isbn", filters.getIsbn());
        }
        if (filters.getType() != null) {
            query.setParameter("type", filters.getType());
        }
        if (filters.getStatus() != null) {
            query.setParameter("status", filters.getStatus());
        }
        if (filters.getAuthorId() != null) {
            query.setParameter("authorId", filters.getAuthorId());
        }
        if (filters.getMinPrice() != null) {
            query.setParameter("minPrice", filters.getMinPrice());
        }
        if (filters.getMaxPrice() != null) {
            query.setParameter("maxPrice", filters.getMaxPrice());
        }
        if (filters.getCreatedAfter() != null) {
            query.setParameter("createdAfter", filters.getCreatedAfter());
        }
        if (filters.getCreatedBefore() != null) {
            query.setParameter("createdBefore", filters.getCreatedBefore());
        }
    }
}