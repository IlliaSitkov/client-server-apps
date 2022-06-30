package utils;

import java.util.Objects;

public enum FilterCriteria {
    SEARCH_STRING("searchString", SQLQueries.SEARCH_STRING_FILTER, 3),
    MIN_PRICE("minPrice", SQLQueries.MIN_PRICE_FILTER),
    MAX_PRICE("maxPrice", SQLQueries.MAX_PRICE_FILTER),
    MIN_QUANTITY("minQuantity", SQLQueries.MIN_QUANTITY_FILTER),
    MAX_QUANTITY("maxQuantity", SQLQueries.MAX_QUANTITY_FILTER),
    GROUP_ID("groupId", SQLQueries.GROUP_ID_FILTER);


    private final String value;

    private final String query;

    private final int paramRepeatTimes;


    FilterCriteria(String criteria, String query, int paramRepeatTimes) {
        value = criteria;
        this.query = query;
        this.paramRepeatTimes = paramRepeatTimes;
    }

    FilterCriteria(String criteria, String query) {
        this(criteria, query, 1);
    }

    public String getQuery() {
        return query;
    }

    public int getParamRepeatTimes() {
        return paramRepeatTimes;
    }

    @Override
    public String toString() {
        return value;
    }

}
