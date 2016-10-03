
package panels;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Mike Fouche
 */
public class AircraftModelGraphic 
{
    private JPanel acPanel;
    
    private TransformGroup objAircraft;

    // For performing the aircraft translation/rotations
    private final Transform3D acRot1;
    private final Transform3D acRot2;
    private final Transform3D acRot3;
    private final Transform3D acRot4;
    private final Transform3D acRot5;

    // ??
    private SimpleUniverse universe;
    private Canvas3D canvas3D;
    private Background background;
    private BoundingSphere bounds;
    
    /** Value - {@value } Aircraft roll, pitch, and heading variables */
    private double acRoll, acPitch, acYaw;
    
    /**
     * Constructor
     */
    public AircraftModelGraphic()
    {
        // Initialize the aircraft attitude
        acRoll = 0.0d;
        acPitch = 0.0d;
        acYaw = 0.0d;
        
        // Instantiate transforms
        acRot1 = new Transform3D();
        acRot2 = new Transform3D();
        acRot3 = new Transform3D();
        acRot4 = new Transform3D();
        acRot5 = new Transform3D();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Updates the aircraft model rotation/translation transform objects
     * with the member roll, pitch, and heading attitude values as inputs.
     * 
     * @param ahrs
     */
    public void updateModelDynamics(double[] ahrs)
    {
        acRoll = ahrs[0];
        acPitch = ahrs[1];
        acYaw = ahrs[2];

        // Set up aircraft to face left in window
        acRot1.rotZ(90.0d * (Math.PI / 180.0d) );       
        acRot2.rotY(90.0d * (Math.PI / 180.0d) );      
        acRot1.mul(acRot2);
        
        // Now perform yaw-pitch-roll rotations
        acRot3.rotZ(-acYaw * (Math.PI / 180.0d) );  // (-) = positive yaw
        acRot1.mul(acRot3);
        acRot4.rotX(acPitch * (Math.PI / 180.0d) );   // (+) = positive pitch
        acRot1.mul(acRot4);
        acRot5.rotY(acRoll * (Math.PI / 180.0d) );   // (+) = positive roll
        acRot1.mul(acRot5);
        // Set the distance between view and aircraft
        acRot1.setTranslation(new Vector3f(0f,0f,-0.6f)); 
        objAircraft.setTransform(acRot1); 
        
    } 
    
    //--------------------------------------------------------------------------
    /**
     * 1. Sets up a JPanel
     * <p>
     * 2. Loads the aircraft model via method createACScene
     * <p>
     * 3. Creates the Canvas3D object and loads the aircraft model
     * <p>
     * 4. Loads the completed Canvas3D object into the JPanel
     * 
     * @return the JPanel 
     */
    public JPanel createContentPane_AC()
    {
        acPanel = new JPanel();
        
        // you have to set a layout manager on the panel, which 
        // automatically expands the child components to the full area. 
        // A JPanel has a FlowLayout by default, 
        // which does not expand the child components.
        acPanel.setLayout(new BorderLayout());
        acPanel.setPreferredSize(new Dimension(490,490));
        acPanel.setOpaque(true);
        
        GraphicsConfiguration config = SimpleUniverse
        .getPreferredConfiguration();
        canvas3D = new Canvas3D(config);
        acPanel.add("Center", canvas3D);
        
        // Load the aircraft model (from method createACScene)
        BranchGroup scene = createACScene();
        
        // Set up the background instance and properties
        background = new Background();
        bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        background.setApplicationBounds(bounds);
        
        // Set the properties to allow the background to be updated.
        // This is needed if the background is continuously updated.
        // For this application the background remains the same.
        background.setCapability(Background.ALLOW_COLOR_WRITE);
        
        // Set the background color
        int rcol = 102, gcol = 178, bcol = 255;
        float rcolf = (float)rcol / 255.0f;
        float gcolf = (float)gcol / 255.0f;
        float bcolf = (float)bcol / 255.0f;
        background.setColor( new Color3f(rcolf,gcolf,bcolf) );
        
        // Add the background to the scene instance
        scene.addChild(background);
        // Build the scene
        scene.compile();
        
        universe = new SimpleUniverse(canvas3D);
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
        
        // Add the canvas to the JPanel
        acPanel.add(canvas3D);
        
        return acPanel;
    
    } 
    
    //--------------------------------------------------------------------------
    /**
     * 1. Sets up the transforms with the correct initial rotations/translation
     * <p>
     * 2. Loads the aircraft model (wavefront format)
     * <p>
     * 3. Sets up the lighting (from opposite sides)
     * <p>
     * 4. Loads the background
     * @return the BranchGroup with loaded aircraft model
     */
    public BranchGroup createACScene() 
    {
        BranchGroup objRoot = new BranchGroup();
        
        // Set up aircraft to face left in window
        acRot1.rotZ(90.0d * (Math.PI / 180.0d) );       
        acRot2.rotY(90.0d * (Math.PI / 180.0d) );      
        acRot1.mul(acRot2);
        
        // Now do yaw-pitch-roll rotations
        acRot3.rotZ(-acYaw * (Math.PI / 180.0d) );  // (-) = positive yaw
        acRot1.mul(acRot3);
        acRot4.rotX(acPitch * (Math.PI / 180.0d) );   // (+) = positive pitch
        acRot1.mul(acRot4);
        acRot5.rotY(acRoll * (Math.PI / 180.0d) );   // (+) = positive roll
        acRot1.mul(acRot5);
        
        TransformGroup objAC = new TransformGroup(acRot1); 
        objAC.setTransform(acRot1); 
        
        acRot1.setTranslation(new Vector3f(0f,0f,-0.6f)); // mf
        objAC.setTransform(acRot1); 

        // Permissions have to be set to allow rotation/translation updates
        objAC.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // Loader
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;
        String fname;
        
        try 
        {
            // Get the user directory - then add on the aircraft folder and file name
            fname = System.getProperty("user.dir").concat("\\F-14 Model\\F-14A_Tomcat.obj");
            scene = file.load(fname);

        } catch (IncorrectFormatException | ParsingErrorException | FileNotFoundException e) 
        {
            System.err.println(e);
            System.exit(1);
        }
        
        objAC.addChild(scene.getSceneGroup());

        // Set the lighting from both directions
        
        // Lighting and bounds #1
        DirectionalLight dLight1 = new DirectionalLight(new Color3f(1.0f, 1.0f,
            1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        dLight1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0d, 0.0d,
            0.0d), 100.0d));
        
        // Lighting and bounds #2
        DirectionalLight dLight2 = new DirectionalLight(new Color3f(1.0f, 1.0f,
            1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
        dLight1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0d, 0.0d,
            0.0d), 100.0d));
        
        // Add the light objects to the TransformGroup object
        objAC.addChild(dLight1);
        objAC.addChild(dLight2);
        
        // Add the background object to the TransformGroup object
        objAC.addChild(background);

        // Set this TransformGroup object equal to the local TransformGroup.
        // The objAircraft object will be used as the aircraft model is 
        // updated each time.
        objAircraft = objAC;
        
        // Add the TransformGroup object to the BranchGroup object.
        objRoot.addChild(objAC);
        
        return objRoot;
  }  
    
} // end of class AircraftModelGraphic
