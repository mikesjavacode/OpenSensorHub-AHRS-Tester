
package attTester;

/**
 * Decodes the attitude data packet (byte array) for each AHRS model.
 * 
 * @author Mike Fouche
 */
public class DecodeAttitude 
{
    private final int ahrsType;
    
    /**
     * Constructor
     * 
     * @param aType AHRS number (e.g. 0 = 3DM-GX2, etc.).
     */
    public DecodeAttitude(int aType)
    {
        ahrsType = aType;
    }
    
    /**
    * Takes the array of serial buffer data (type long), that is passed
    * through the method argument, and decodes the individual attitude
    * values (IEEE-745 floating point protocol)
    * 
    * @param buff2 The Long array that holds the data packet of attitude data
    * 
    * @return a 3-element array of type double with roll, pitch, and heading.
    */
    public double[] attData(long[] buff2)
    {
        double[] rpyData = new double[3];
        
        switch (ahrsType) 
        {
            case 1:
                rpyData = ahrsType_1(buff2);
                break;
            case 2:
                rpyData = ahrsType_2(buff2);
                break;
            case 3:
                rpyData = ahrsType_3(buff2);
                break;
            case 4:
                rpyData = ahrsType_4(buff2);
                break;
            default:
                break;
        }    
        
        return rpyData;
   
    } 
    
    //--------------------------------------------------------------------------
    /**
     * For the 3DM-GX2 model, takes the array of serial buffer data (type long), 
     * that is passed through the method argument, and decodes the individual attitude
     * values (IEEE-745 floating point protocol)
     * 
     * @param buff2 The Long array that holds the data packet of attitude data
     * 
     * @return a 3-element array of type double with roll, pitch, and heading.
     */
    public double[] ahrsType_1(long[] buff2)
    {
        double roll, pitch, heading;
        double[] rpyData = new double[3];

        /**** ROLL ***********************************************/

        // Perform Big-Endian conversion.  Note that this can be done with a Java
        // library but I did this approach before knowing about the library
        // and decided to leave it as is.
        int ibuff = (int) ( (buff2[1]<<24) + (buff2[2]<<16) + (buff2[3]<<8) + (buff2[4]) );
        // Perform an IEEE-754 float conversion
        roll = Float.intBitsToFloat(ibuff);
        // Convert from radians to degrees
        roll = roll * 180.0f / Math.PI;
        
        /**** PITCH ***********************************************/

        // Perform Big-Endian conversion
        ibuff = (int) ( (buff2[5]<<24) + (buff2[6]<<16) + (buff2[7]<<8) + (buff2[8]) );
        // Perform an IEEE-754 float conversion
        pitch = Float.intBitsToFloat(ibuff);
        // Convert from radians to degrees
        pitch = pitch * 180.0f / Math.PI;

        /**** HEADING ***********************************************/

        // Perform Big-Endian conversion
        ibuff = (int) ( (buff2[9]<<24) + (buff2[10]<<16) + (buff2[11]<<8) + (buff2[12]) );
        // Perform an IEEE-754 float conversion
        heading = Float.intBitsToFloat(ibuff);
        // Convert from radians to degrees
        heading = heading * 180.0f / Math.PI;
        
        rpyData[0] = roll;
        rpyData[1] = pitch;
        rpyData[2] = heading;
        
        return rpyData;
    }

    //--------------------------------------------------------------------------
    /**
     * For the 3DM-GX4-25 model, takes the array of serial buffer data (type long), 
     * that is passed through the method argument, and decodes the individual attitude
     * values (IEEE-745 floating point protocol)
     * 
     * @param buff2 The Long array that holds the data packet of attitude data
     * 
     * @return a 3-element array of type double with roll, pitch, and heading.
     */
    public double[] ahrsType_2(long[] buff2)
    {
        double roll, pitch, heading;
        double[] rpyData = new double[3];

        /**** ROLL ***********************************************/

        // Perform Big-Endian conversion.  Note that this can be done with a Java
        // library but I did this approach before knowing about the library
        // and decided to leave it as is.
        int ibuff = (int) ( (buff2[6]<<24) + (buff2[7]<<16) + (buff2[8]<<8) 
                                                                + (buff2[9]) );
        // Perform an IEEE-754 float conversion
        roll = Float.intBitsToFloat(ibuff);
        // Convert from radians to degrees
        roll = roll * 180.0f / Math.PI;
        
        /**** PITCH ***********************************************/

        // Perform Big-Endian conversion
        ibuff = (int) ( (buff2[10]<<24) + (buff2[11]<<16) + (buff2[12]<<8) 
                                                                + (buff2[13]) );
        // Perform an IEEE-754 float conversion
        pitch = Float.intBitsToFloat(ibuff);
        // Convert from radians to degrees
        pitch = pitch * 180.0f / Math.PI;

        /**** HEADING ***********************************************/

        // Perform Big-Endian conversion
        ibuff = (int) ( (buff2[14]<<24) + (buff2[15]<<16) + (buff2[16]<<8) 
                                                                + (buff2[17]) );
        // Perform an IEEE-754 float conversion
        heading = Float.intBitsToFloat(ibuff);
        // Convert from radians to degrees
        heading = heading * 180.0f / Math.PI;
        
        rpyData[0] = roll;
        rpyData[1] = pitch;
        rpyData[2] = heading;
        
        return rpyData;
    }

    //--------------------------------------------------------------------------
    /**
     * For the 3DM-GX3-35 model, takes the array of serial buffer data (type 
     * long), that is passed through the method argument, and decodes the 
     * individual attitude values (IEEE-745 floating point protocol)
     * 
     * @param buff2 The Long array that holds the data packet of attitude data
     * 
     * @return a 3-element array of type double with roll, pitch, and heading.
     */
    public double[] ahrsType_3(long[] buff2)
    {
        double roll, pitch, heading;
        double[] rpyData = new double[3];
        
        return rpyData;
    }

    //--------------------------------------------------------------------------
    /**
     * For the 3DM-GX3-25-OEMmodel, takes the array of serial buffer data 
     * (type long), that is passed through the method argument, and decodes the 
     * individual attitude values (IEEE-745 floating point protocol)
     * 
     * @param buff2 The Long array that holds the data packet of attitude data
     * 
     * @return a 3-element array of type double with roll, pitch, and heading.
     */
    public double[] ahrsType_4(long[] buff2)
    {
        double roll, pitch, heading;
        double[] rpyData = new double[3];
        
        return rpyData;
    }
    
} // end of class DecodeAttitude
