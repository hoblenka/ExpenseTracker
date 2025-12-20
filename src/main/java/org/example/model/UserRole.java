package org.example.model;

public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String role) {
        if (role == null) return USER;
        for (UserRole userRole : UserRole.values()) {
            if (userRole.displayName.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        return USER;
    }
}