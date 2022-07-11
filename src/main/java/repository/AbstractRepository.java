package repository;

import exceptions.SQLExceptionRuntime;
import utils.DBUtils;

import java.sql.*;

public abstract class AbstractRepository {
	
    protected static Connection connection;

    protected AbstractRepository(String initTableStatement, String databaseName) {
        init(initTableStatement, databaseName);
    }


    private void init(String initTableStatement, String databaseName) {
        try{
            connection = DBUtils.getDBConnection(databaseName);
            PreparedStatement st = connection.prepareStatement(initTableStatement);
            st.executeUpdate();
            //this.enableFKs();
        }catch(ClassNotFoundException e){
            System.out.println("Could not find JDBC driver");
            e.printStackTrace();
            System.exit(0);
        }catch (SQLException e){
            System.out.println("Wrong SQL statement");
            e.printStackTrace();
            System.exit(0);
        }
    }

    
    protected boolean existsWithName(String name, String findAllByNameQuery) {
        try{
            PreparedStatement st = connection.prepareStatement(findAllByNameQuery);
            st.setString(1, name.toLowerCase());
            st.execute();
            ResultSet set = st.getResultSet();
            return set.next();
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }

    protected boolean existsWithNameAndNotId(String name, Long id, String idColName, String findAllByNameQuery) {
        try{
            PreparedStatement st = connection.prepareStatement(findAllByNameQuery);
            st.setString(1, name.toLowerCase());
            st.execute();
            ResultSet set = st.getResultSet();
            int count = 0;
            boolean match = false;
            while (set.next()) {
                count++;
                if (set.getLong(idColName) == id) {
                    match = true;
                }
            }
            return count != 0 && (count != 1 || !match);
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }



    protected boolean existsWithId(Long id, String findAllByIdQuery) {
        try{
            PreparedStatement st = connection.prepareStatement(findAllByIdQuery);
            st.setLong(1, id);
            st.execute();
            ResultSet set = st.getResultSet();
            return set.next();
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }

    protected void deleteAll(String deleteAllStatement) {
        try{
            Statement st = connection.createStatement();
            st.executeUpdate(deleteAllStatement);
        } catch (SQLException e){
            throw new SQLExceptionRuntime(e);
        }
    }
    
}
