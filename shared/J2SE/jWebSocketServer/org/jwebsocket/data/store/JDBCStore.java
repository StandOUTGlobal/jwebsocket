package org.jwebsocket.data.store;

import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.jwebsocket.api.data.Store;

/**
 * Implementation of the <code>Store</code> interface that stores jWebSocket
 * component data in the database.
 * 
 * @author puran
 * @version $Id$
 */
public class JDBCStore implements Store {

  /**
   * The descriptive information about this implementation.
   */
  protected static final String info = "JDBCStore/1.0";

  /**
   * Name to register for this Store, used for logging.
   */
  protected static String storeName = "JDBCStore";

  /**
   * Name to register for the background thread.
   */
  protected String threadName = "JDBCStore";

  /**
   * The connection username to use when trying to connect to the database.
   */
  protected String connectionName = null;

  /**
   * The connection URL to use when trying to connect to the database.
   */
  protected String connectionPassword = null;

  /**
   * Connection string to use when connecting to the DB.
   */
  protected String connectionURL = null;

  /**
   * The database connection.
   */
  private Connection dbConnection = null;

  /**
   * Instance of the JDBC Driver class we use as a connection factory.
   */
  protected Driver driver = null;

  /**
   * Driver to use.
   */
  protected String driverName = null;

  /**
   * Table to use
   */
  protected String tableName = null;

  /**
   * key column name
   */
  protected String keyColumnName = "store_key";

  /**
   * value column name
   */
  protected String valueColumnName = "store_value";

  /**
   * app column name
   */
  protected String appColumnName = "store_app_key";

  // ------------------------------------------------------------- SQL Variables

  /**
   * Variable to hold the <code>getSize()</code> prepared statement.
   */
  protected PreparedStatement preparedSizeSql = null;

  /**
   * Variable to hold the <code>keys()</code> prepared statement.
   */
  protected PreparedStatement preparedKeysSql = null;

  /**
   * Variable to hold the <code>save()</code> prepared statement.
   */
  protected PreparedStatement preparedSaveSql = null;

  /**
   * Variable to hold the <code>clear()</code> prepared statement.
   */
  protected PreparedStatement preparedClearSql = null;

  /**
   * Variable to hold the <code>remove()</code> prepared statement.
   */
  protected PreparedStatement preparedRemoveSql = null;

  /**
   * Variable to hold the <code>load()</code> prepared statement.
   */
  protected PreparedStatement preparedLoadSql = null;

  /**
   * The property change support for this component.
   */
  protected PropertyChangeSupport support = new PropertyChangeSupport(this);

  /**
   * Return the info for this Store.
   */
  @Override
  public String getInfo() {
    return (info);
  }

  /**
   * Set the driver for this Store.
   * 
   * @param driverName
   *          The new driver
   */
  public void setDriverName(String driverName) {
    String oldDriverName = this.driverName;
    this.driverName = driverName;
    support.firePropertyChange("driverName", oldDriverName, this.driverName);
    this.driverName = driverName;
  }

  /**
   * Return the driver for this Store.
   */
  public String getDriverName() {
    return (this.driverName);
  }

  /**
   * Return the username to use to connect to the database.
   * 
   */
  public String getConnectionName() {
    return connectionName;
  }

  /**
   * Set the username to use to connect to the database.
   * 
   * @param connectionName
   *          Username
   */
  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  /**
   * Return the password to use to connect to the database.
   * 
   */
  public String getConnectionPassword() {
    return connectionPassword;
  }

  /**
   * Set the password to use to connect to the database.
   * 
   * @param connectionPassword
   *          User password
   */
  public void setConnectionPassword(String connectionPassword) {
    this.connectionPassword = connectionPassword;
  }

  /**
   * Set the Connection URL for this Store.
   * 
   * @param connectionURL
   *          The new Connection URL
   */
  public void setConnectionURL(String connectionURL) {
    String oldConnString = this.connectionURL;
    this.connectionURL = connectionURL;
    support.firePropertyChange("connectionURL", oldConnString, this.connectionURL);
  }

  /**
   * Return the Connection URL for this Store.
   */
  public String getConnectionURL() {
    return (this.connectionURL);
  }

  /**
   * Set the table for this Store.
   * 
   * @param table
   *          The new table
   */
  public void setTableName(String table) {
    this.tableName = table;
  }

  /**
   * Return the table for this Store.
   */
  public String getTableName() {
    return this.tableName;
  }

  // --------------------------------------------------------- Public Methods

  /**
   * @return the keyColumnName
   */
  public String getKeyColumnName() {
    return keyColumnName;
  }

  /**
   * @param keyColumnName
   *          the keyColumnName to set
   */
  public void setKeyColumnName(String keyColumnName) {
    this.keyColumnName = keyColumnName;
  }

  /**
   * @return the appColumnName
   */
  public String getAppColumnName() {
    return appColumnName;
  }

  /**
   * @param appColumnName
   *          the appColumnName to set
   */
  public void setAppColumnName(String appColumnName) {
    this.appColumnName = appColumnName;
  }

  /**
   * @return the valueColumnName
   */
  public String getValueColumnName() {
    return valueColumnName;
  }

  /**
   * @param valueColumnName
   *          the valueColumnName to set
   */
  public void setValueColumnName(String valueColumnName) {
    this.valueColumnName = valueColumnName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] keys() {
    ResultSet rst = null;
    String keys[] = null;
    synchronized (this) {
      int numberOfTries = 2;
      while (numberOfTries > 0) {

        Connection _conn = getConnection();
        if (_conn == null) {
          return (new String[0]);
        }
        try {
          if (preparedKeysSql == null) {
            String keysSql = "SELECT " + keyColumnName + " FROM " + tableName + " WHERE " + appColumnName + " = ?";
            preparedKeysSql = _conn.prepareStatement(keysSql);
          }

          preparedKeysSql.setString(1, getAppColumnName());
          rst = preparedKeysSql.executeQuery();
          ArrayList<String> tmpkeys = new ArrayList<String>();
          if (rst != null) {
            while (rst.next()) {
              tmpkeys.add(rst.getString(1));
            }
          }
          keys = tmpkeys.toArray(new String[tmpkeys.size()]);
          // Break out after the finally block
          numberOfTries = 0;
        } catch (SQLException e) {
          // TODO: LOG ERROR
          keys = new String[0];
          // Close the connection so that it gets reopened next time
          if (dbConnection != null)
            close(dbConnection);
        } finally {
          try {
            if (rst != null) {
              rst.close();
            }
          } catch (SQLException e) {
            // Ignore
          }
          release(_conn);
        }
        numberOfTries--;
      }
    }
    return (keys);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSize() {
    int size = 0;
    ResultSet rst = null;
    synchronized (this) {
      int numberOfTries = 2;
      while (numberOfTries > 0) {
        Connection _conn = getConnection();
        if (_conn == null) {
          return (size);
        }
        try {
          if (preparedSizeSql == null) {
            String sizeSql = "SELECT COUNT(" + keyColumnName + ") FROM " + tableName + " WHERE " + appColumnName + " = ?";
            preparedSizeSql = _conn.prepareStatement(sizeSql);
          }

          preparedSizeSql.setString(1, getAppColumnName());
          rst = preparedSizeSql.executeQuery();
          if (rst.next()) {
            size = rst.getInt(1);
          }
          // Break out after the finally block
          numberOfTries = 0;
        } catch (SQLException e) {
          // TODO: log error
          if (dbConnection != null) {
            close(dbConnection);
          }
        } finally {
          try {
            if (rst != null)
              rst.close();
          } catch (SQLException e) {
            // Ignore
          }

          release(_conn);
        }
        numberOfTries--;
      }
    }
    return (size);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object get(String key) {
    ResultSet rst = null;
    ObjectInputStream ois = null;
    BufferedInputStream bis = null;
    Object returnObj = null;
    synchronized (this) {
      int numberOfTries = 2;
      while (numberOfTries > 0) {
        Connection _conn = getConnection();
        if (_conn == null) {
          return (null);
        }
        try {
          if (preparedLoadSql == null) {
            String loadSql = "SELECT " + valueColumnName + " FROM " + tableName + " WHERE " + keyColumnName + " = ? AND " + appColumnName + " = ?";
            preparedLoadSql = _conn.prepareStatement(loadSql);
          }
          preparedLoadSql.setString(1, key);
          preparedLoadSql.setString(2, getAppColumnName());
          rst = preparedLoadSql.executeQuery();
          if (rst.next()) {
            bis = new BufferedInputStream(rst.getBinaryStream(2));
            ois = new ObjectInputStream(bis);
            returnObj = ois.readObject();
          }
          // Break out after the finally block
          numberOfTries = 0;
        } catch (Exception e) {
          // TODO: log the error
          if (dbConnection != null)
            close(dbConnection);
        } finally {
          try {
            if (rst != null) {
              rst.close();
            }
          } catch (SQLException e) {
            // Ignore
          }
          if (ois != null) {
            try {
              ois.close();
            } catch (IOException e) {
              // Ignore
            }
          }
          release(_conn);
        }
        numberOfTries--;
      }
    }

    return returnObj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove(String key) {
    synchronized (this) {
      int numberOfTries = 2;
      while (numberOfTries > 0) {
        Connection _conn = getConnection();

        if (_conn == null) {
          return;
        }

        try {
          if (preparedRemoveSql == null) {
            String removeSql = "DELETE FROM " + tableName + " WHERE " + keyColumnName + " = ?  AND " + appColumnName + " = ?";
            preparedRemoveSql = _conn.prepareStatement(removeSql);
          }

          preparedRemoveSql.setString(1, key);
          preparedRemoveSql.setString(2, getAppColumnName());
          preparedRemoveSql.execute();
          // Break out after the finally block
          numberOfTries = 0;
        } catch (SQLException e) {
          // TODO: log error please
          if (dbConnection != null)
            close(dbConnection);
        } finally {
          release(_conn);
        }
        numberOfTries--;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    synchronized (this) {
      int numberOfTries = 2;
      while (numberOfTries > 0) {
        Connection _conn = getConnection();
        if (_conn == null) {
          return;
        }
        try {
          if (preparedClearSql == null) {
            String clearSql = "DELETE FROM " + tableName + " WHERE " + appColumnName + " = ?";
            preparedClearSql = _conn.prepareStatement(clearSql);
          }
          preparedClearSql.setString(1, getAppColumnName());
          preparedClearSql.execute();
          // Break out after the finally block
          numberOfTries = 0;
        } catch (SQLException e) {
          // TODO: log error
          if (dbConnection != null)
            close(dbConnection);
        } finally {
          release(_conn);
        }
        numberOfTries--;
      }
    }
  }

  /**
   * Save a session to the Store.
   * 
   * @param session
   *          the session to be stored
   * @exception IOException
   *              if an input/output error occurs
   */
  public boolean put(String key, Object data) {
    ObjectOutputStream oos = null;
    ByteArrayOutputStream bos = null;
    ByteArrayInputStream bis = null;
    InputStream in = null;
    boolean result = false;
    synchronized (this) {
      int numberOfTries = 2;
      while (numberOfTries > 0) {
        Connection _conn = getConnection();
        if (_conn == null) {
          return false;
        }
        // Check if ID exists in database and if so use UPDATE.
        remove(key);
        try {
          bos = new ByteArrayOutputStream();
          oos = new ObjectOutputStream(new BufferedOutputStream(bos));
          oos.writeObject(data);
          oos.close();
          oos = null;
          byte[] obs = bos.toByteArray();
          int size = obs.length;
          bis = new ByteArrayInputStream(obs, 0, size);
          in = new BufferedInputStream(bis, size);

          if (preparedSaveSql == null) {
            String saveSql = "INSERT INTO " + tableName + " (" + keyColumnName + ", " + valueColumnName + ", " + appColumnName + ") VALUES (?, ?, ?)";
            preparedSaveSql = _conn.prepareStatement(saveSql);
          }

          preparedSaveSql.setString(1, key);
          preparedSaveSql.setBinaryStream(2, in, size);
          preparedSaveSql.setString(3, getAppColumnName());
          preparedSaveSql.execute();
          // Break out after the finally block
          numberOfTries = 0;
          result = true;
        } catch (SQLException e) {
          // TODO: log error
          if (dbConnection != null)
            close(dbConnection);
        } catch (IOException e) {
          // Ignore
        } finally {
          try {
            if (oos != null) {
              oos.close();
            }
            if (bis != null) {
              bis.close();
            }
            if (in != null) {
              in.close();
            }
          } catch (IOException e) {
            // nothing we can do just return false
            result = false;
          }
          release(_conn);
        }
        numberOfTries--;
      }
      return result;
    }
  }

  // --------------------------------------------------------- Protected Methods

  /**
   * Check the connection associated with this store, if it's <code>null</code>
   * or closed try to reopen it. Returns <code>null</code> if the connection
   * could not be established.
   * 
   * @return <code>Connection</code> if the connection succeeded
   */
  protected Connection getConnection() {
    try {
      if (dbConnection == null || dbConnection.isClosed()) {
        // TODO: log info level
        open();
        if (dbConnection == null || dbConnection.isClosed()) {
          // TODO: log info level fail
        }
      }
    } catch (SQLException ex) {
      // TODO: log error
    }

    return dbConnection;
  }

  /**
   * Open (if necessary) and return a database connection for use by this Realm.
   * 
   * @exception SQLException
   *              if a database error occurs
   */
  protected Connection open() throws SQLException {
    // Do nothing if there is a database connection already open
    if (dbConnection != null)
      return (dbConnection);

    // Instantiate our database driver if necessary
    if (driver == null) {
      try {
        Class<?> clazz = Class.forName(driverName);
        driver = (Driver) clazz.newInstance();
      } catch (ClassNotFoundException ex) {
        // TODO: log error
      } catch (InstantiationException ex) {
        // TODO: log error
      } catch (IllegalAccessException ex) {
        // TODO: log error
      }
    }
    // Open a new connection
    Properties props = new Properties();
    if (connectionName != null)
      props.put("user", connectionName);
    if (connectionPassword != null)
      props.put("password", connectionPassword);
    dbConnection = driver.connect(connectionURL, props);
    dbConnection.setAutoCommit(true);
    return (dbConnection);

  }

  public void handleThrowable(Throwable t) {
    if (t instanceof ThreadDeath) {
      throw (ThreadDeath) t;
    }
    if (t instanceof VirtualMachineError) {
      throw (VirtualMachineError) t;
    }
    // All other instances of Throwable will be silently swallowed
  }

  /**
   * Close the specified database connection.
   * 
   * @param dbConnection
   *          The connection to be closed
   */
  protected void close(Connection dbConnection) {
    // Do nothing if the database connection is already closed
    if (dbConnection == null)
      return;

    // Close our prepared statements (if any)
    try {
      preparedSizeSql.close();
    } catch (Throwable f) {
      handleThrowable(f);
    }
    this.preparedSizeSql = null;

    try {
      preparedKeysSql.close();
    } catch (Throwable f) {
      handleThrowable(f);
    }
    this.preparedKeysSql = null;

    try {
      preparedSaveSql.close();
    } catch (Throwable f) {
      handleThrowable(f);
    }
    this.preparedSaveSql = null;

    try {
      preparedClearSql.close();
    } catch (Throwable f) {
      handleThrowable(f);
    }

    try {
      preparedRemoveSql.close();
    } catch (Throwable f) {
      handleThrowable(f);
    }
    this.preparedRemoveSql = null;

    try {
      preparedLoadSql.close();
    } catch (Throwable f) {
      handleThrowable(f);
    }
    this.preparedLoadSql = null;

    // Close this database connection, and log any errors
    try {
      dbConnection.close();
    } catch (SQLException e) {
      // TODO:log error here
    } finally {
      this.dbConnection = null;
    }

  }

  /**
   * Release the connection, not needed here since the connection is not
   * associated with a connection pool.
   * 
   * @param conn
   *          The connection to be released
   */
  protected void release(Connection conn) {
    // NOOP
  }
}
