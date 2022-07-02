package utils;

public enum SortCriteria {
    BY_NAME("product_name"),
    BY_QUANTITY("product_quantity"),
    BY_PRICE("product_price");

    private final String value;

    SortCriteria(String criteria) {
        value = criteria;
    }

    @Override
    public String toString() {
        return value;
    }

}
