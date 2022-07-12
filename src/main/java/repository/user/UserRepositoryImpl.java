package repository.user;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import exceptions.SQLExceptionRuntime;
import model.User;
import repository.AbstractRepository;
import utils.DBUtils;
import utils.SQLQueries;

public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

	private UserRepositoryImpl(String databaseName) {
        super(SQLQueries.CREATE_USER_TABLE, databaseName);
        this.initData();
    }

    private static volatile UserRepositoryImpl userRepository;

    public static UserRepositoryImpl getInstance() {
        return getInstance(DBUtils.PROD_DB);
    }

    public static UserRepositoryImpl getInstance(String databaseName) {
        UserRepositoryImpl repository = userRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (UserRepositoryImpl.class) {
            if (userRepository == null) {
                userRepository = new UserRepositoryImpl(databaseName);
            }
            return userRepository;
        }
    }
	

	@Override
	public Optional<User> getByUsername(String username) {
		try {
			PreparedStatement st = connection.prepareStatement(SQLQueries.USER_FIND_BY_USERNAME);
			st.setString(1, username);
			st.execute();
			List<User> list = DBUtils.resultSetToUserList(st.getResultSet());
			return list.isEmpty() ? Optional.ofNullable(null) : Optional.of(list.get(0));
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
	}
	
	@Override
	public List<User> getAll() {
        try {
        	Statement st = connection.createStatement();
			st.execute(SQLQueries.USER_FIND_ALL);
			return DBUtils.resultSetToUserList(st.getResultSet());
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
	}
	
	
	private void initData() {
        try {
        	Statement st = connection.createStatement();
            st.execute(SQLQueries.USER_FIND_ALL);
            if(st.getResultSet().next())
            	return;
        	st.clearBatch();
            st.executeUpdate(SQLQueries.INSERT_HARDCODED_USERS);
		} catch (SQLException e) {
			throw new SQLExceptionRuntime(e);
		}
	}

	

}
