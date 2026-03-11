package org.swam.publishing_house.security;

public class SecurityContext {
    private static final ThreadLocal<String> currentUserEmail = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserRole = new ThreadLocal<>();

    public static void setCurrentUser(String email, String role) {
        currentUserEmail.set(email);
        currentUserRole.set(role);
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail.get();
    }

    public static String getCurrentUserRole() {
        return currentUserRole.get();
    }

    public static boolean isCurrentUserAdmin() {
        String role = currentUserRole.get();
        return "ADMIN".equals(role);
    }

    public static void clear() {
        currentUserEmail.remove();
        currentUserRole.remove();
    }
}