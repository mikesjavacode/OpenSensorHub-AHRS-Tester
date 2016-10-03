
package attTester;

import java.io.IOException;
import java.io.InputStream;

import gnu.io.CommPort;

/**
 * Retrieves the buffered data from the RS232 serial port, decodes the data 
 * (which comes in IEEE-754 format) into the appropriate values (roll, pitch, 
 * and heading in degrees).
 * 
 * @author Mike Fouche
 * 
 */
public class AttRecv implements Runnable 
{
    
    private final InputStream in;
    private final Thread_Queue TQ;
    private final CommPort commPort;
    private final ThreadQueueRcv rQ;
    
    private final byte[] buffer;
    private final long[] buff2;
    
    private int len; 
    private int buffctr;
    // 2-D array holds roll, pitch, and heading
    private double roll, pitch, heading;
    
    private double[] rphdata;
    
    private int ictr;

    /**
     * Constructor
     * 
     * @param in Serial input stream object
     * @param TQ Thread synchronizer object
     * @param commPort RS-232 communications port object 
     * @param rQ 
     */
    public AttRecv (InputStream in,Thread_Queue TQ,CommPort commPort,ThreadQueueRcv rQ)
    {
        this.in       = in;
        this.TQ       = TQ;
        this.commPort = commPort;
        this.rQ       = rQ;
        
        buffer = new byte[1024];
        buff2 = new long[100];
        
        rphdata = new double[3];
        ictr = 0;
        
        len = 1;
        // Counts the number of read bytes from the serial port
        buffctr = 0;
    }
    
    @Override
    public void run ()
    {
        try
        {
            // Create instance of InterfaceMySQL
            ConnectMySQL talkToMySQL = new ConnectMySQL();
            
            // Clear out elements of aircraft state ArrayList if a simulation
            // was previously run.
            DataStore.getSInstc().clearStateArray();
            
            switch (DataStore.getSInstc().getAHRSModel()) 
            {
                case "3DM-GX2":
                    DataStore.getSInstc().setTextSys("\nUsing AHRS 3DM-GX2 ...\n" );
                    ahrs_3DM_GX2();
                    break;
                case "3DM-GX4-25":
                    DataStore.getSInstc().setTextSys("\nUsing AHRS 3DM-GX4-25 ...\n" );
                    ahrs_3DM_GX4_25();
                    break;
                case "3DM-GX3-35":
                    DataStore.getSInstc().setTextSys("\nUsing AHRS 3DM-GX3-35 ...\n" );
                    ahrs_3DM_GX3_35();
                    break;
                case "3DM-GX3-25-OEM":
                    DataStore.getSInstc().setTextSys("\nUsing AHRS 3DM-GX3-25-OEM ...\n" );
                    ahrs_3DM_GX3_25_OEM();
                    break;
                default:
                    return;
            }
            
            DataStore.getSInstc().setStateArray(ictr-1, rphdata);

            // Load roll/pitch/heading to the Thread_Queue object
            // which synchronizes between it and this thread - AttRecv.
            TQ.put(roll,pitch,heading); 
            
            if( DataStore.getSInstc().getUseTextFile() )
            {
                DataStore.getSInstc().setTextSys("Writing "+ictr+" records to a text file ...\n" );
                DataStore.getSInstc().setTextCaretPosition();
                
                ConnectTextData cd = new ConnectTextData();
                // Load the data to a text file
                cd.writeTextData();
            }
            else
            {
                // Start with fresh table
                talkToMySQL.createTable();
                DataStore.getSInstc().setTextSys("MySQL loading "+ictr+" records ...\n" );
                DataStore.getSInstc().setTextCaretPosition();
                // Load the data to the MySQL table
                talkToMySQL.loadTable();
            }
            
            // Clear out the state array
            DataStore.getSInstc().clearStateArray();
            
            // Close the serial input stream object.
            this.in.close();
            
            // Wait 2 seconds to make sure that serial output stream object
            // has stopped writing (ds.attCom = 10), then close port.
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException e)
            {
                System.out.println("Thread sleep exception in AttRecv");
            }    
            
            // Close the COM port object.
            commPort.close();

            // Notify the information window that the thread is closed
            DataStore.getSInstc().setTextSys("AHRS data receive Thread CLOSED ...\n" );
            DataStore.getSInstc().setTextCaretPosition();
        } 
        catch ( IOException e )
        {
            System.out.println("Exception: "+e+", in AttRecv");
        }

    } 
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX2()
    {
        // Create instance of attitude data decoder.  This contains the
        // logic for decoding the data packet from the AHRS
        DecodeAttitude datt = new DecodeAttitude(1);
    
        try
        {
            // If len = -1 then there is no data in the buffer
            while ( (len = this.in.read(buffer)) > -1 && rQ.getTesterStatus() )
            {
                // If the number of bytes in the buffer is greater than zero
                // then begin processing them
                if ( len > 0 )
                {    
                    for (int i = 0; i < len; i++)
                    {    
                        // Load the byte into the long - only process the 
                        // first 8 bits (& 0xFF) to eliminate any negative bit
                        buff2[buffctr] = ( (int)buffer[i] ) & 0xFF;
                        
                        // If 19 bytes have been retrieved from the serial
                        // buffer, then the full attitude state can be decoded.
                        // 0 through 18 = 19 bytes.
                        if (buffctr == 18)
                        {    
                            // Send serial data packet to be decoded.  Attitude
                            // values (roll/pitch/heading) are returned.
                            rphdata = datt.attData(buff2);

                            roll = rphdata[0];
                            pitch = rphdata[1];
                            heading = rphdata[2];

                            // Load the roll/pitch/heading into the state 
                            // array in the Singleton common data class
                            DataStore.getSInstc().setStateArray(ictr, rphdata);

                            // Load roll/pitch/heading to the Queue synchronizer
                            // object which will pass them to the DataReader object.
                            // The TQ object synchronizes thread communications 
                            // between the DataReader and AttRecv objects.
                            TQ.put(roll,pitch,heading);

                            // Reset the byte counter
                            buffctr = 0;
                            // Increment the "data packets read" counter
                            ictr ++;

                        } // end of loading data
                        else
                        {
                            // Increment the byte counter
                            buffctr++;
                        }   
                        
                    } // end of processing through bytes
                
                } // end of if-statement len > 0 
            
            } // end while loop
        }
        catch(Exception e)
        {
            System.out.println("Exception: "+e);
        }    
    }

    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX4_25()
    {
        // Create instance of attitude data decoder.  This contains the
        // logic for decoding the data packet from the AHRS
        DecodeAttitude datt = new DecodeAttitude(2);

        try
        {
            // If len = -1 then there is no data in the buffer
            while ( (len = this.in.read(buffer)) > -1 && rQ.getTesterStatus() )
            {
                // If the number of bytes in the buffer is greater than zero
                // then begin processing them
                if ( len > 0 )
                {    
                    for (int i = 0; i < len; i++)
                    {    
                        // Load the byte into the long - only process the 
                        // first 8 bits (& 0xFF) to eliminate any negative bit
                        buff2[buffctr] = ( (int)buffer[i] ) & 0xFF;
                        
                        // If 20 bytes have been retrieved from the serial
                        // buffer, then the full attitude state can be decoded.
                        // 0 through 19 = 20 bytes.
                        if (buffctr == 19)
                        {    
                            // Send serial data packet to be decoded.  Attitude
                            // values (roll/pitch/heading) are returned.
                            rphdata = datt.attData(buff2);

                            roll = rphdata[0];
                            pitch = rphdata[1];
                            heading = rphdata[2];

                            // Load the roll/pitch/heading into the state 
                            // array in the Singleton common data class
                            DataStore.getSInstc().setStateArray(ictr, rphdata);

                            // Load roll/pitch/heading to the Queue synchronizer
                            // object which will pass them to the DataReader object.
                            // The TQ object synchronizes thread communications 
                            // between the DataReader and AttRecv objects.
                            TQ.put(roll,pitch,heading);

                            // Reset the byte counter
                            buffctr = 0;
                            // Increment the "data packets read" counter
                            ictr ++;

                        } // end of loading data
                        else
                        {
                            // Increment the byte counter
                            buffctr++;
                        }   
                        
                    } // end of processing through bytes
                
                } // end of if-statement len > 0 
            
            } // end while loop
        }
        catch(Exception e)
        {
            System.out.println("Exception: "+e);
        }    
        
    }    

    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX3_35()
    {
        // Create instance of attitude data decoder.  This contains the
        // logic for decoding the data packet from the AHRS
        DecodeAttitude datt = new DecodeAttitude(3);

    }    

    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrs_3DM_GX3_25_OEM()
    {
        // Create instance of attitude data decoder.  This contains the
        // logic for decoding the data packet from the AHRS
        DecodeAttitude datt = new DecodeAttitude(4);

    }    
   
} // end of class AttRecv



