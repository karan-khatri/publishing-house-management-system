package org.swam.publishing_house.dao;

import org.swam.publishing_house.dto.common.BaseFiltersDTO;
import org.swam.publishing_house.model.AuthorModel;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

public interface AuthorDAO {
    AuthorModel save(AuthorModel author);
    AuthorModel update(AuthorModel author);
    Optional<AuthorModel> findById(Long id);
    List<AuthorModel> findAll();
    Optional<AuthorModel> deleteById(Long id);
    boolean existsByEmail(String email);
    PagedResult<AuthorModel> findWithFilters(BaseFiltersDTO filters);
}