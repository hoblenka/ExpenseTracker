package org.example.model;

public enum ExpenseCategory {
    FOOD("Food"),
    TRANSPORT("Transport"),
    UTILITIES("Utilities"),
    ENTERTAINMENT("Entertainment"),
    SHOPPING("Shopping"),
    RENT("Rent"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean isValid(String category) {
        if (category == null) return false;
        for (ExpenseCategory cat : values()) {
            if (cat.displayName.equals(category)) {
                return true;
            }
        }
        return false;
    }
}