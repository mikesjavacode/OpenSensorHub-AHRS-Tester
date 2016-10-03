
package attTester;

/**
 * This is the thread-safe communications link between the AttGraphicsContainer
 * (contains the GUI elements for setting up and running the simulation) and 
 * AttRecv (receives and processes the AHRS data packet).
 * 
 * @author Mike Fouche
 */
public class ThreadQueueRcv 
{
    // True if the AttRecv is to remain active.
    private boolean ahrsTesterActive;
    
    /**
     * Constructor
     */
    public ThreadQueueRcv()
    {
        ahrsTesterActive = true;
    }
    
    /**
     * Sets the AttRecv thread status.
     * 
     * @param aTA aTA Status (active or not) of the AttRecv thread.
     */
    public synchronized void setTesterStatus(boolean aTA)
    {
        ahrsTesterActive = aTA;
    }        
    
    /**
     * Gets the AttRecv thread status.
     * 
     * @return Status (active or not) of the AttRecv thread.
     */
    public synchronized boolean getTesterStatus()
    {
        return ahrsTesterActive;
    }
    
} // end of class ThreadQueueRcv
