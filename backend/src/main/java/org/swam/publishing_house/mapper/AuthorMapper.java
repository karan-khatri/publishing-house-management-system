package org.swam.publishing_house.mapper;

import org.swam.publishing_house.dto.author.AuthorCreateRequestDTO;
import org.swam.publishing_house.dto.author.AuthorResponseDTO;
import org.swam.publishing_house.model.AuthorModel;

public class AuthorMapper {

    public static AuthorModel toEntity(AuthorCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        AuthorModel author = new AuthorModel();
        author.setName(dto.getName());
        author.setEmail(dto.getEmail());
        author.setBiography(dto.getBiography());
        author.setBirthDate(dto.getBirthDate());
        author.setNationality(dto.getNationality());

        return author;
    }

    public static AuthorResponseDTO toResponseDTO(AuthorModel author) {
        if (author == null) {
            return null;
        }

        return new AuthorResponseDTO(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getBiography(),
                author.getBirthDate(),
                author.getNationality(),
                author.getCreatedAt(),
                author.getUpdatedAt(),
                author.getPublicationCount() != null ?
                        author.getPublicationCount().intValue() : 0  // Convert Long to int
        );
    }

    public static void updateEntityFromDTO(AuthorModel author, AuthorCreateRequestDTO dto) {
        if (author == null || dto == null) {
            return;
        }

        author.setName(dto.getName());
        author.setEmail(dto.getEmail());
        author.setBiography(dto.getBiography());
        author.setBirthDate(dto.getBirthDate());
        author.setNationality(dto.getNationality());
    }
}