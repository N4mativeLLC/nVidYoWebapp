package org.nvidyo.webapp.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

public class ConnectionPool {
	
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    
	//public static String URL = "jdbc:mysql://192.168.1.71:3306/";
    //public static final String USERNAME = "liamapp";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "Gauch022$";
    
    //private GenericObjectPool connectionPool = null;
    private static ObjectPool<PoolableConnection> connectionPool = null;
    private static Object obj = new Object();
    private static DataSource dataSource = null;
    public static Class driverClass;
    
    public ConnectionPool() {

    }
    
    public ConnectionPool(String dbname) {
    	
		synchronized (obj) {
			try {
					if (connectionPool == null || dataSource == null) {

					dataSource = setUp(dbname);
					//LOG.info("MySQL connection created");
					//System.out.println("MySQL connection created");
				} 
			} catch (Exception e) {
				//LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
    
    public ConnectionPool(String host, String dbname) {
    	
		synchronized (obj) {
			try {
					if (connectionPool == null || dataSource == null) {

					dataSource = setUp(host, dbname);
					//LOG.info("MySQL connection created");
					//System.out.println("MySQL connection created");
				} 
			} catch (Exception e) {
				//LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
    public ConnectionPool(String host, String dbname, String userName) {
    	
		synchronized (obj) {
			try {
					if (connectionPool == null || dataSource == null) {

					dataSource = setUp(host, dbname,userName);
					//LOG.info("MySQL connection created");
					//System.out.println("MySQL connection created");
				} 
			} catch (Exception e) {
				//LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

    private DataSource setUp(String host, String database) throws Exception {
    	
    	System.out.println(host);
    	String URL = String.format("jdbc:mysql://%s:3306/%s?characterEncoding=latin1&useConfigs=maxPerformance", host, database); //jdbc:mysql://192.168.1.71:3306
    	System.out.println(URL);
    	
		registerJDBCDriver(MYSQL_DRIVER);

		ConnectionFactory connectionFactory = 
			getConnFactory(URL, USERNAME, PASSWORD);
		
		PoolableConnectionFactory poolfactory = new PoolableConnectionFactory(
				connectionFactory, null);

        connectionPool = new GenericObjectPool<PoolableConnection>(poolfactory);

        poolfactory.setPool(connectionPool);
        
        PoolingDriver dbcpDriver = PoolConnectionFactory.getDBCPDriver();

        dbcpDriver.registerPool("LIAM-Connection pool", connectionPool);

        return new PoolingDataSource(connectionPool);
    }
    private DataSource setUp(String host, String database,String userName) throws Exception {
    	
    	System.out.println(host);
    	String URL = String.format("jdbc:mysql://%s:3306/%s?characterEncoding=latin1&useConfigs=maxPerformance", host, database); //jdbc:mysql://192.168.1.71:3306
    	System.out.println(URL);
    	
		registerJDBCDriver(MYSQL_DRIVER);

		ConnectionFactory connectionFactory = 
			getConnFactory(URL, userName, PASSWORD);
		
		PoolableConnectionFactory poolfactory = new PoolableConnectionFactory(
				connectionFactory, null);

        connectionPool = new GenericObjectPool<PoolableConnection>(poolfactory);

        poolfactory.setPool(connectionPool);
        
        PoolingDriver dbcpDriver = PoolConnectionFactory.getDBCPDriver();

        dbcpDriver.registerPool("LIAM-Connection pool", connectionPool);

        return new PoolingDataSource(connectionPool);
    }
    private DataSource setUp(String database) throws Exception {
    	
    	String host = System.getenv("mysql.host");
    	//host = env.getProperty("mysql.host");
    	System.out.println(host);
    	String URL = String.format("jdbc:mysql://%s:3306/%s?characterEncoding=latin1&useConfigs=maxPerformance", host, database); //jdbc:mysql://192.168.1.71:3306
    	System.out.println(URL);
    	
		registerJDBCDriver(MYSQL_DRIVER);

		ConnectionFactory connectionFactory = 
			getConnFactory(URL, USERNAME, PASSWORD);
		
		PoolableConnectionFactory poolfactory = new PoolableConnectionFactory(
				connectionFactory, null);

        connectionPool = new GenericObjectPool<PoolableConnection>(poolfactory);

        poolfactory.setPool(connectionPool);
        
        PoolingDriver dbcpDriver = PoolConnectionFactory.getDBCPDriver();

        dbcpDriver.registerPool("LIAM-Connection pool", connectionPool);

        return new PoolingDataSource(connectionPool);
    }

    private ConnectionFactory getConnFactory(String connectionURI,
    		
    		            String user, String password) {
    		
    		        ConnectionFactory driverManagerConnectionFactory = new DriverManagerConnectionFactory(
    		
    		                connectionURI, user, password);
    		
    		        return driverManagerConnectionFactory;
    		
    		    }

    
    private static ObjectPool<PoolableConnection> getConnectionPool() {
        return connectionPool;
    }

    public static Connection getConnection(String dbName) throws IOException, SQLException {

		synchronized (obj) {
			new ConnectionPool(dbName);
		}
		printStatus();
		return dataSource.getConnection();
    }
    
    public static Connection getConnection(String host, String dbName) throws IOException, SQLException {

		synchronized (obj) {
			new ConnectionPool(host, dbName);
		}
		printStatus();
		return dataSource.getConnection();
    }
    public static Connection getConnection(String host, String dbName,String userName) throws IOException, SQLException {

		synchronized (obj) {
			new ConnectionPool(host, dbName,userName);
		}
		printStatus();
		return dataSource.getConnection();
    }
    public static Connection getConnection() throws IOException, SQLException {

		synchronized (obj) {
			//new ConnectionPool(dbName);
			return dataSource.getConnection();
		}
    }
    
    private void registerJDBCDriver(String driver) {
		try {
			driverClass = Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to find the driver class");
		}
	}
    /*
    public static void main(String[] args) throws Exception {
        ConnectionPool demo = new ConnectionPool("LIAM");
        //DataSource dataSource = demo.setUp("LIAM");
        //demo.printStatus();
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();
            demo.printStatus();

            stmt = conn.prepareStatement("SELECT * FROM heirarchy where id = 1");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("name: " + rs.getString("name"));
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        demo.printStatus();
    }
*/
    /**
     * Prints connection pool status.
     */
    private static void printStatus() {
        System.out.println("Max   : " + getConnectionPool().getNumIdle() + "; " +
                "Active: " + getConnectionPool().getNumActive() + "; " +
                "Idle  : " + getConnectionPool().getNumIdle());
    }
    
}
