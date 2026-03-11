package org.swam.publishing_house.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.swam.publishing_house.dao.RoleDAO;
import org.swam.publishing_house.model.RoleModel;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class RoleService {
    
    @Inject
    private RoleDAO roleDAO;

    // CDI requires a no-arg constructor
    public RoleService() {
        // Empty constructor for CDI
    }

    public RoleModel createRole(String name, String description, Integer level) {
        if (roleDAO.findByName(name).isPresent()) {
            throw new RuntimeException("Role with name '" + name + "' already exists");
        }

        RoleModel role = new RoleModel(name, description, level);
        return roleDAO.save(role);
    }

    public Optional<RoleModel> getRoleById(Long id) {
        return roleDAO.findById(id);
    }

    public Optional<RoleModel> getRoleByName(String name) {
        return roleDAO.findByName(name);
    }

    public List<RoleModel> getAllRoles() {
        return roleDAO.findAll();
    }

    public boolean deleteRole(Long id) {
        return roleDAO.deleteById(id);
    }
}