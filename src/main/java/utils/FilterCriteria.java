package utils;

public enum FilterCriteria {
    SEARCH_STRING("searchString"),
    MIN_PRICE("minPrice"),
    MAX_PRICE("maxPrice"),
    MIN_QUANTITY("minQuantity"),
    MAX_QUANTITY("maxQuantity"),
    GROUP_ID("groupId");

    private final String value;


    FilterCriteria(String criteria) {
        value = criteria;
    }

    @Override
    public String toString() {
        return value;
    }

}
