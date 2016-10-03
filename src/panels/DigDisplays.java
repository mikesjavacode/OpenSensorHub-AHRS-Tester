
package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Mike Fouche
 */
public class DigDisplays 
{
    private final JTextArea textDisp;
    private final JScrollPane sysDisp;
    
    /**
     * Constructor 1
     * 
     * @param textDisp
     */
    public DigDisplays(JTextArea textDisp)
    {
        this.textDisp = textDisp;
        this.sysDisp = null;
    }
    
    /**
     * Constructor 2
     * 
     * @param sysDisp
     */
    public DigDisplays(JScrollPane sysDisp)
    {
        this.sysDisp = sysDisp;
        this.textDisp = null;
    }        
    
    /**
     * Create JPanel with display
     * 
     * @return
     */
    public JPanel jpanelDigDisp()
    {
        JPanel td = new JPanel();
        td.setLayout(new BorderLayout());
        
        td.setOpaque(true);
        td.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        td.setBackground(new Color(192,192,192));

        // for digital displays - JTextArea
        if( textDisp != null )
        {    
            td.setPreferredSize(new Dimension(60,20));
            td.add(textDisp);
        }
        // for system display - JScrollPane
        else
        {
            td.setPreferredSize(new Dimension(490,120));
            td.add(sysDisp);
        }
        
        return td;

    } 
    
} // end of class DisDisplays
