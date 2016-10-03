
package panels;

import attTester.*;

import javax.swing.*;

import java.awt.*;

/**
 * This JPanel contains the roll, pitch, and heading attitude text boxes as 
 * well as the system output text box (largest of all of them).
 * 
 * @author Mike
 */

public class DigDisplayContainer 
{
    private JPanel mainPanel;
    private JPanel subPanel;
    
    private final JLabel rlabel2;
    private final JLabel plabel2;
    private final JLabel ylabel2;
    
    private JTextArea textRoll;
    private JTextArea textPitch;
    private JTextArea textHead;
    private JTextArea textSys;
    
    /**
     * Constructor
     * 
     * @param rlabel2 Label for the roll bar.
     * @param plabel2 Label for the pitch bar.
     * @param ylabel2 Label for the heading bar.
     */
    public DigDisplayContainer (JLabel rlabel2, JLabel plabel2, JLabel ylabel2)
    {
        this.rlabel2 = rlabel2;
        this.plabel2 = plabel2;
        this.ylabel2 = ylabel2;
    }        
    
    //--------------------------------------------------------------------------
    /**
     * Creates a JPanel which will be the main JPanel that will be loaded 
     * into the JFrame.
     * <p>
     * Set up JTextArea objects for the roll, pitch, heading, and system text boxes.
     * The numerical output of the sensor will appear in these boxes.
     * <p>
     * Sets the bounds for each of the roll/pitch/heading text boxes and the
     * system output text box.
     * <p>
     * Creates a JSCrollPane object to add a scrolling feature to the main
     * system text box.  Inserts the JTextArea system text box into the
     * JSCrollPane object.
     * <p>
     * Adds the JTextArea and JScrollPane objects to the JPanel and sets the
     * JPanel background color.
     * 
     * @return mainPanel of type JPanel 
     */
    public JPanel digiLoad()
    {
        // Create JPanel.  This is the main JPanel that will
        // be loaded into the JFrame.
        mainPanel = new JPanel();
        mainPanel.setOpaque(true);
        mainPanel.setLayout(new FlowLayout(1,60,10));
        mainPanel.setPreferredSize(new Dimension(520,208));
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        //**********************************************************************
        // Create JLabels for text set below attitude bars
        //**********************************************************************
        subPanel = new JPanel();
        subPanel.setOpaque(false);
        subPanel.setLayout(new FlowLayout(1,70,5));
        subPanel.setPreferredSize(new Dimension(520,30));

        Font font = new Font("ARIAL", Font.BOLD, 16);

        // Roll text
        subPanel.add(rlabel2);
        rlabel2.setText("Roll");
        rlabel2.setLocation(140, 322);
        rlabel2.setSize(100,50);
        rlabel2.setHorizontalAlignment(0);
        rlabel2.setForeground(Color.red);
        rlabel2.setFont(font);
        
        // Pitch text
        plabel2.setText("PITCH");
        subPanel.add(plabel2);
        plabel2.setLocation(280, 322);
        plabel2.setSize(100,50);
        plabel2.setHorizontalAlignment(0);
        plabel2.setForeground(Color.blue);
        plabel2.setFont(font);
        
        // Heading text
        ylabel2.setText("YAW");
        subPanel.add(ylabel2);
        ylabel2.setLocation(420, 322);
        ylabel2.setSize(100,50);
        ylabel2.setHorizontalAlignment(0);
        ylabel2.setForeground(Color.green);
        ylabel2.setFont(font);
        
        mainPanel.add(subPanel);
        
        // Set up JTextArea objects for the Roll, Pitch, Heading, and 
        // system text boxes.  The numerical output of the sensor will
        // appear in these boxes.
        
        // Retrieve the JTextArea objects from the common data object, DataStore.
        textRoll = DataStore.getSInstc().getTextRoll();
        textPitch = DataStore.getSInstc().getTextPitch();
        textHead = DataStore.getSInstc().getTextHead();
        textSys = DataStore.getSInstc().getTextSys();

        DigDisplays rollDigDisp = new DigDisplays(textRoll);
        DigDisplays pitchDigDisp = new DigDisplays(textPitch);
        DigDisplays headDigDisp = new DigDisplays(textHead);
        
        // Create a JSCrollPane object to add a scrolling feature to the main
        // system text box.  Insert the JTextArea system text box into the
        // JSCrollPane object.
        JScrollPane tScroll = new JScrollPane(textSys);
        DigDisplays sysDigDisp = new DigDisplays(tScroll);
        
        // Add the JTextArea and JScrollPane JPanel objects to the main JPanel.
        mainPanel.add(rollDigDisp.jpanelDigDisp());
        mainPanel.add(pitchDigDisp.jpanelDigDisp());
        mainPanel.add(headDigDisp.jpanelDigDisp());
        mainPanel.add(sysDigDisp.jpanelDigDisp());
        
        // Set the background color for the JPanel.
        mainPanel.setBackground(new Color(224,224,224));
        
        return mainPanel;
        
    }     
    
} // end of class DigDisplayContainer
