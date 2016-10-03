
package attTester;

import panels.AircraftModelGraphic;
import panels.AttitudeGraphic;

/**
 *
 * @author Michael Fouche
 */
public class AHRSPlayBack extends Thread
{
    private final AircraftModelGraphic ac;
    private final AttitudeGraphic ag;
    
    private double[] ahrs;
    private final int[] ahrs_data;
    
    /**
     *
     * @param ac
     * @param ag
     */
    public AHRSPlayBack(AircraftModelGraphic ac, AttitudeGraphic ag)
    {
        this.ac = ac;
        this.ag = ag;
        
        ahrs = new double[3];
        ahrs_data = new int[3];
    }
    
    public void run()
    {
        int simPass = 0;
        int i = 0;
        int endSim = 0;
        boolean runMySQL = true;
        
        while (runMySQL)
        {    
           if ( simPass == 0 )
           {
               if( DataStore.getSInstc().getUseTextFile() )
               {
                   ConnectTextData td = new ConnectTextData();
                   // Read the attitude data from the text file
                   td.readTextData();
               } 
               else
               {    
                   // Create instance of InterfaceMySQL
                   ConnectMySQL talkToMySQL = new ConnectMySQL();
                   // Read the attitude data from the MySQL table into the array
                   talkToMySQL.readTable();

                   DataStore.getSInstc().setTextSys("MySQL table retrieved ...\n" );
                   DataStore.getSInstc().setTextCaretPosition();
                   DataStore.getSInstc().setReadMySQL(false);
               }
               
               // Get the number of data elements to be used for playback
               endSim = DataStore.getSInstc().getSizeStateArray() - 2;
           
               simPass++;
           }
           
           try
           {
               ahrs = DataStore.getSInstc().getStateArray(i);
           }
           catch(Exception e)
           {
               System.out.println("AHRS exception - e = "+e+", i = "+i);
           }    
           ahrs_data[0] = (int)ahrs[0];
           ahrs_data[1] = (int)ahrs[1];
           ahrs_data[2] = (int)ahrs[2];

           // Update attitude bar positions
           ag.ahrs_update(ahrs_data);
           // Update aircraft attitude model
           ac.updateModelDynamics(ahrs);

           // Update attitude text boxes with fresh attitude data
           DataStore.getSInstc().setRoll(String.format(" %5.1f \n",(float)ahrs[0] ) );
           DataStore.getSInstc().setPitch(String.format(" %5.1f \n",(float)ahrs[1] ) );
           DataStore.getSInstc().setHead(String.format(" %5.1f \n",(float)ahrs[2] ) );
           
           if( i > endSim )
           {
               DataStore.getSInstc().setEndofMySQL(true);

               DataStore.getSInstc().setTextSys("End of attitude profile playback ... \n" );
               DataStore.getSInstc().setTextCaretPosition();
               runMySQL = false;
           }    

           // Simulate the time sampling of the AHRS.  Time is
           // set in SerialWriter.java
           try
           {
               Thread.sleep(50);
           }
           catch(InterruptedException e)
           {
               System.out.println("Thread sleep exception in AHRSTester");
           } 
           
           i++;
        }                                       

        // Clear out the state array
        DataStore.getSInstc().clearStateArray();
        
    } // end of method run
    
} // End of class AHRSPlayBack
