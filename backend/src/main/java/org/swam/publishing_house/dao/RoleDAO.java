package org.swam.publishing_house.dao;

import org.swam.publishing_house.model.RoleModel;
import java.util.List;
import java.util.Optional;

public interface RoleDAO {
    RoleModel save(RoleModel role);
    RoleModel update(RoleModel role);
    Optional<RoleModel> findById(Long id);
    Optional<RoleModel> findByName(String name);
    List<RoleModel> findAll();
    List<RoleModel> findByLevelLessThanEqual(Integer level);
    boolean deleteById(Long id);
}