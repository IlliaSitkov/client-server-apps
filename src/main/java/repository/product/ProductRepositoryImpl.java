package repository.product;

import exceptions.ProductNotFoundException;
import exceptions.SQLExceptionRuntime;
import model.Product;
import org.hibernate.Session;
import org.hibernate.query.Query;
import repository.AbstractRepository;
import utils.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Product> update(Product p) {
        try {
        	PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_UPDATE_BY_ID);
        	st.setString(1, p.getName());
        	st.setString(2, p.getDescription());
        	st.setString(3, p.getProducer());
        	st.setInt(4, p.getQuantity());
        	st.setDouble(5, p.getPrice());
        	st.setLong(6, p.getGroupId());
        	st.setLong(7, p.getId());
        	int res = st.executeUpdate();
        	return res > 0 ? Optional.of(p) : Optional.ofNullable(null);
        } catch(SQLException e) {
        	throw new SQLExceptionRuntime(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        if (!existsWithId(id)) {
            throw new ProductNotFoundException(id);
        }
    	try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_DELETE_BY_ID);
			st.setLong(1, id);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }

    @Override
    public Optional<Product> getById(Long id) {
    	try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_FIND_BY_ID);
			st.setLong(1, id);
			st.execute();
			List<Product> list = DBUtils.resultSetToProductList(st.getResultSet());
			return list.isEmpty() ? Optional.ofNullable(null) : Optional.of(list.get(0));
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
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
    	try {
			Statement st = connection.createStatement();
			st.executeUpdate(SQLQueries.PRODUCT_DELETE_ALL);
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}	
    }

    @Override
    public boolean deleteByName(String name) {
    	try {
    		PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_DELETE_BY_NAME);
			st.setString(1, name);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }
    
    @Override
    public boolean deleteOfGroup(Long groupId) {
    	try {
    		PreparedStatement st = connection.prepareStatement(SQLQueries.PRODUCT_DELETE_BY_GROUP_ID);
			st.setLong(1, groupId);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }

    @Override
    public synchronized boolean existsWithName(String name) {
        return existsWithName(name, SQLQueries.PRODUCT_FIND_ALL_BY_NAME);
    }

    @Override
    public synchronized boolean existsWithId(Long id) {
        return existsWithId(id, SQLQueries.PRODUCT_FIND_ALL_BY_ID);
    }

    @Override
    public synchronized List<Product> filterByCriteria(Map<FilterCriteria, Object> criteria) {
        Session session = HibernateUtil.getHibernateSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cr = cb.createQuery(Product.class);
        Root<Product> root = cr.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();
        if (criteria.containsKey(FilterCriteria.SEARCH_STRING)) {
            String str = ((String) criteria.get(FilterCriteria.SEARCH_STRING)).toLowerCase();
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), "%"+str+"%"),
                    cb.like(cb.lower(root.get("description")), "%"+str+"%"),
                    cb.like(cb.lower(root.get("producer")), "%"+str+"%")
                    ));
        }
        if (criteria.containsKey(FilterCriteria.MIN_QUANTITY)) {
            int minQuant = Integer.parseInt((String) criteria.get(FilterCriteria.MIN_QUANTITY));
            predicates.add(cb.ge(root.get("quantity"), minQuant));
        }
        if (criteria.containsKey(FilterCriteria.MAX_QUANTITY)) {
            int maxQuant = Integer.parseInt((String) criteria.get(FilterCriteria.MAX_QUANTITY));
            predicates.add(cb.le(root.get("quantity"), maxQuant));
        }
        if (criteria.containsKey(FilterCriteria.MIN_PRICE)) {
            double minPrice = Double.parseDouble(criteria.get(FilterCriteria.MIN_PRICE).toString());
            predicates.add(cb.ge(root.get("price"), minPrice));
        }
        if (criteria.containsKey(FilterCriteria.MAX_PRICE)) {
            double maxPrice = Double.parseDouble(criteria.get(FilterCriteria.MAX_PRICE).toString());
            predicates.add(cb.le(root.get("price"), maxPrice));
        }
        if (criteria.containsKey(FilterCriteria.GROUP_ID)) {
            long groupId = Long.parseLong(criteria.get(FilterCriteria.GROUP_ID).toString());
            predicates.add(cb.equal(root.get("groupId"), groupId));
        }

        cr.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(root.get("name")));

        Query<Product> query = session.createQuery(cr);
        return query.getResultList();
    }


    public List<Product> listByCriteria(Map<SortCriteria, SortOrder> sortMap) {
        if (sortMap.isEmpty()) {
            return getAll();
        }
        try{
            Statement st = connection.createStatement();
            st.execute(constructSortStatement(sortMap));
            return DBUtils.resultSetToProductList(st.getResultSet());
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }

    private String constructSortStatement(Map<SortCriteria, SortOrder> sortMap) {
        List<String> sortStrings = new ArrayList<>();
        for (SortCriteria sortCriteria: sortMap.keySet()) {
            sortStrings.add(sortCriteria+" "+sortMap.get(sortCriteria));
        }
        return SQLQueries.PRODUCT_ORDER_BY_BASE + String.join(",", sortStrings)+";";
    }
    
}
