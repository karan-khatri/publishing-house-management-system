package org.swam.publishing_house.mapper;

import org.swam.publishing_house.dto.author.AuthorResponseDTO;
import org.swam.publishing_house.dto.publication.PublicationCreateRequestDTO;
import org.swam.publishing_house.dto.publication.PublicationResponseDTO;
import org.swam.publishing_house.model.AuthorModel;
import org.swam.publishing_house.model.PublicationModel;

import java.util.Set;
import java.util.stream.Collectors;

public class PublicationMapper {

    /**
     * Convert PublicationCreateRequestDTO to PublicationModel
     */
    public static PublicationModel toModel(PublicationCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        PublicationModel publication = new PublicationModel();
        publication.setTitle(dto.getTitle());
        publication.setIsbn(dto.getIsbn());
        publication.setDescription(dto.getDescription());
        publication.setPrice(dto.getPrice());
        publication.setPublicationDate(dto.getPublicationDate());
        publication.setEdition(dto.getEdition());
        publication.setPages(dto.getPages());
        publication.setType(dto.getType());
        publication.setStatus(dto.getStatus());

        return publication;
    }

    /**
     * Convert PublicationModel to PublicationResponseDTO
     */
    public static PublicationResponseDTO toResponseDTO(PublicationModel publication) {
        if (publication == null) {
            return null;
        }

        PublicationResponseDTO dto = new PublicationResponseDTO();
        dto.setId(publication.getId());
        dto.setTitle(publication.getTitle());
        dto.setIsbn(publication.getIsbn());
        dto.setDescription(publication.getDescription());
        dto.setPrice(publication.getPrice());
        dto.setPublicationDate(publication.getPublicationDate());
        dto.setEdition(publication.getEdition());
        dto.setPages(publication.getPages());
        dto.setType(publication.getType());
        dto.setStatus(publication.getStatus());

        // Map authors to AuthorResponseDTO
        if (publication.getAuthors() != null) {
            Set<AuthorResponseDTO> authorDTOs = publication.getAuthors().stream()
                    .map(AuthorMapper::toResponseDTO)
                    .collect(Collectors.toSet());
            dto.setAuthors(authorDTOs);
        }

        return dto;
    }

    /**
     * Update existing PublicationModel with data from PublicationCreateRequestDTO
     */
    public static void updateModelFromDTO(PublicationModel publication, PublicationCreateRequestDTO dto) {
        if (publication == null || dto == null) {
            return;
        }

        publication.setTitle(dto.getTitle());
        publication.setIsbn(dto.getIsbn());
        publication.setDescription(dto.getDescription());
        publication.setPrice(dto.getPrice());
        publication.setPublicationDate(dto.getPublicationDate());
        publication.setEdition(dto.getEdition());
        publication.setPages(dto.getPages());
        publication.setType(dto.getType());
        publication.setStatus(dto.getStatus());
    }
}