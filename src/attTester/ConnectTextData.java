
package attTester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Michael Fouche
 */
public class ConnectTextData 
{
    private final String fin;
    private double[] rpydata;
    private float roll, pitch, heading;

    /**
     * Constructor
     */
    public ConnectTextData()
    {
        fin = "ACData.txt";
        
        rpydata = new double[3];
    }
    
    //--------------------------------------------------------------------------
    /**
     * Write the attitude data to a text file.
     */
    public void writeTextData()
    {
        int endSim;
        int i = 0;
        
        String attData;
        
        try
        {
            FileWriter fc = new FileWriter(fin);
            
            String r, p, h;
            
            // Get size of the data array
            endSim = DataStore.getSInstc().getSizeStateArray();

            // Load the flight data into the table
            while (i < (endSim-1) )            
            {
                // Retrieve the attitude data from the Singleton common data object.
                rpydata = DataStore.getSInstc().getStateArray(i);

                roll    = (float)rpydata[0];
                pitch   = (float)rpydata[1];
                heading = (float)rpydata[2];
                
                r = String.format("%5.2f",roll);
                p = String.format("%5.2f",pitch);
                h = String.format("%5.2f",heading);
                
                attData = r.concat(", ").
                        concat(p).concat(", ").
                        concat(String.valueOf(h));

                fc.write(attData+"\n");
                i++;
            }
            
            DataStore.getSInstc().setTextSys("Finished writing data to text file ...\n" );
            DataStore.getSInstc().setTextCaretPosition();

            fc.close();
        }
        catch(Exception e)
        {
            System.out.println("Problem writing text file - e = "+e);
        }    
    }
    
    //--------------------------------------------------------------------------
    /**
     * Read the attitude data from a text file.
     */
    public void readTextData()
    {
        int inum = 0;
        
        try
        {
            BufferedReader br;
            File file = new File(fin);
            br = new BufferedReader(new FileReader(file));
            String line; 
            while((line = br.readLine()) != null) 
            {
                // Since you told comma as separator.
                String[] cols = line.split(",");                 
                roll = Float.valueOf(cols[0]);
                pitch = Float.valueOf(cols[1]);
                heading = Float.valueOf(cols[2]);

                rpydata[0] = (double)roll;
                rpydata[1] = (double)pitch;
                rpydata[2] = (double)heading;
                
                // Store the data in the Singleton common data object
                DataStore.getSInstc().setStateArray(inum, rpydata);
                
                inum ++;
            }    
            
            br.close();
        }
        catch(IOException | NumberFormatException e)
        {
            System.out.println("Exception in readTextData - e = "+e);
        }    
    }
    
} // end of class ConnectTextData
