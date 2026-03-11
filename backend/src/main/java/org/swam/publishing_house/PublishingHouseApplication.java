package org.swam.publishing_house;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.swam.publishing_house.rest.*;
import org.swam.publishing_house.security.AuthenticationFilter;
import org.swam.publishing_house.security.CORSFilter;
import org.swam.publishing_house.security.SecurityContextCleanupFilter;
import org.swam.publishing_house.util.ErrorHandlerUtil;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class PublishingHouseApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        // classes.add(UserRest.class);
        classes.add(AuthRest.class);
        classes.add(AuthorRest.class);
        classes.add(PublicationRest.class);
        classes.add(EmployeeRest.class);
        classes.add(RoleRest.class);

        // Filters
        classes.add(AuthenticationFilter.class);
        classes.add(CORSFilter.class);
        classes.add(SecurityContextCleanupFilter.class);

        // Exception Mappers
        classes.add(ErrorHandlerUtil.class);

        return classes;
    }
}