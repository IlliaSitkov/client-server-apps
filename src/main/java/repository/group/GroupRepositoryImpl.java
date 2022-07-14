package repository.group;

import exceptions.SQLExceptionRuntime;
import model.Group;
import repository.AbstractRepository;
import utils.DBUtils;
import utils.SQLQueries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class GroupRepositoryImpl  extends AbstractRepository implements GroupRepository {



    private GroupRepositoryImpl(String databaseName) {
        super(SQLQueries.CREATE_GROUP_TABLE, databaseName);
    }


    private static volatile GroupRepositoryImpl groupRepository;

    public static GroupRepositoryImpl getInstance() {
        return getInstance(DBUtils.PROD_DB);
    }


    public static GroupRepositoryImpl getInstance(String databaseName) {
        GroupRepositoryImpl repository = groupRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (GroupRepositoryImpl.class) {
            if (groupRepository == null) {
                groupRepository = new GroupRepositoryImpl(databaseName);
            }
            return groupRepository;
        }
    }

    @Override
    public synchronized Group save(Group g) {
        try{
            PreparedStatement st = connection.prepareStatement(SQLQueries.GROUP_CREATE);
            st.setLong(1, g.getId());
            st.setString(2, g.getName());
            st.setString(3, g.getDescription());
            st.executeUpdate();
            return g;
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }

    @Override
    public synchronized Optional<Group> update(Group g) {
    	 try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.GROUP_UPDATE_BY_ID);
			st.setString(1, g.getName());
			st.setString(2, g.getDescription());
			st.setLong(3, g.getId());
			int res = st.executeUpdate();
			return res > 0 ? Optional.of(g) : Optional.ofNullable(null);
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }

    @Override
    public synchronized boolean delete(Long id) {
    	try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.GROUP_DELETE_BY_ID);
			st.setLong(1, id);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }

    @Override 
    public synchronized boolean deleteByName(String name) {
    	try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.GROUP_DELETE_BY_NAME);
			st.setString(1, name);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }
    
    @Override
    public synchronized Optional<Group> getById(Long id) {
    	try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.GROUP_FIND_BY_ID);
			st.setLong(1, id);
			st.execute();
			List<Group> list = DBUtils.resultSetToGroupList(st.getResultSet());
			return list.isEmpty() ? Optional.ofNullable(null) : Optional.of(list.get(0));
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }

    @Override
    public synchronized List<Group> getAll() {
    	try {
			Statement st = connection.createStatement();
			st.execute(SQLQueries.GROUPS_FIND_ALL);
			return DBUtils.resultSetToGroupList(st.getResultSet());
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
    }

    @Override
    public synchronized void deleteAll() {
        deleteAll(SQLQueries.GROUPS_DELETE_ALL);
    }

    @Override
    public synchronized boolean existsWithName(String name) {
        return existsWithName(name, SQLQueries.GROUP_FIND_ALL_BY_NAME);
    }

    @Override
    public synchronized boolean existsWithNameAndNotId(String name, Long id) {
        return existsWithNameAndNotId(name, id, "group_id", SQLQueries.GROUP_FIND_ALL_BY_NAME);
    }

    @Override
    public synchronized boolean existsWithId(Long id) {
        return existsWithId(id, SQLQueries.GROUP_FIND_BY_ID);
    }
}
