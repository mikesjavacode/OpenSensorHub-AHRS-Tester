
package attTester;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Initializes the serial communications and launches two threads – 
 * AttCom and AttRecv.
 * 
 * @author Mike Fouche
 */
public class TwoWaySerialComm
{
    private SerialPort serialPort;

    // See definitions below.
    private final Thread_Queue TQ;
    private final ThreadQueueCmd cQ;
    private final ThreadQueueRcv rQ;
    private final String ahrsModel;
    
    /**
     * Constructor
     * 
     * @param TQ The thread-safe communications link between AHRSRealTime 
     * (which loops while receiving the AHRS data and sending it to the graphics 
     * objects) and AttRecv.
     * @param cQ The thread-safe communications link between AttGraphicsContainer 
     * (contains the GUI elements for setting up and running the simulation) 
     * and AttComm (sends the data packet request to the AHRS).
     * @param rQ This is the thread-safe communications link between 
     * AttGraphicsContainer (contains the GUI elements for setting up and 
     * running the simulation) and AttRecv (receives and processes the AHRS 
     * data packet).
     * @param ahrsModel The selected AHRS model.
     */
    public TwoWaySerialComm(Thread_Queue TQ, ThreadQueueCmd cQ, 
                            ThreadQueueRcv rQ, String ahrsModel)
    {
        super();
        
        this.TQ = TQ;
        this.cQ = cQ;
        this.rQ = rQ;
        
        this.ahrsModel = ahrsModel; 
    }
    
   /**
    * This is the main serial communications method, the instance of its class is 
    * launched from AttGraphicsContainer.java.  
    * <p>
    * Instances of DataStore (common data) and Thread_Queue are passed 
    * through the constructor.
    * <p>
    * 1.  checks to see if the serial port is available (the string value is 
    *     passed in the method argument.
    * <p>
    * 2.  creates a CommPort object and verifies that the serial port is a valid
    *     RS-232 port (not an RS-485 port, etc.)
    * <p>
    * 3.  verifies that the commPort object is the same type as the SerialPort object
    * <p>
    * 4.  creates an instance of SerialPort and sets the communications parameters
    * <p>
    * 5.  creates an instance of InputStream (data coming from the serial port) 
    *     and OutputStream (data going to the serial port).
    * 
    * @param portName The String value of the serial port number (e.g. "COM3").
    */
    public void connect ( String portName ) 
    {
        try
        {
            // Check the port status.
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if ( portIdentifier.isCurrentlyOwned() )
            {
                System.out.println("Error: Port is currently in use");
            }
            else
            {
                // Open the COM port.
                CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

                // Verify that commPort is of the same type as SerialPort (not a
                // parallel port) - if so then set the serial port parameters.
                if ( commPort instanceof SerialPort ) 
                {
                    serialPort = (SerialPort) commPort;

                    serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

                    // Build serial port input and output objects.
                    InputStream in = serialPort.getInputStream();
                    OutputStream out = serialPort.getOutputStream();

                    // Launch the AHRS receiver thread.
                    (new Thread(new AttRecv(in,TQ,commPort,rQ))).start();
                    
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch(InterruptedException e)
                    {
                        System.out.println("Exception in TwoWaySerialComm");
                    }    

                    // Launch the AHRS command thread.
                    (new Thread(new AttComm(out,cQ,ahrsModel))).start();

                    // Notify user that both threads have been launched.
                    DataStore.getSInstc().setTextSys("Comms threads launched ...\n" );
                }
                else
                {
                    System.out.println("Error: Only serial ports are handled by this code.");
                }
            } 
        }
        catch(NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException e)
        {
            System.out.println("Exception: "+e+", in TwoWaySerialComm");
        }    
    
    } 
    
} // end of class TwoWaySerialComm

