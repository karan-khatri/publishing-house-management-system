package org.swam.publishing_house.util;

import java.util.regex.Pattern;

public class HelperUtil {

    // Regular expression for password validation
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    // Regular expression for email validation
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Message for password requirements
    public static final String PASSWORD_REQUIREMENTS_MESSAGE = "Password must be at least 8 characters long and contain: \nat least one uppercase letter, one lowercase letter, one digit, \nand one special character (@$!%*?&)";


    // Email validation pattern
    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // Password validation pattern
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidPersonnelRole(String role) {
        return "EDITOR".equals(role) || "MANAGER".equals(role) ||
                "WAREHOUSE_WORKER".equals(role) || "ADMIN".equals(role);
    }
}