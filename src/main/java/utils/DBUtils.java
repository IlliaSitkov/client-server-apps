package utils;

import model.Group;
import model.Product;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {


    public static final String PROD_DB = "store.sqlite";
    public static final String TEST_DB = "test_store.sqlite";

    private DBUtils() {
        throw new RuntimeException("Can not create object");
    }

    public static Connection getDBConnection(String databaseName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:"+databaseName);
    }


    public static List<Product> resultSetToProductList(ResultSet resultSet) throws SQLException {
        List<Product> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new Product(
                            resultSet.getLong("product_id"),
                            resultSet.getString("product_name"),
                            resultSet.getString("product_description"),
                            resultSet.getString("product_producer"),
                            resultSet.getInt("product_quantity"),
                            resultSet.getDouble("product_price"),
                            resultSet.getLong("group_id")));
        }
        return list;
    }

    
    public static List<Group> resultSetToGroupList(ResultSet resultSet) throws SQLException {
    	List<Group> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new Group(
                            resultSet.getLong("group_id"),
                            resultSet.getString("group_name"),
                            resultSet.getString("group_description"))
                    );
        }
        return list;
    }
    
    public static List<User> resultSetToUserList(ResultSet resultSet) throws SQLException {
    	List<User> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new User(
                            resultSet.getLong("user_id"),
                            resultSet.getString("user_name"),
                            resultSet.getString("password"))
                    );
        }
        return list;
    }
    
    


}
