
package attTester;

import panels.AttGraphicsContainer;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

/**
 *
 * @author Mike
 */
public class AHRSBenchTest 
{

    /**
     *
     * @param args None.
     */
    public static void main(String[] args)
    {
       JDesktopPane fdt = null; 
       
       boolean useDesktop = true;
       
       // Create main frame 
       JFrame f = new JFrame("                            ATTITUDE SENSOR DATA DISPLAY ");
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.setSize(1050,660);
       f.setLocation(100,100);  
       f.setResizable(true);

       if( useDesktop )
       {    
           // Create Desktop and add it into main frame
           fdt = new JDesktopPane();
           fdt.setSize(1050,650);
           fdt.setVisible(true);

           // Declare and create instance of AttGraphicsContainer 
           AttGraphicsContainer gc = new AttGraphicsContainer(fdt);
           // ??
           gc.createContentPane_AHRS();
           // Add JDesktopPane to JFrame
           f.add(fdt, BorderLayout.CENTER);
       }
       else
       {
           // Declare and create instance of AttGraphicsContainer 
           AttGraphicsContainer gc = new AttGraphicsContainer();
           //??
           gc.createContentPane_AHRS();
           
           // Add JPanel to JFrame
           f.add(gc.getGraphicsPanel());
       }
       
       // Note that the Singleton instance is created in 
       // class DigDisplayContainer which is instantiated
       // in AttGraphicsContainer

       // Make JFrame contents visible
       f.setVisible(true);
       
       if( useDesktop && fdt != null )
       {
           fdt.setLayout(null);
       } 

       String Intro;
       Intro = ("This software tests output from an attitude and heading reference sensor (AHRS).\n"+
                "Click the 'Data Acquisition' menu button on the top left, then click 'Start' to begin\n"+
                "taking data.  Click on 'Data Acquisition'->'Stop' to stop the data acquisition process.\n"+
                "Immediately following, a MySQL table will be loaded with the data - wait until the\n"+
                "process is finished."+
                "  To playback the previous test, click 'Playback' -> 'Start playback'.\n");
       // Load the intro text to the system window display
       DataStore.getSInstc().setTextSys(Intro );
    }
    
} // end of class AHRSBenchTester
