
package attTester;

/**
 * This is the thread-safe communications link between AHRSRealTime (which loops
 * while receiving the AHRS data and sending it to the graphics objects) and 
 * AttRecv.
 * 
 * @author Mike Fouche
 */
public class Thread_Queue 
{
    // 
    private boolean toggleSet; // false by default
    // Holds the AHRS data - roll, pitch, and heading.
    private final double[] ahrs;
    
    /**
     * Constructor
     */
    public Thread_Queue()
    {
        ahrs = new double[3];
    }        
    
    /**
     * Called from AHRSRealTime to retrieve the AHRS data.
     * 
     * @return Attitude values to thread AHRSRealTime - roll, pitch, heading 
     */
    synchronized double[] get() 
    {
        if(!toggleSet)
        {    
            try 
            {
                wait(); 
            } 
            catch(InterruptedException e) 
            {
                System.out.println("InterruptedException caught");
            }
        }
        toggleSet = false;
      
        return ahrs;
   
    } 

    /**
     * Called from AttRecv to load the freshly received AHRS data.
     */
    synchronized void put(double roll, double pitch, double heading) 
    {
        toggleSet = true;
      
        ahrs[0] = roll;
        ahrs[1] = pitch;
        ahrs[2] = heading;

        notify();
   
    } 

} // end of class Thread_Queue
