package org.swam.publishing_house.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseConnection {

    private static EntityManagerFactory entityManagerFactory;

    static {
        try {
            // Initialize EntityManagerFactory using persistence unit name from persistence.xml
            entityManagerFactory = Persistence.createEntityManagerFactory("default");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create EntityManagerFactory", e);
        }
    }

    /**
     * Get EntityManagerFactory instance
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * Get a new EntityManager instance
     */
    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Close EntityManagerFactory on application shutdown
     */
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
