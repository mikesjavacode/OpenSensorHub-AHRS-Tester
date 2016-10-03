
package attTester;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class sends a command to the AHRS to send an attitude data packet.
 * 
 * @author Mike Fouche
 * @version 1.0
 */
public class AttComm implements Runnable 
{
    private final OutputStream out;
    private final ThreadQueueCmd cQ;
    private final String ahrsModel;
    
    private ByteBuffer byteBuffer;
    private final byte[] buffer;
    private byte[] buffSend;
    
    /**
     * Constructor
     * 
     * @param out Serial output stream object: this is what 
     * sends serial commands to the AHRS.
     * @param cQ
     * @param ahrsModel
     */
    public AttComm (OutputStream out, ThreadQueueCmd cQ, String ahrsModel)
    {
        this.out = out;
        this.cQ  = cQ;
        this.ahrsModel = ahrsModel;
        
        buffer = new byte[1024];
        buffSend = new byte[2]; // nominally 4
    }

    @Override
    public void run ()
    {
        while ( cQ.getTesterStatus() )
        {
            // A time delay is implemented so that the graphics can 
            // keep up.  The command is sent every 50 milliseconds to the
            // AHRS.  Thus the graphics are updated at the same rate.
            try 
            {
                Thread.sleep(50); // do nothing for 70 milliseconds
            } 
            catch(InterruptedException e)
            {
                System.out.println("Exception: "+e+", in Sleep thread in class AttCom");
            } 

            switch ( ahrsModel ) 
            {
                case "3DM-GX2":
                    ahrs_3DM_GX2();
                    break;
                case "3DM-GX4-25":
                    ahrs_3DM_GX4_25();
                    break;
                case "3DM-GX3-35":
                    ahrs_3DM_GX3_35();
                    break;
                case "3DM-GX3-25_OEM":
                    ahrs_3DM_GX3_25_OEM();
                    break;
                default:
                    return;
            }

        }
            
        // Close the serial output stream object
        try
        {
            out.close();
        }
        catch(Exception e)
        {
            System.out.println("Command closing exception: "+e);
        }   
        
        // Send text to the system window
        DataStore.getSInstc().setTextSys("AHRS command Thread CLOSED ...\n" );
        // Set caret position to bottom of text
        DataStore.getSInstc().setTextCaretPosition();
                
        
    }  
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX2()
    {
        // Send the command, "206"
        try
        {
            out.write(206);
        }
        catch(Exception e)
        {
            System.out.println("3DM-GX2 Command exception: "+e);
        }    
    }
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX4_25()
    {
        // Send the command, 
        try
        {
            byte leadIn = 0x75;
            byte lagIn  = 0x65;
            
            byteBuffer = ByteBuffer.allocate(2);
            // Set up byte order.
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            // Load synch bytes at front of data pack
            byteBuffer.put(leadIn);
            byteBuffer.put(lagIn);
            // Load data packet into byte array.
            buffSend = byteBuffer.array();
            out.write(buffSend);
        }
        catch(Exception e)
        {
            System.out.println("3DM-GX4-25 Command exception: "+e);
        }    
    }
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX3_35()
    {
        // Send the command, "206"
        try
        {
            out.write(206);
        }
        catch(Exception e)
        {
            System.out.println("3DM-GX3-35 Command exception: "+e);
        }    
    } 
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX3_25_OEM()
    {
        // Send the command, "206"
        try
        {
            out.write(206);
        }
        catch(Exception e)
        {
            System.out.println("3DM-GX3-25-OEM Command exception: "+e);
        }    
    }        
    
} // end of class AttComm
