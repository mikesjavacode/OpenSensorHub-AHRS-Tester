
package panels;

import attTester.DataStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Creates the color objects used for setting the attitude bar colors.
 * 
 * @author Mike Fouche
 */
public class AttitudeGraphic extends JPanel
{
    // **********************
    // Declare instance objects
    // **********************
    
    private Color[] colors3;
    
    // Color objects used for setting the colors for the attitude bars
    private Color rollc;
    private Color pitchc;
    private Color headingc;
    
    // **********************
    // Declare instance variables
    // **********************
    
    // The integer values of roll, pitch, and heading are loaded into "data"
    // which drives the location of the attitude color bars
    private int[] data;
    
    /** 
     * Constructor.
     * <p>
     * Instantiates a new Color object.
     * Initializes the attitude bars to the default color settings.
     */
    public AttitudeGraphic()
    {
        // Instantiate instance object
        colors3 = new Color[3];
        
        // Initialize the attitude bars to default color settings
        rollc = colors3[0];
        pitchc = colors3[1];
        headingc = colors3[2];
        
        data = new int[3];
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Loads the latest values of roll, pitch and heading into the member array
     * "data".  Then forces a graphic update - repaint();
     * 
     * @param data A 3-element array which holds the roll, pitch, and heading values.
     */ 
    public void ahrs_update(int[] data)
    {
        // Load attitude data (roll/pitch/heading)
        this.data = data;
        // Update the graphics - in particular the attitude bar locations
        repaint();
    
    } 
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void ahrsBarUpdate()
    {
        // Retrieve the color settings for the attitude bars
        colors3 = DataStore.getSInstc().getColors();
        // Update the graphics - in particular the attitude bar locations
        repaint();
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Sets up the JPanel parameters - layout, location, size, border, and background.
     */
    public void panelSetup()
    {
        this.setPreferredSize(new Dimension(500,300));
        this.setOpaque(true);
        this.setBorder(BorderFactory.createRaisedBevelBorder());
        this.setBackground(new Color(224,224,224));
        
    } 
    
    //--------------------------------------------------------------------------
    /**
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);        

        float xScale, yScale, maxValue;
        
        int w = 500; //getWidth();
        int h = 300; //getHeight();
        int x, y;
        
        // Retrieve the color settings for the attitude bars
        colors3 = DataStore.getSInstc().getColors();
        // Roll bar color
        rollc = colors3[0];
        // Pitch bar color
        pitchc = colors3[1];
        // Heading bar color
        headingc = colors3[2];
        
        // Graphic object
        Graphics2D attBar = (Graphics2D)g;

        // Coordinate System:
        // - Starts from top left of screen
        // - X goes from left to right
        // - Y goes from top to down
        
//        attBar.drawLine(7+100, 7, 7+100, h-7);      // y-axis   // 50, 50,       ... 50, height-50
//        attBar.drawLine(7+100, h-144, w-7, h-144);  // x-axis   // 50, height-50 ... width-50, height-50
        
        attBar.drawLine(7, 7, 7, h-7);      // y-axis   // 50, 50,       ... 50, height-50
        attBar.drawLine(7, h-144, w-7, h-144);  // x-axis   // 50, height-50 ... width-50, height-50

        //               ( screen width - 2x7 ) / 4
        xScale = (float)(w - 210) / 4.0f;
        
        maxValue = 100.0f;  // was 100
        //               ( screen height - 2x7) / 100
        yScale = (float)(h - 2*7) / maxValue;
        
        // The location of origin.
        int x0 = 7;
        int y0 = h-7;
        
        x = x0 + (int)xScale;
        
        //= (height - 7) - ( ( screen height - 2x7) / 100 ) * roll_value
        // Set the location of the roll attitude bar
        y = y0 - (int)(yScale * (data[0]+50));
        // Set the roll bar color
        attBar.setPaint(rollc);            
        attBar.fillRect(x-2, y-2, 100, 20);    
        
        // Set the location of the pitch attitude bar
        y = y0 - (int)(yScale * (data[1]+50));
        // Set the pitch bar color
        attBar.setPaint(pitchc);
        attBar.fillRect(x+120-2, y-2, 100, 20);

        // Set the location of the heading attitude bar
        y = y0 - (int)(yScale * (data[2]+50));
        // Set the heading bar color
        attBar.setPaint(headingc);
        attBar.fillRect(x+240-2, y-2, 100, 20);

    } 

} // end of class AttitudeGraphics

