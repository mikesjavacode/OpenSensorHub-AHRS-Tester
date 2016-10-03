
package attTester;

import panels.AircraftModelGraphic;
import panels.AttitudeGraphic;

/**
 *
 * @author Michael Fouche
 */

public class AHRSRealTime extends Thread 
{
    private final Thread_Queue TQ;
    private final AircraftModelGraphic ac;
    private final AttitudeGraphic ag;
    
    private double[] ahrs;
    private final int[] ahrs_data;
    
    /**
     *
     * @param TQ
     * @param ac
     * @param ag
     */
    public AHRSRealTime(Thread_Queue TQ, AircraftModelGraphic ac, 
                                        AttitudeGraphic ag)
    {
        this.TQ = TQ;
        this.ac = ac;
        this.ag = ag;
        
        ahrs = new double[3];
        ahrs_data = new int[3];
    }
    
    @Override
    public void run()
    {
        
        while (true)
        {    
           // Wait until AHRS returns fresh attitude data.
           ahrs = TQ.get(); 

           // Update AHRS attitude data
           ahrs_data[0] = (int)ahrs[0]; // roll
           ahrs_data[1] = (int)ahrs[1]; // pitch
           ahrs_data[2] = (int)ahrs[2]; // heading

           // Update attitude bar positions
           ag.ahrs_update(ahrs_data);
           // Update aircraft attitude model
           ac.updateModelDynamics(ahrs);

           // Update attitude text boxes with fresh attitude data
           DataStore.getSInstc().setRoll(String.format(" %5.1f \n",(float)ahrs[0] ) );
           DataStore.getSInstc().setPitch(String.format(" %5.1f \n",(float)ahrs[1] ) );
           DataStore.getSInstc().setHead(String.format(" %5.1f \n",(float)ahrs[2] ) );

           if (!DataStore.getSInstc().getAttLaunch())
           {
               DataStore.getSInstc().setAttLaunch(true);
               break;
           }
       }                                       
        
    } 
    
} // end of class AHRSRealTime
