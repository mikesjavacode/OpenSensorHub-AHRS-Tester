
package panels;

import attTester.AHRSPlayBack;
import attTester.AHRSRealTime;
import attTester.DataStore;
import attTester.ThreadQueueCmd;
import attTester.ThreadQueueRcv;
import attTester.Thread_Queue;
import attTester.TwoWaySerialComm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.border.EmptyBorder;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * This class's JPanel method loads the 3 JPanels for:
 * - the roll, pitch, and heading attitude text boxes as 
 * well as the system output text box (largest of all of them).
 * - the attitude graphic bars
 * - the aircraft model
 * <p>
 * It also provides the menu bar logic - the JMenu and JMenuItem objects for
 * start/stop of data acquisition, playback, setting the COM port, and 
 * changing the attitude bar colors.
 * 
 * @author Mike Fouche
 */
public class AttGraphicsContainer 
{
    // Can be a JDesktopPane or JPanel
    private final JComponent fdt;

    // Can be a JInternalFrame or JPanel
    private JComponent f1;
    private JComponent f2;
    private JComponent f3;
    
    private JPanel subPanel1;
    private JPanel subPanel2;
    private JPanel subPanel3;
    
    private final AttitudeGraphic      ag;
    private final AircraftModelGraphic ac;
    private final Thread_Queue         TQ;
             
    private final JLabel rlabel2;
    private final JLabel plabel2;
    private final JLabel ylabel2;

    private Color red;
    private Color blue;
    private Color green;
    
    private ThreadQueueRcv rQ;
    private ThreadQueueCmd cQ;
    
    //**************************************************************************
    // HashMap and String Array for COM port setting
    private HashMap<String,Integer> com;
    
    private String[] comArray;
    //**************************************************************************

    //**************************************************************************
    // HashMap, String Array, and Color array for Roll color settings
    private HashMap<String,Color> rColor;
    
    private String[] rollColorArray1;
    private Color[] rollColorArray2;
    //**************************************************************************

    //**************************************************************************
    // HashMap, String Array, and Color array for Pitch color settings
    private HashMap<String,Color> pColor;
    
    private String[] pitchColorArray1;
    private Color[] pitchColorArray2;
    //**************************************************************************
    
    //**************************************************************************
    // HashMap, String Array, and Color array for Heading color settings
    private HashMap<String,Color> hColor;
    
    private String[] headColorArray1;
    private Color[] headColorArray2;
    //**************************************************************************

    //**************************************************************************
    // JMenuItem arrays for COM port settings 
    private JMenuItem[] jMCArray; 
    
    //**************************************************************************

    //**************************************************************************
    // JMenuItem arrays for Roll, Pitch, and Heading color settings 
    private JMenuItem[] jMRColArray; 
    private JMenuItem[] jMPColArray; 
    private JMenuItem[] jMHColArray; 
    
    //**************************************************************************

    //**************************************************************************
    // JMenu arrays for Roll, Pitch, Heading 
    private JMenu[] jMAttArray; 
    private String[] AttArray;
    
    //**************************************************************************
    
    // String COM port setting variable ("COM1" or "COM2", etc.)
    private String ComSet;
    
    /**
     * Constructor #1 - This is used for the Desktop framework - 
     * i.e. movable (JInternalFrame) panels.
     * 
     * @param fdesktop 
     */
    public AttGraphicsContainer(JDesktopPane fdesktop)
    {
        ag = new AttitudeGraphic();
        ac = new AircraftModelGraphic();
        TQ = new Thread_Queue();
    
        // Set up the desktop
        fdt = fdesktop;
        
        rlabel2 = new JLabel();
        plabel2 = new JLabel();
        ylabel2 = new JLabel();
        
        cQ = new ThreadQueueCmd();
        rQ = new ThreadQueueRcv();
    }        
    
    /**
     * Constructor #2 - This is used for the non-Desktop framework - 
     * i.e. fixed (JPanel) panels.
     */
    public AttGraphicsContainer()
    {
        // Declare and create instance of AttitudeGraphic 
        ag = new AttitudeGraphic();
        
        // Declare and create instance of AircraftModelGraphic
        ac = new AircraftModelGraphic();
        
        TQ = new Thread_Queue();

        // Set the main JPanel
        fdt = new JPanel();
        
        // Explain why they need to be initialized here ...
        rlabel2 = new JLabel();
        plabel2 = new JLabel();
        ylabel2 = new JLabel();
    }        
    
    //--------------------------------------------------------------------------
    /**
    * Loads all of the graphics objects and 
    * is the JPanel container which is loaded into the JFrame.
    * <p>
    * 1.  creates text
    * <p>
    * 2.  creates the menu bar and all menu items and associated listeners
    * 
    */
    public void createContentPane_AHRS()
    {
        // Set Desktop parameters
        fdt.setLayout(new FlowLayout(1,10,10));
        fdt.setBackground(new Color(160,160,160));
        
        initializePanels();
        
    } 
    
    //--------------------------------------------------------------------------
    /**
     *
     */
    public void initializePanels()
    {
        red   = new Color(255, 0, 0);
        blue  = new Color(0, 0, 255);
        green = new Color(0, 255, 0);
        
        //************
        com = new HashMap<>();
        comArray = new String[]{"COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7",
          "COM8", "COM9", "COM10", "COM11", "COM12", "COM13", "COM14", "COM15"};
        //************
        
        //************
        rColor = new HashMap<>();
        rollColorArray1 = new String[]{"Roll to Red", "Roll to Green", "Roll to Blue"};
        rollColorArray2 = new Color[]{red, green, blue};
        //************
        
        //************
        pColor = new HashMap<>();
        pitchColorArray1 = new String[]{"Pitch to Red", "Pitch to Green", "Pitch to Blue"};
        pitchColorArray2 = new Color[]{red, green, blue};
        //************
        
        //************
        hColor = new HashMap<>();
        headColorArray1 = new String[]{"Heading to Red", "Heading to Green", "Heading to Blue"};
        headColorArray2 = new Color[]{red, green, blue};
        //************
        
        jMCArray = new JMenuItem[15];         
        
        //************
        jMRColArray = new JMenuItem[3]; 
        jMPColArray = new JMenuItem[3]; 
        jMHColArray = new JMenuItem[3]; 
        //************
        
        //************
        jMAttArray = new JMenu[3]; 
        AttArray = new String[]{"Roll", "Pitch", "Heading"};
        //************
        
        // Load HashMap for COM port settings
        for (int i = 0; i < 15; i++)
        {
            com.put(comArray[i], i);
        }    

        // Load HashMaps for roll/pitch/heading attitude bar color settings
        for (int i = 0; i < 3; i++)
        {
            rColor.put(rollColorArray1[i], rollColorArray2[i]);
            pColor.put(pitchColorArray1[i], pitchColorArray2[i]);
            hColor.put(headColorArray1[i], headColorArray2[i]);
        }    

        //**********************************************************************
        // Create 1st JInternalFrame and sub-JPanel
        //**********************************************************************
        f1 = new JInternalFrame("Menu Bar");
        f1.setPreferredSize(new Dimension(900,50));
        f1.setBorder(new EmptyBorder(0,0,0,0));
        f1.setBackground(new Color(160,160,160));
        f1.setOpaque(true);
        f1.setVisible(true);
        
        // Add JInternalFrame to the JDesktopPane
        fdt.add(f1, new Integer( 10 ));
        
        //**********************************************************************
        // Create menu bar 
        //**********************************************************************
        JMenuBar b = new JMenuBar();
        b.setLayout(new FlowLayout(0,20,0));
        f1.add(b);
        
        //**********************************************************************
        // Launch (start and stop)JMenu header
        //**********************************************************************
        JMenu menuL = new JMenu("Data Acquisition");
        // add to the menu bar
        b.add(menuL);

        //----------------------------------------------------------------------
        // Start/Stop JMenuItems.  Add listeners so that the code can detect
        // if the user has selected "start" or "stop" for data acquisition.
        StartStopSelect sss = new StartStopSelect();

        JMenuItem menuStart = new JMenuItem("Start data acquisition");
        menuL.add(menuStart);
        menuStart.addActionListener(sss);
        
        JMenuItem menuStop = new JMenuItem("Stop");
        menuL.add(menuStop);
        menuStop.addActionListener(sss);

        //----------------------------------------------------------------------
        // AHRS type
        JMenu menuAHRS = new JMenu("AHRS Model");
        // Add to the menu bar
        b.add(menuAHRS);
        
        AHRSModel am = new AHRSModel();
        
        // 3DM-GX2
        JMenuItem ahrs3DMGX2 = new JMenuItem("3DM-GX2");
        menuAHRS.add(ahrs3DMGX2);
        ahrs3DMGX2.addActionListener(am);

        // 3DM-GX4-25
        JMenuItem ahrs3DMGX4_25 = new JMenuItem("3DM-GX4-25");
        menuAHRS.add(ahrs3DMGX4_25);
        ahrs3DMGX4_25.addActionListener(am);
        
        // 3DM-GX3-35
        JMenuItem ahrs3DMGX3_35 = new JMenuItem("3DM-GX3-35");
        menuAHRS.add(ahrs3DMGX3_35);
        ahrs3DMGX3_35.addActionListener(am);
        
        // 3DM-GX3-35
        JMenuItem ahrs3DMGX3_25_OEM = new JMenuItem("3DM-GX3-25_OEM");
        menuAHRS.add(ahrs3DMGX3_25_OEM);
        ahrs3DMGX3_25_OEM.addActionListener(am);

        //**********************************************************************
        // COM port setting JMenu header 
        //**********************************************************************
        JMenu menuComm = new JMenu("Set COM Port");
        // Add to the menu bar
        b.add(menuComm);

        //----------------------------------------------------------------------
        // COM Ports 1-15 JMenuItems - add listeners so that code detects 
        // if user has selected this COM port setting.
        for (int i = 0; i < 15; i++)
        {
            CommSelect cSelect = new CommSelect();
            
            // Create COM port JMenuItem instance
            jMCArray[i] = new JMenuItem(comArray[i]);
            // Add to the COM Port JMenu header
            menuComm.add(jMCArray[i]);
            // Add listener
            jMCArray[i].addActionListener(cSelect);
        }    
        
        //**********************************************************************
        // Attitude bar JMenu header
        //**********************************************************************
        JMenu menuAtt = new JMenu("Attitude Bar Options");
        // Add to the menu bar
        b.add(menuAtt);

        BarColors bc = new BarColors();
        
        //----------------------------------------------------------------------
        // Attitude JMenu objects - Roll, Pitch, Heading sub-menus
        for (int i = 0; i < 3; i++)
        {
            // Create attitude (Roll/Pitch/Heading) JMenu instance
            jMAttArray[i] = new JMenu(AttArray[i]);
            // Add to the attitude bar JMenu header
            menuAtt.add(jMAttArray[i]);
        }

        //----------------------------------------------------------------------
        // ROLL JMenuItem color objects (choices) - 3 loops for 3 colors

        for (int i = 0; i < 3; i++)
        {
            // Create roll attitude bar color choice JMenuItem instance
            jMRColArray[i] = new JMenuItem(rollColorArray1[i]);
            // Add listener
            jMRColArray[i].addActionListener(bc);
            // Add to the main attitude JMenu object
            jMAttArray[0].add(jMRColArray[i]);
        }    
        
        //----------------------------------------------------------------------
        // PITCH JMenuItem color objects (choices) - 3 loops for 3 colors

        for (int i = 0; i < 3; i++)
        {
            // Create pitch attitude bar color choice JMenuItem instance
            jMPColArray[i] = new JMenuItem(pitchColorArray1[i]);
            // Add listener
            jMPColArray[i].addActionListener(bc);
            // Add to the main attitude JMenu object
            jMAttArray[1].add(jMPColArray[i]);
        }    

        //----------------------------------------------------------------------
        // HEADING JMenuItem color objects (choices) - 3 loops for 3 colors

        for (int i = 0; i < 3; i++)
        {
            // Create heading attitude bar color choice JMenuItem instance
            jMHColArray[i] = new JMenuItem(headColorArray1[i]);
            // Add listener
            jMHColArray[i].addActionListener(bc);
            // Add to the main attitude JMenu object
            jMAttArray[2].add(jMHColArray[i]);
        }    
        
        //**********************************************************************
        // Playback JMenu header 
        //**********************************************************************
        JMenu menuMType = new JMenu("Playback");
        // Add to menu bar
        b.add(menuMType);

        //----------------------------------------------------------------------
        // MySQL playback JMenuItem - add listener so that the code can detect
        // if the user has selected "playback". 
        PlayBack pb = new PlayBack();
        
        JMenuItem menuMySQL = new JMenuItem("Start playback");
        menuMType.add(menuMySQL);
        menuMySQL.addActionListener(pb);

        //**********************************************************************
        // Use text file instead of MySQL database table JMenu header 
        //**********************************************************************
        JMenu menuDStore = new JMenu("Data Storage");
        // Add to the menu bar
        b.add(menuDStore);

        //----------------------------------------------------------------------
        // Data storage playback JMenuItem - add listener so that the code can detect
        // if the user wants to use a text file 
        DataStorage ds = new DataStorage();
        
        JMenuItem menuTxt = new JMenuItem("Use text file");
        menuDStore.add(menuTxt);
        menuTxt.addActionListener(ds);

        //----------------------------------------------------------------------
        // Data storage playback JMenuItem - add listener so that the code can detect
        // if the user wants to use a MySQL table 
        JMenuItem menuSQL = new JMenuItem("Use MySQL table");
        menuDStore.add(menuSQL);
        menuSQL.addActionListener(ds);
        
        //**********************************************************************
        // Create 2nd JInternalFrame or JPanel
        //**********************************************************************
        if( fdt instanceof JDesktopPane )
        {
            f2 = new JInternalFrame("Data Display");
        }
        else
        {
            f2 = new JPanel();
        }
        
        f2.setPreferredSize(new Dimension(500,600));
        f2.setBackground(new Color(160,160,160));
        f2.setBorder(new EmptyBorder(0,0,0,0));
        f2.setOpaque(true);
        f2.setVisible(true);
        f2.setLayout(new FlowLayout(1,5,5));
        
        //**********************************************************************
        // Set properties of attitude bar graphic JPanel - load into this JPanel
        //**********************************************************************
        ag.panelSetup();
        f2.add(ag);
        fdt.add(f2);

        //**********************************************************************
        // Load digital readout JPanel into this JPanel
        //**********************************************************************
        DigDisplayContainer dd = new DigDisplayContainer(rlabel2,plabel2,ylabel2);
        f2.add(dd.digiLoad());

        //**********************************************************************
        // Create 3rd JInternalFrame or JPanel
        //**********************************************************************
        if( fdt instanceof JDesktopPane )
        {
            f3 = new JInternalFrame("Aircraft Display");
        }
        else
        {
            f3 = new JPanel();
        }
        
        f3.setPreferredSize(new Dimension(500,600));
        f3.setBackground(new Color(160,160,160));
        f3.setBorder(new EmptyBorder(0,0,0,0));
        f3.setOpaque(true);
        f3.setVisible(true);
        f3.setLayout(new FlowLayout(1,5,5));

        //**********************************************************************
        // Load aircraft model JPanel into this JPanel
        //**********************************************************************
        ACContainer acc = new ACContainer(ac);
        f3.add(acc.acLoad());
        fdt.add(f3);
    } 

    //--------------------------------------------------------------------------
    // Inner class for Tester start / stop selection options
    private class StartStopSelect implements ActionListener
    {   
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            String eAction = ae.getActionCommand();
            if( null != eAction )
            {
                if ("Start data acquisition".equals(eAction))
                {
                    // Set MySQL paramters
                    DataStore.getSInstc().setMySQLrealTime();
                    DataStore.getSInstc().setAttCom(206);
                    DataStore.getSInstc().setAttLaunch(true);
                    rQ.setTesterStatus(true);
                    cQ.setTesterStatus(true);

                    try
                    {
                        String ahrsModel = DataStore.getSInstc().getAHRSModel();
                        
                        if ( ComSet != null )
                        {
                           
                            // Write the COM port setting to a file.  The next time
                            // the code is run, if the user doesn't select a new
                            // COM port setting then the one from the file will be used.
                            String fin = "COM.txt";
                            FileWriter fc = new FileWriter(fin);
                            // Write the value to a text file.
                            fc.write(ComSet);
                            fc.close();
                            // Launch the thread to begin the serial communications
                            (new TwoWaySerialComm(TQ,cQ,rQ,ahrsModel)).connect(ComSet);
                            // Launch the thread to process the AHRS data real time
                            (new AHRSRealTime(TQ,ac,ag)).start();
                        }    
                        else
                        {
                            String fin = "COM.txt";
                            File fil = new File(fin);

                            // A COM port setting was not selected - therefore the 
                            // COM port setting value will be read from the file.
                            if ( fil.exists() ) // see if file exists
                            {
                                try
                                {
                                    FileReader fc = new FileReader(fin);
                                    BufferedReader fr = new BufferedReader(fc);
                                    ComSet = fr.readLine();
                                    fc.close();
                                }
                                catch(FileNotFoundException ef)
                                {
                                    System.out.println("Except: "+ef);
                                }    

                                // Launch the thread to begin the serial communications
                                (new TwoWaySerialComm(TQ,cQ,rQ,ahrsModel)).connect(ComSet);
                                // Launch the thread to process the AHRS data real time
                                (new AHRSRealTime(TQ,ac,ag)).start();
                            }
                        }

                    }
                    catch ( IOException ex )
                    {
                        System.out.println("Exception: "+ex+", in actionPerformed method");
                    }

                }
                else if ("Stop".equals(eAction))
                {
                    // stop serial writer and serial reader threads
                    DataStore.getSInstc().setAttCom(0); 
                    // stop graphics thread
                    DataStore.getSInstc().setAttLaunch(false);
                    rQ.setTesterStatus(false);
                    cQ.setTesterStatus(false);
                }
            }
        }
    } // end of inner class StartStopSelect
    
    //--------------------------------------------------------------------------
    // Inner class for comm port selection options
    private class CommSelect implements ActionListener
    {   
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            String eAction = ae.getActionCommand();
            if( null != eAction )
                if ( com.containsKey(eAction) )
                {
                    ComSet = eAction;
                }        
        }
    } // end of inner class CommSelect
    
    //--------------------------------------------------------------------------
    // Inner class for playback selection options
    private class PlayBack implements ActionListener
    {   
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            String eAction = ae.getActionCommand();
            if( null != eAction )
            {    
                if ("Start playback".equals(eAction))
                {
                    // Launch thread to playback the data from the MySQL table.
                    (new AHRSPlayBack(ac,ag)).start();
                }
            }    
        }
    } // end of inner class PlayBack

    //--------------------------------------------------------------------------
    // Inner class for data storage selection options
    private class DataStorage implements ActionListener
    {   
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            String eAction = ae.getActionCommand();
            if( null != eAction )
            {    
                if ("Use text file".equals(eAction))
                {
                    DataStore.getSInstc().setUseTextFile(true);
                    DataStore.getSInstc().setTextSys("\nUsing text file instead of MySQL table ...\n" );
                    DataStore.getSInstc().setTextCaretPosition();
                }
                else if ("Use MySQL table".equals(eAction))
                {
                    DataStore.getSInstc().setUseTextFile(false);
                    DataStore.getSInstc().setTextSys("\nUsing MySQL table ...\n" );
                    DataStore.getSInstc().setTextCaretPosition();
                }    
            }    
        }
    } // end of inner class DataStorage

    //--------------------------------------------------------------------------
    // Inner class for attitude bar color selection options
    private class BarColors implements ActionListener
    {   
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            String eAction = ae.getActionCommand();
            if( null != eAction )
            {    
                // Set Roll attitude bar color based on user selection
                if (rColor.containsKey(eAction))
                {
                    DataStore.getSInstc().setRColor(rColor.get(eAction));
                    rlabel2.setForeground(rColor.get(eAction));
                    ag.ahrsBarUpdate();
                }
                // Set Pitch attitude bar color based on user selection
                else if (pColor.containsKey(eAction))
                {
                    DataStore.getSInstc().setPColor(pColor.get(eAction));
                    plabel2.setForeground(pColor.get(eAction));
                    ag.ahrsBarUpdate();
                }
                // Set Heading attitude bar color based on user selection
                else if (hColor.containsKey(eAction))
                {
                    DataStore.getSInstc().setHColor(hColor.get(eAction));
                    ylabel2.setForeground(hColor.get(eAction));
                    ag.ahrsBarUpdate();
                }
            }    
        }
    } // end of inner class BarColors

    //--------------------------------------------------------------------------
    // Inner class for playback selection options
    private class AHRSModel implements ActionListener
    {   
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            String eAction = ae.getActionCommand();
            if( null != eAction )
            {    
                DataStore.getSInstc().setAHRSModel(eAction);
            }    
        }
    } // end of inner class AHRSModel
    
    //--------------------------------------------------------------------------
    /**
     *
     * @return
     */
    public JPanel getGraphicsPanel()
    {
        JPanel graphicsPanel = (JPanel)fdt;
        
        return graphicsPanel;
    }
    
} // end of class AttGraphicsContainer
