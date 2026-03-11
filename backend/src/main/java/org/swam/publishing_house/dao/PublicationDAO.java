package org.swam.publishing_house.dao;

import org.swam.publishing_house.model.PublicationModel;
import org.swam.publishing_house.dto.publication.PublicationFilterRequest;
import org.swam.publishing_house.util.PagedResult;

import java.util.List;
import java.util.Optional;

public interface PublicationDAO {
    PublicationModel save(PublicationModel publication);
    Optional<PublicationModel> findById(Long id);
    List<PublicationModel> findAll();
    PublicationModel update(PublicationModel publication);
    Optional<PublicationModel> deleteById(Long id);
    boolean existsByIsbn(String isbn);
    PagedResult<PublicationModel> findWithFilters(PublicationFilterRequest filters);
}