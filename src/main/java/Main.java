import model.Group;
import model.Product;
import org.json.JSONObject;
import repository.group.GroupRepository;
import repository.group.GroupRepositoryImpl;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import utils.FilterCriteria;
import utils.Utils;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        System.out.println(Utils.getIdFromPath("/api/7n","/api"));

    }

}
