package org.swam.publishing_house.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleModel extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "level", nullable = false)
    private Integer level;

    // Constructors
    public RoleModel() {}

    public RoleModel(String name, String description, Integer level) {
        this.name = name;
        this.description = description;
        this.level = level;
    }

    public RoleModel(String name, Integer level, String description) {
        this.name = name;
        this.level = level;
        this.description = description;
    }

    // Getters and Setters (remove id, createdAt, updatedAt, PrePersist, PreUpdate)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
}