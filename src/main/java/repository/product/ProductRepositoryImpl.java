package repository.product;

import exceptions.SQLExceptionRuntime;
import model.Product;
import repository.AbstractRepository;
import utils.FilterCriteria;
import utils.DBUtils;
import utils.SQLQueries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProductRepositoryImpl extends AbstractRepository implements ProductRepository {

    private ProductRepositoryImpl(String databaseName) {
        super(SQLQueries.CREATE_PRODUCT_TABLE, databaseName);
    }

    private static volatile ProductRepositoryImpl groupRepository;

    public static ProductRepositoryImpl getInstance() {
        return getInstance(DBUtils.PROD_DB);
    }


    public static ProductRepositoryImpl getInstance(String databaseName) {
        ProductRepositoryImpl repository = groupRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (ProductRepositoryImpl.class) {
            if (groupRepository == null) {
                groupRepository = new ProductRepositoryImpl(databaseName);
            }
            return groupRepository;
        }
    }


    @Override
    public synchronized Product save(Product p) {
        try{
            PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_CREATE);
            st.setLong(1, p.getId());
            st.setString(2, p.getName());
            st.setString(3, p.getDescription());
            st.setString(4, p.getProducer());
            st.setInt(5, p.getQuantity());
            st.setDouble(6, p.getPrice());
            st.setLong(7, p.getGroupId());
            st.executeUpdate();
            return p;
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }

    @Override
    public Product update(Product p) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Product getById(Long id) {
        return null;
    }

    @Override
    public synchronized List<Product> getAll() {
        try{
            Statement st = connection.createStatement();
            st.execute(SQLQueries.PRODUCT_GET_ALL);
            return DBUtils.resultSetToProductList(st.getResultSet());
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void deleteOfGroup(Long groupId) {

    }

    @Override
    public synchronized boolean existsWithName(String name) {
        return existsWithName(name, SQLQueries.PRODUCT_FIND_ALL_BY_NAME);
    }

    @Override
    public synchronized boolean existsWithId(Long id) {
        return existsWithId(id, SQLQueries.PRODUCT_FIND_ALL_BY_ID);
    }


    public synchronized List<Product> findByCriteria(
            String searchString,
            double minPrice, double maxPrice,
            int minQuantity, int maxQuantity,
            Long groupId) {
        try{
            PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_FILTER);


            st.setString(1, searchString);
            st.setString(2, searchString);
            st.setString(3, searchString);
            st.setInt(4, minQuantity);
            st.setInt(5, maxQuantity);
            st.setDouble(6, minPrice);
            st.setDouble(7, maxPrice);
            st.setLong(8, groupId);
            st.setLong(9, groupId);
            st.execute();
            return DBUtils.resultSetToProductList(st.getResultSet());
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }
    @Override
    public synchronized List<Product> findByCriteria(Map<FilterCriteria, Object> criteria) {
        try{
            PreparedStatement st = prepareFilterStatement(criteria);
            st.execute();
            return DBUtils.resultSetToProductList(st.getResultSet());
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }


    private PreparedStatement prepareFilterStatement(Map<FilterCriteria, Object> criteriaMap) throws SQLException {
        StringBuilder filterQuery = new StringBuilder(SQLQueries.PRODUCT_FILTER_BASE);
        List<Object> params = new LinkedList<>();
        for (FilterCriteria criteria: criteriaMap.keySet()) {
            filterQuery.append(criteria.getQuery());
            Object o = criteriaMap.get(criteria);
            for (int i = 0; i < criteria.getParamRepeatTimes(); i++) {
                params.add(o);
            }
        }
        PreparedStatement preparedStatement = connection.prepareStatement(filterQuery.append(';').toString());
        int i = 1;
        for (Object o: params) {
            preparedStatement.setObject(i, o);
            i++;
        }
        return preparedStatement;
    }











}
