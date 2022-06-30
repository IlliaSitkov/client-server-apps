package repository.group;

import exceptions.SQLExceptionRuntime;
import model.Group;
import repository.AbstractRepository;
import utils.DBUtils;
import utils.SQLQueries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
    public Group update(Group g) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Group getById(Long id) {
        return null;
    }

    @Override
    public List<Group> getAll() {
        return null;
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
    public synchronized boolean existsWithId(Long id) {
        return existsWithId(id, SQLQueries.GROUP_FIND_ALL_BY_ID);
    }
}
