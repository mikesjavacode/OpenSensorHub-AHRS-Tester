
package attTester;

/**
 * This is the thread-safe communications link between the AttGraphicsContainer
 * (contains the GUI elements for setting up and running the simulation) and 
 * AttComm (sends the data packet request to the AHRS).
 * 
 * @author Mike Fouche
 */
public class ThreadQueueCmd 
{
    // True if the AttComm is to remain active.
    private boolean ahrsTesterActive;
    
    /**
     * Constructor
     */
    public ThreadQueueCmd()
    {
        ahrsTesterActive = true;
    }
    
    /**
     * Sets the AttComm thread status.
     * 
     * @param aTA Status (active or not) of the AttComm thread.
     */
    public synchronized void setTesterStatus(boolean aTA)
    {
        ahrsTesterActive = aTA;
    }        
    
    /**
     *  Gets the AttComm thread status.
     * 
     * @return Status (active or not) of the AttComm thread.
     */
    public synchronized boolean getTesterStatus()
    {
        return ahrsTesterActive;
    }
    
} // end of class ThreadQueueCmd
