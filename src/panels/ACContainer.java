
package panels;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Mike Fouche
 */

public class ACContainer 
{
    private final AircraftModelGraphic ac;
    
    /**
     * Constructor
     * 
     * @param ac instance of aircraft model graphics class 
     */
    
    public ACContainer(AircraftModelGraphic ac)
    {
        this.ac = ac;
    }        

    /**
     * This JPanel, with a raised/beveled look, is created for 
     * "mounting" the helicopter JPanel.
     * 
     * @return the JPanel with aircraft model loaded 
     */
    public JPanel acLoad()
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(true);
        mainPanel.setSize(500,500);
        mainPanel.setLayout(new FlowLayout(0,40,10));
        
        // Set a raised-beveled look for the panel
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Add the aircraft model JPanel to this JPanel
        mainPanel.add(ac.createContentPane_AC());
        
        return mainPanel;
        
    } 
    
} // End of class ACContainer
