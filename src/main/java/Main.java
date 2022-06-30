import utils.FilterCriteria;

import java.sql.SQLException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

//        String sDriverName = "org.sqlite.JDBC";
//        Class.forName(sDriverName);
//        String url = "jdbc:sqlite:store";
//        Connection con = DriverManager.getConnection(url);

//        GroupRepository groupRepository = GroupRepositoryImpl.getInstance();
//
//        System.out.println(groupRepository.existsWithName("group de1"));

//        groupRepository.save(new Group("Group      1     ", "Description"));
//        groupRepository.save(new Group("Group 1", "Description"));


//        ProductRepository productRepository = ProductRepositoryImpl.getInstance();
//
//        List<Product> products = productRepository.findByCriteria("gdf", 6,457,3,467,-1L);
//
//        groupRepository.deleteAll();
//
//        System.out.println(products);

        System.out.println(Arrays.toString(FilterCriteria.values()));


    }

}
