package utils;

public class SQLQueries {


    public static final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys = ON;";

    /////////////////////////////////////////////////////////////////////////////

    public static final String CREATE_GROUP_TABLE =
            "CREATE TABLE IF NOT EXISTS groups (\n" +
            "    group_id BIGINT PRIMARY KEY,\n" +
            "    group_name VARCHAR(100) CHECK (length(group_name) > 1) NOT NULL UNIQUE,\n" +
            "    group_description VARCHAR(255)\n" +
            ");";

    public static final String GROUP_CREATE =
            "INSERT INTO groups VALUES (?,?,?);";

    public static final String GROUP_FIND_ALL_BY_NAME =
            "SELECT *\n" +
            "FROM groups\n" +
            "WHERE lower(group_name) LIKE lower(?);";

    public static final String GROUP_FIND_ALL_BY_ID =
            "SELECT *\n" +
            "FROM groups\n" +
            "WHERE group_id = ?;";

    public static final String GROUPS_DELETE_ALL =
            ENABLE_FOREIGN_KEYS+"DELETE FROM groups;";



    /////////////////////////////////////////////////////////////////////////////////


    public static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE IF NOT EXISTS products (\n" +
                    "    product_id BIGINT PRIMARY KEY,\n" +
                    "    product_name VARCHAR(100) CHECK (length(product_name) > 1) NOT NULL UNIQUE ,\n" +
                    "    product_description VARCHAR(255),\n" +
                    "    product_producer VARCHAR(255) NOT NULL ,\n" +
                    "    product_quantity INTEGER CHECK(product_quantity >= 0) NOT NULL,\n" +
                    "    product_price DOUBLE CHECK(product_price >= 0) NOT NULL,\n" +
                    "    group_id BIGINT NOT NULL,\n" +
                    "    FOREIGN KEY (group_id)\n" +
                    "        REFERENCES groups (group_id)\n" +
                    "        ON DELETE CASCADE\n" +
                    "        ON UPDATE CASCADE\n" +
                    ");";


    public static final String PRODUCT_CREATE =
            "INSERT INTO products VALUES (?,?,?,?,?,?,?);";

    public static final String PRODUCT_FIND_ALL_BY_NAME =
            "SELECT *\n" +
            "FROM products\n" +
            "WHERE lower(product_name) LIKE lower(?);";

    public static final String PRODUCT_FIND_ALL_BY_ID =
            "SELECT *\n" +
            "FROM products\n" +
            "WHERE product_id = ?;";

    public static final String PRODUCT_GET_ALL =
            "SELECT *\n" +
            "FROM products;\n";

    public static final String PRODUCT_ORDER_BY_BASE =
            "SELECT *\n" +
            "FROM products\n" +
            "ORDER BY ";

}
