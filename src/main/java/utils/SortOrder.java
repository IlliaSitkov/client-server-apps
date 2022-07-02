package utils;

public enum SortOrder {
    DESCENDING("DESC"),
    ASCENDING("ASC");

    private final String value;

    SortOrder(String criteria) {
        value = criteria;
    }

    @Override
    public String toString() {
        return value;
    }

}
