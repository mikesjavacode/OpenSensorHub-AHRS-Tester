
package attTester;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/** This class acts as an interface to the MySQL database for creating
 * tables, loading tables, and retrieving the data from tables.
 * 
 * @author Michael Fouche
 */
public class ConnectMySQL 
{
    private Statement stmt;
    private Connection conn;
    private ResultSet resultSet;
    private DatabaseMetaData metadata;
    private PreparedStatement ps;
    
    private final String connectionUrl;
    
    private boolean fastLoad;
    
    /**
     * Constructor
     */
    public ConnectMySQL()
    {
        fastLoad = false;
        connectionUrl = "jdbc:mysql://localhost/mysql?" +
                           "user=root&password=tridvola";
    }        
    
    //--------------------------------------------------------------------------
    /** 
    * Sets up a MySQL table - there is no argument list.
    * <p>
    * 1.  Connects to the existing MySQL server,
    * <p>
    * 2.  If the table exists then it is deleted and a new one is created,
    * <p>
    * 3.  Columns are created with the titles and data types.
    * 
    */
    public void createTable()
    {
        try 
        {
            // Only needed for this method since it always 
            // attempts to connect to MySQL before the other methods
            Class.forName("com.mysql.jdbc.Driver");
            
            // Connect to the MySQL database
            conn = DriverManager.getConnection(connectionUrl);
            metadata = conn.getMetaData();
            
            // Query to get table
            resultSet = metadata.getTables(null, null, "AttData", null);
            
            // If this is true, then the table exists ...
            if ( resultSet.next() )
            {
                DataStore.getSInstc().setTextSys("Table exists ...\n" );

                stmt = conn.createStatement();
                // Remove the table from the MySQL database
                stmt.executeUpdate("DROP Table AttData");
                DataStore.getSInstc().setTextSys("Deleting existing table ...\n" );
            }
            else
            {    
                DataStore.getSInstc().setTextSys("Table does NOT exist ...\n" );
            }
            
            DataStore.getSInstc().setTextSys("Creating new table ...\n" );

            // Create a new table in the MySQL database
            conn.createStatement().execute("CREATE TABLE `AttData` ("+ 
            "`id` int(64) NOT NULL AUTO_INCREMENT,"+ 
            "`Roll` FLOAT,"+        // column for Roll
            "`Pitch` FLOAT," +      // column for Pitch
            "`Heading` FLOAT," +    // column for Heading
            " PRIMARY KEY ( id ))"); 

            resultSet = metadata.getTables(null, null, "AttData", null);
            
            // Close the JDBC connection.
            conn.close(); 
        
        } 
        catch (SQLException e) 
        {
                System.out.println("SQL Exception: "+ e.toString());
        } 
        catch (ClassNotFoundException cE) 
        {
                System.out.println("Class Not Found Exception: "+
                                                cE.toString());
        }
        
    } 
    
    //--------------------------------------------------------------------------
    /** 
    * Retrieves the attitude data from the MySQL table for playback.
    * <p>
    * 1.  Connects to the existing MySQL server.
    * <p>
    * 2.  Retrieves the attitude data and loads it into an array in DataStore
    *  
    */
    public void readTable()
    {
        try 
        {
            // Local variables
            int inum = 0;
            double[] rpydata = new double[3];
            
            // Connect to the MySQL database
            conn = DriverManager.getConnection(connectionUrl);
            
            // ??
            metadata = conn.getMetaData();
            
            // Query to get table
            resultSet = metadata.getTables(null, null, "AttData", null);
            
            // Pull all of the columns from the table.
            ResultSet rs = conn.createStatement().
                    executeQuery("SELECT * FROM `AttData` ");

            while ( rs.next() )
            {
                // Retrieve the attitude data.
                rpydata[0] = (double)rs.getDouble("Roll"); 
                rpydata[1] = (double)rs.getDouble("Pitch");
                rpydata[2] = (double)rs.getDouble("Heading");
                
                // Store the data in the Singleton common data object
                DataStore.getSInstc().setStateArray(inum, rpydata);

                inum ++;
            }   

            DataStore.getSInstc().setIloop(inum-1);
            
            // Close the JDBC connection to the MySQL database.
            conn.close(); 

        } 
        catch (SQLException e) 
        {
                System.out.println("SQL Exception: "+ e.toString());
        } 
        
    } 

     //--------------------------------------------------------------------------
   /** 
    * Loads the attitude data to the MySQL table
    * <p>
    * 1.  Connects to the existing MySQL server.
    * <p>
    * 2.  Retrieves the attitude data from DataStore and inserts 
    * it into the table with batch commands.
    */
    public void loadTable()
    {
        try 
        {
            double[] rpydata = new double[3];
            float roll, pitch, heading;
            int numrec;
            
            fastLoad = true;
            
            // Connect to the MySQL database.
            conn = DriverManager.getConnection(connectionUrl);
            
            // This is what takes loading time down 
            // from 20-40 seconds to 2 seconds!
            conn.setAutoCommit(false);
            
            // ??
            metadata = conn.getMetaData();
            
            if ( fastLoad )
            {    
                stmt = conn.createStatement();
                
                // Create the MySQL command for loading the data into the table
                String sql = "insert into AttData (Roll, Pitch, Heading)"+
                                                " values (?, ?, ?)";
                // ??
                ps = conn.prepareStatement(sql);
            }
            
            // Query to get table
            resultSet = metadata.getTables(null, null, "AttData", null);

            int i = 0;
            int endSim;
            endSim = DataStore.getSInstc().getSizeStateArray();
            
            // Load the flight data into the table
            while (i < (endSim-1) )            
            {
                // Retrieve the attitude data from the Singleton common data object.
                rpydata = DataStore.getSInstc().getStateArray(i);
                
                roll    = (float)rpydata[0];
                pitch   = (float)rpydata[1];
                heading = (float)rpydata[2];
                
                if (!fastLoad)
                {    
                    conn.createStatement().executeUpdate("INSERT INTO"+""
                            + " `AttData` " +
                            "(`Roll`,`Pitch`, `Heading`) VALUES ("+
                            roll+","+pitch+","+heading+")"); 
                }
                else
                {
                    ps.setFloat(1, roll);
                    ps.setFloat(2, pitch);
                    ps.setFloat(3, heading);
                    ps.addBatch();                    
                }
                
                i ++;
                
            } // end of for-loop
            
            if ( fastLoad )
            {
                ps.executeBatch();
                conn.commit();
                ps.close();
            }
            
            DataStore.getSInstc().setTextSys("Finished loading MySQL table - "+
                             "closing connection ...\n" );
            DataStore.getSInstc().setTextCaretPosition();
            
            // Clear out the state array
            DataStore.getSInstc().clearStateArray();
            
            // Close the JDBC connection to the MySQL database.
            conn.close(); 
            
        } 
        catch (SQLException e) 
        {
                System.out.println("SQL Exception: "+ e.toString());
        } 
        
    } 

}  // end of class ConnectMySQL
