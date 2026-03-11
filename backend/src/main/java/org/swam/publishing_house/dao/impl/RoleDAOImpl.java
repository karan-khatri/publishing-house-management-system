package org.swam.publishing_house.dao.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.swam.publishing_house.dao.RoleDAO;
import org.swam.publishing_house.model.RoleModel;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleDAOImpl implements RoleDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public RoleModel save(RoleModel role) {
        try {
            em.persist(role);
            em.flush();
            return role;
        } catch (Exception e) {
            throw new RuntimeException("Error saving role", e);
        }
    }

    @Override
    public RoleModel update(RoleModel role) {
        try {
            RoleModel updatedRole = em.merge(role);
            em.flush();
            return updatedRole;
        } catch (Exception e) {
            throw new RuntimeException("Error updating role", e);
        }
    }

    @Override
    public Optional<RoleModel> findById(Long id) {
        try {
            RoleModel role = em.find(RoleModel.class, id);
            if (role != null) {
                // Force initialization of all properties
                role.getName();
                role.getDescription();
                role.getLevel();
            }
            return Optional.ofNullable(role);
        } catch (Exception e) {
            throw new RuntimeException("Error finding role by id", e);
        }
    }

    @Override
    public Optional<RoleModel> findByName(String name) {
        try {
            TypedQuery<RoleModel> query = em.createQuery(
                    "SELECT r FROM RoleModel r WHERE r.name = :name", RoleModel.class);
            query.setParameter("name", name);
            RoleModel role = query.getSingleResult();
            if (role != null) {
                // Force initialization of all properties
                role.getName();
                role.getDescription();
                role.getLevel();
            }
            return Optional.ofNullable(role);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding role by name", e);
        }
    }

    @Override
    public List<RoleModel> findAll() {
        try {
            TypedQuery<RoleModel> query = em.createQuery(
                    "SELECT r FROM RoleModel r ORDER BY r.level ASC", RoleModel.class);
            List<RoleModel> roles = query.getResultList();
            // Force initialization of all properties for each role
            for (RoleModel role : roles) {
                role.getName();
                role.getDescription();
                role.getLevel();
            }
            return roles;
        } catch (Exception e) {
            throw new RuntimeException("Error finding all roles", e);
        }
    }

    @Override
    public List<RoleModel> findByLevelLessThanEqual(Integer level) {
        try {
            TypedQuery<RoleModel> query = em.createQuery(
                    "SELECT r FROM RoleModel r WHERE r.level <= :level ORDER BY r.level ASC", RoleModel.class);
            query.setParameter("level", level);
            List<RoleModel> roles = query.getResultList();
            // Force initialization of all properties for each role
            for (RoleModel role : roles) {
                role.getName();
                role.getDescription();
                role.getLevel();
            }
            return roles;
        } catch (Exception e) {
            throw new RuntimeException("Error finding roles by level", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            RoleModel role = em.find(RoleModel.class, id);
            if (role != null) {
                em.remove(role);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting role", e);
        }
    }
}