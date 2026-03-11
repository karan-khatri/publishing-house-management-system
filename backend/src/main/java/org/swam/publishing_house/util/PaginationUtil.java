package org.swam.publishing_house.util;

import java.util.Arrays;

public class PaginationUtil {

    public static int validatePage(int page) {
        return Math.max(0, page);
    }

    public static int validateSize(int size) {
        return (size <= 0 || size > 100) ? 20 : size;
    }

    public static String validateSortDirection(String direction) {
        return ("desc".equalsIgnoreCase(direction)) ? "desc" : "asc";
    }

    public static String validateSortField(String sortBy, String[] allowedFields, String defaultField) {
        if (sortBy != null && Arrays.asList(allowedFields).contains(sortBy)) {
            return sortBy;
        }
        return defaultField;
    }
}