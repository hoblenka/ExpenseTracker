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

    public static ExpenseCategory fromString(String category) {
        if (category == null) return null;
        for (ExpenseCategory cat : values()) {
            if (cat.displayName.equals(category) || cat.name().equals(category)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + category);
    }
}