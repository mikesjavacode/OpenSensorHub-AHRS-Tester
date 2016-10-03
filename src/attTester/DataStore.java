
package attTester;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTextArea;

/**
 *
 * @author Michael Fouche
 */
public class DataStore 
{
    // Static member holds only one instance of the
    // DataStore class
    private static DataStore singletonInstance;

    // Swing instance objects
    private Color rColor;
    private Color pColor;
    private Color hColor;
   
    private final JTextArea textRoll;
    private final JTextArea textPitch;
    private final JTextArea textHead;
    private final JTextArea textSys;
    
    // Instance variables
    private int attCom;
    private byte[] attCom2;
    private boolean attLaunch;
    
    private boolean useTextFile;
    
    private volatile boolean useMySQL, endofMySQL, readMySQL;
    
    // 2D Array for holding the attitude data (roll, pitch, heading)
    private final ArrayList<ArrayList<Float>> stateArray;

    private int iloop;
    
    private String ahrsModel;

    /**
     * "Private" Constructor.  Prevents any other class from 
     * instantiating this class. 
     */
    private DataStore()
    {
        // Color objects for the attitude text (roll/pitch/heading-yaw)
        rColor = new Color(255, 0, 0); // RGB red
        pColor = new Color(0, 0, 255); // RGB blue
        hColor = new Color(0, 255, 0); // RGB green
        
        attCom = 206;
        attCom2 = new byte[2];
        
        attLaunch = false;
        
        useMySQL = false;
        endofMySQL = false;
        readMySQL = false;
        
        // Digital indicator objects
        Font font = new Font("ARIAL", Font.PLAIN, 14);
        textRoll = new JTextArea("",12,4);
        textRoll.setFont(font);
        textPitch = new JTextArea("",12,4);
        textPitch.setFont(font);
        textHead = new JTextArea("",12,4);
        textHead.setFont(font);
        // System indicator object - explain fields (5-down)
        textSys = new JTextArea("",5,1);
        
        // Flight data is stored in this array
        stateArray = new ArrayList<>();
    }
    
    // Provides Global point of access to this Singleton
    /**
     *
     * @return 
     */
    public static DataStore getSInstc() 
    {
        if (null == singletonInstance) 
        {
            singletonInstance = new DataStore();
            System.out.println("Singleton instance created ...");
        }
        
        return singletonInstance;
    }    

    //--------------------------------------------------------------------------
    /**
     * Clear out the state array
     */
    public void clearStateArray()
    {
        stateArray.clear();
    }
    
    //**************************************************************************
    // Setter methods
    //**************************************************************************

    //--------------------------------------------------------------------------
    /**
     *
     * @param useTextFile
     */
    public void setUseTextFile(boolean useTextFile)
    {
        this.useTextFile = useTextFile;
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Set the size of the roll indicator text display.
     * 
     * @param i
     * @param j
     * @param k
     * @param l 
     */
    public void setTextRollBounds(int i, int j, int k, int l)
    {
        textRoll.setBounds(i,j,k,l);
    }        

    //--------------------------------------------------------------------------
    /**
     * Setter: Set the size of the pitch indicator text display.
     * 
     * @param i
     * @param j
     * @param k
     * @param l 
     */
    public void setTextPitchBounds(int i, int j, int k, int l)
    {
        textPitch.setBounds(i,j,k,l);
    }        

    //--------------------------------------------------------------------------
    /**
     * Setter: Set the size of the heading/yaw indicator text display.
     * 
     * @param i
     * @param j
     * @param k
     * @param l 
     */
    public void setTextHeadBounds(int i, int j, int k, int l)
    {
        textHead.setBounds(i,j,k,l);
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Set the size of the system output text display
     * 
     * @param i
     * @param j
     * @param k
     * @param l 
     */
    public void setTextSysBounds(int i, int j, int k, int l)
    {
        textSys.setBounds(i,j,k,l);
    }

    //--------------------------------------------------------------------------
    /**
     * Setter: Set the cursor at the end of the text.
     * 
     */
    public void setTextCaretPosition()
    {
        textSys.setCaretPosition(textSys.getDocument().getLength());
    }
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Writes the system messages to the main system box.
     * 
     * @param text 
     */
    public void setTextSys(String text)
    {
        textSys.append(text);
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Writes the text into the roll indicator box.
     * 
     * @param text 
     */
    public void setRoll( String text)
    {
        textRoll.replaceSelection(text);
    }        

    //--------------------------------------------------------------------------
    /**
     * Setter: Writes the text into the pitch indicator box.
     * 
     * @param text 
     */
    public void setPitch( String text)
    {
        textPitch.replaceSelection(text);
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Writes the text into the heading indicator box.
     * 
     * @param text 
     */
    public void setHead( String text)
    {
        textHead.replaceSelection(text);
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the color of the roll attitude bar.
     * 
     * @param clr Color object
     */
    public void setRColor(Color clr)
    {
        rColor = clr;
    }        

    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the color of the pitch attitude bar.
     * 
     * @param clr Color object
     */
    public void setPColor(Color clr)
    {
        pColor = clr;
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the color of the heading/yaw attitude bar.
     * 
     * @param clr Color object
     */
    public void setHColor(Color clr)
    {
        hColor = clr;
    }   
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the MySQL parameter for real time run.
     */
    public void setMySQLrealTime()
    {
        useMySQL = false;
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the MySQL parameter for playback;
     */
    public void setMySQLplayBack()
    {
        endofMySQL = false;
        readMySQL = true;
        useMySQL = true;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the readMySQL parameter to read the MySQL table.
     * 
     * @param readMySQL boolean
     */
    public void setReadMySQL(boolean readMySQL)
    {
        this.readMySQL = readMySQL;
    }        

    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the endofMySQL parameter.
     * 
     * @param endofMySQL boolean
     */
    public void setEndofMySQL(boolean endofMySQL)
    {
        this.endofMySQL = endofMySQL;
    }        

    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the value of AttCom.
     * 
     * @param attCom integer
     */
    public void setAttCom(int attCom)
    {
        this.attCom = attCom;
    }
    
    //--------------------------------------------------------------------------
    /**
     *
     * @param aCom2
     */
    public void setAttCom2(byte[] aCom2)
    {
        attCom2 = aCom2;
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the value of attLaunch.
     * 
     * @param attLaunch boolean
     */
    public void setAttLaunch(boolean attLaunch)
    {
        this.attLaunch = attLaunch;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Sets the value of iloop.
     * 
     * @param iloop integer
     */
    public void setIloop(int iloop)
    {
        this.iloop = iloop;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Setter: Loads the roll, pitch, and heading/yaw values into the State Array.
     * 
     * @param ict integer counter
     * @param rpy double 3-element array holding roll, pitch, and heading values
     */
    public void setStateArray(int ict, double[] rpy)
    {
        // Add another row
        stateArray.add(new ArrayList());
        // Add Roll to the 1st column in the current row
        stateArray.get(ict).add(0, (float)rpy[0]);
        // Add Pitch to the 2nd column
        stateArray.get(ict).add(1, (float)rpy[1]);
        // Add Heading to the 3rd column
        stateArray.get(ict).add(2, (float)rpy[2]);
    }        
    
    //**************************************************************************
    // Getter methods
    //**************************************************************************

    //--------------------------------------------------------------------------
    /**
     *
     * @return
     */
    public boolean getUseTextFile()
    {
        return useTextFile;
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Getter: Returns the value of the roll indicator text.
     * 
     * @return roll text
     */
    public JTextArea getTextRoll()
    {
        return textRoll;
    }

    //--------------------------------------------------------------------------
    /**
     * Getter: Returns the value of the pitch indicator text.
     * 
     * @return pitch text
     */
    public JTextArea getTextPitch()
    {
        return textPitch;
    }

    //--------------------------------------------------------------------------
    /**
     * Getter: Returns the value of the heading/yaw indicator text.
     * 
     * @return heading text
     */
    public JTextArea getTextHead()
    {
        return textHead;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Getter: Returns the value of the system message text.
     * 
     * @return system text
     */
    public JTextArea getTextSys()
    {
        return textSys;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Getter: Returns the 3-element array of object Color with color values
     * for roll, pitch, and heading/yaw attitude bars.
     * 
     * @return 3-element array of object Color
     */
    public Color[] getColors()
    {
        Color[] colors3 = new Color[3];
        
        colors3[0] = rColor;
        colors3[1] = pColor;
        colors3[2] = hColor;
        
        return colors3;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Getter Method.
     * 
     * @return boolean value of endofMySQL 
     */
    public boolean getEndofMySQL()
    {
        boolean tmp = endofMySQL;
        
        return tmp;
    }

    //--------------------------------------------------------------------------
    /**
     * Getter Method.
     * 
     * @return boolean value of readMySQL 
     */
    public boolean getreadMySQL()
    {
        boolean tmp = readMySQL;
        
        return tmp;
    }

    //--------------------------------------------------------------------------
    /**
     * Getter Method.
     * 
     * @return boolean value of useMySQL
     */
    public boolean getuseMySQL()
    {
        boolean tmp = useMySQL;
        
        return tmp;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Getter Method.
     * 
     * @return boolean value of attLaunch
     */
    public boolean getAttLaunch()
    {
        boolean tmp = attLaunch;
        
        return tmp;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Getter Method.
     * 
     * @return boolean value of attCom
     */
    public int getAttCom()
    {
        return attCom;
    }
    
    //--------------------------------------------------------------------------
    /**
     *
     * @return
     */
    public byte[] getAttCom2()
    {
        return attCom2;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Get values of stateArray.
     * 
     * @param ict counter
     * 
     * @return values of roll, pitch, and heading state array
     */
    public double[] getStateArray(int ict)
    {
        double[] rpy = new double[3];
        // Roll - current row, 1st column
        rpy[0] = stateArray.get(ict).get(0);
        // Pitch - current row, 2nd column
        rpy[1] = stateArray.get(ict).get(1);
        // Heading - current row, 3rd column
        rpy[2] = stateArray.get(ict).get(2);
        
        return rpy;
    } 
    
    //--------------------------------------------------------------------------
    /**
     *
     * @return
     */
    public int getSizeStateArray()
    {
        int sizeSA = stateArray.size();
        
        return sizeSA;
    } 
    
    //--------------------------------------------------------------------------
    /**
     *
     * @param model
     */
    public void setAHRSModel(String model)
    {
        ahrsModel = model;
    }        
    
    //--------------------------------------------------------------------------
    /**
     *
     * @return
     */
    public String getAHRSModel()
    {
        return ahrsModel;
    }        
    
} // end of Singleton class DataStore
