/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package games.strategy.engine.lobby.server.userDB;

import games.strategy.engine.framework.GameRunner;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.logging.*;

/**
 * Utility to get connections to the database.<p>
 * 
 * The database is embedded within the jvm.<p>
 * 
 * Getting a connection will cause the database (and the neccessary tables) to be created if it does not already exist.<p>
 * 
 * The database will be shutdown on System.exit through a shutdown hook.<p>
 * 
 * 
 * @author sgb
 */
class Database
{
    private final static Logger s_logger = Logger.getLogger(Database.class.getName());
    
    private static final Object s_dbSetupLock = new Object();
    private static boolean s_isDbSetup = false;
    private static boolean s_areDBTablesCreated = false;

    
    private static File getDerbyRootDir()
    {
        File root = GameRunner.getRootFolder();
        if(!root.exists())
        {
            throw new IllegalStateException("Root dir does not exist");
        }
        
        File derby = new File(root, "derby_db/database");
        if(!derby.exists())
        {
            if(!derby.mkdirs())
                throw new IllegalStateException("Could not create derby dir");
        }
        return derby;
        
    }
    
    static Connection getConnection()
    {
        ensureDbIsSetup();
        
        Connection conn = null;
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");

        /*
           The connection specifies create=true to cause
           the database to be created. To remove the database,
           remove the directory derbyDB and its contents.
           The directory derbyDB will be created under
           the directory that the system property
           derby.system.home points to, or the current
           directory if derby.system.home is not set.
         */
        String url = "jdbc:derby:ta_users;create=true";
        try
        {
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e)
        {
            s_logger.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("Could not create db connection");
        }
        
        ensureDbTablesAreCreated(conn);
        
        return conn;
    }

    /**
     * The connection passed in to this method is not closed, except in case of error.
     */
    private static void ensureDbTablesAreCreated(Connection conn)
    {
        synchronized(s_dbSetupLock)
        {
            try
            {
                if(s_areDBTablesCreated)
                    return;
                
                ResultSet rs = conn.getMetaData().getTables(null, null, "TA_USERS", null);
                
                if(rs.next())
                {
                    //we have the table, return
                    rs.close();
                    return;
                }
                else
                {
                    rs.close();
                }
                
                
                Statement s = conn.createStatement();
                s.execute("create table ta_users" +
                            "(" +
                            "userName varchar(40) NOT NULL PRIMARY KEY, " +
                            "password varchar(40) NOT NULL, " +
                            "email varchar(40) NOT NULL, " +
                            "joined timestamp NOT NULL, " +
                            "lastLogin timestamp NOT NULL, " +
                            "admin integer NOT NULL " +
                            ")"
                        );
                
                s_areDBTablesCreated = true;
            }
            catch(SQLException sqle)
            {
                //only close if an error occurs
                try
                {
                    conn.close();
                } catch (SQLException e)
                {}
                
                s_logger.log(Level.SEVERE, sqle.getMessage(), sqle);
                throw new IllegalStateException("Could not create tables");
            }
            
        }
        
    }

    /**
     * Set up folders and environment variables for database
     */
    private static void ensureDbIsSetup()
    {
        synchronized(s_dbSetupLock)
        {
            if(s_isDbSetup)
                return;
            
            //setup the derby location
            System.getProperties().setProperty("derby.system.home", getDerbyRootDir().getAbsolutePath());

            //load the driver
            try
            {
                String driver = "org.apache.derby.jdbc.EmbeddedDriver";
                Class.forName(driver).newInstance();
            } catch (Exception e)
            {
                s_logger.log(Level.SEVERE, e.getMessage() ,e);
                throw new Error("Could not load db driver");
            }
         
            //shut the database down on finish
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
                    {

                        public void run()
                        {
                            shutDownDB();
                        }}));
            
            s_isDbSetup = true;
        }
    
    }
    
    private static void shutDownDB()
    {
        try
        {
            DriverManager.getConnection("jdbc:derby:ta_users;shutdown=true");
        }
        catch (SQLException se)
        {
            if(se.getErrorCode() != 45000)
                s_logger.log(Level.WARNING, se.getMessage(), se);
        }

    }
}