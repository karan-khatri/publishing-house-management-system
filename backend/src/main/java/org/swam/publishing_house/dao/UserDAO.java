package org.swam.publishing_house.dao;

import org.swam.publishing_house.model.UserModel;
import org.swam.publishing_house.model.RoleModel;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    UserModel save(UserModel user);
    Optional<UserModel> findById(Long id);
    Optional<UserModel> findByEmail(String email);
    List<UserModel> findAll();
    List<UserModel> findByRole(RoleModel role);
    UserModel update(UserModel user);
    boolean deleteById(Long id);
    boolean existsByEmail(String email);
}