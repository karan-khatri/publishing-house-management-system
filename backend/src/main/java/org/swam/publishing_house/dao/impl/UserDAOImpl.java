package org.swam.publishing_house.dao.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.swam.publishing_house.dao.UserDAO;
import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.model.RoleModel;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserDAOImpl implements UserDAO {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public UserModel save(UserModel user) {
        try {
            em.persist(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<UserModel> findById(Long id) {
        try {
            UserModel user = em.find(UserModel.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by id", e);
        }
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        try {
            TypedQuery<UserModel> query = em.createQuery(
                    "SELECT u FROM UserModel u WHERE u.email = :email", UserModel.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    @Override
    public List<UserModel> findAll() {
        try {
            TypedQuery<UserModel> query = em.createQuery(
                    "SELECT u FROM UserModel u ORDER BY u.createdAt DESC", UserModel.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all users", e);
        }
    }

    @Override
    public List<UserModel> findByRole(RoleModel role) {
        try {
            TypedQuery<UserModel> query = em.createQuery(
                    "SELECT u FROM UserModel u WHERE u.role = :role ORDER BY u.createdAt DESC", UserModel.class);
            query.setParameter("role", role);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding users by role", e);
        }
    }

    @Override
    public UserModel update(UserModel user) {
        try {
            return em.merge(user);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            UserModel user = em.find(UserModel.class, id);
            if (user != null) {
                em.remove(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM UserModel u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking if email exists", e);
        }
    }
}