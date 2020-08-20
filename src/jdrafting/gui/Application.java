package jdrafting.gui;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleMnemonic;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import com.sun.istack.internal.NotNull;

import cla.ParsedParameterMap;
import jdrafting.Exercise;
import jdrafting.geom.JDStrokes;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.controller.actions.AboutAction;
import jdrafting.gui.controller.actions.AngleAction;
import jdrafting.gui.controller.actions.ArcAction;
import jdrafting.gui.controller.actions.ArrowAction;
import jdrafting.gui.controller.actions.AxialSymmetryAction;
import jdrafting.gui.controller.actions.BackwardAction;
import jdrafting.gui.controller.actions.BisectrixAction;
import jdrafting.gui.controller.actions.BoundsAction;
import jdrafting.gui.controller.actions.CanvasColorAction;
import jdrafting.gui.controller.actions.CapableArcAction;
import jdrafting.gui.controller.actions.CentralSymmetryAction;
import jdrafting.gui.controller.actions.CircumferenceAction;
import jdrafting.gui.controller.actions.CopySelectedAction;
import jdrafting.gui.controller.actions.DeleteSelectedAction;
import jdrafting.gui.controller.actions.DivisionPointsAction;
import jdrafting.gui.controller.actions.EllipseAction;
import jdrafting.gui.controller.actions.EndAction;
import jdrafting.gui.controller.actions.ExerciseMetadataAction;
import jdrafting.gui.controller.actions.ExitAction;
import jdrafting.gui.controller.actions.ExtremesAction;
import jdrafting.gui.controller.actions.EyedropperAction;
import jdrafting.gui.controller.actions.ForwardAction;
import jdrafting.gui.controller.actions.FragmentAction;
import jdrafting.gui.controller.actions.FreeHandAction;
import jdrafting.gui.controller.actions.FusionAction;
import jdrafting.gui.controller.actions.HomothetyAction;
import jdrafting.gui.controller.actions.HyperbolaAction;
import jdrafting.gui.controller.actions.IntersectionsAction;
import jdrafting.gui.controller.actions.InvertSelectionAction;
import jdrafting.gui.controller.actions.LookFeelAction;
import jdrafting.gui.controller.actions.MathFunctionAction;
import jdrafting.gui.controller.actions.MediatrixAction;
import jdrafting.gui.controller.actions.MidpointAction;
import jdrafting.gui.controller.actions.ModifySegmentAction;
import jdrafting.gui.controller.actions.MoveZBufferAction;
import jdrafting.gui.controller.actions.NewAction;
import jdrafting.gui.controller.actions.OpenAction;
import jdrafting.gui.controller.actions.ParabolaAction;
import jdrafting.gui.controller.actions.ParallelAction;
import jdrafting.gui.controller.actions.PasteStyleAction;
import jdrafting.gui.controller.actions.PerpendicularAction;
import jdrafting.gui.controller.actions.PointAction;
import jdrafting.gui.controller.actions.PointColorAction;
import jdrafting.gui.controller.actions.PolyLineAction;
import jdrafting.gui.controller.actions.PolygonAction;
import jdrafting.gui.controller.actions.PrintAction;
import jdrafting.gui.controller.actions.ProtractorAction;
import jdrafting.gui.controller.actions.RectangleAction;
import jdrafting.gui.controller.actions.RedoAction;
import jdrafting.gui.controller.actions.RegularPolygonAction;
import jdrafting.gui.controller.actions.RewindAction;
import jdrafting.gui.controller.actions.RotationAction;
import jdrafting.gui.controller.actions.RulerAction;
import jdrafting.gui.controller.actions.SaveAction;
import jdrafting.gui.controller.actions.SaveImageAction;
import jdrafting.gui.controller.actions.SegmentAction;
import jdrafting.gui.controller.actions.SelectAllAction;
import jdrafting.gui.controller.actions.SelectionAction;
import jdrafting.gui.controller.actions.ShapeColorAction;
import jdrafting.gui.controller.actions.SplineAction;
import jdrafting.gui.controller.actions.TextVisibleAction;
import jdrafting.gui.controller.actions.TranslationAction;
import jdrafting.gui.controller.actions.TriangleAction;
import jdrafting.gui.controller.actions.TrianglePointsAction;
import jdrafting.gui.controller.actions.UndoAction;
import jdrafting.gui.controller.actions.VertexAction;
import jdrafting.gui.controller.actions.ZoomAllAction;
import jdrafting.gui.controller.actions.ZoomInOutAction;
import jdrafting.gui.controller.mouse.HandListener;
import jdrafting.gui.controller.mouse.TrianglePointsListener;

/**
 * {@value #APPNAME} GUI class
 * @author {@value #AUTHOR}, {@value #COPYLEFT}
 * @since 0.1.0
 * @version {@value #VERSION}
 */
@SuppressWarnings("serial")
public class Application extends JFrame
{
	//////////////////////
	// STATIC CONSTANTS //
	//////////////////////
	// metainfo
	public static final String APPNAME = "JDrafting";
	public static final String VERSION = "0.1.10.2";
	public static final String AUTHOR = "Miguel Alejandro Moreno Barrientos";
	public static final String COPYLEFT = "2016,2020";
	public static final String PROJECT_PAGE = 
									"http://miguelalejandromorenobarrientos.github.io/jdrafting";
	public static final String GITHUB_REPOSITORY =	
						"https://github.com/miguelalejandromorenobarrientos/jdrafting/tree/master";
	// colors
	public static Color toolMainColor = Color.BLUE;
	// separators
	private static final Dimension HSEP = new Dimension( 12, 0 ); 
	private static final Dimension VSEP = new Dimension( 0, 12 ); 


	/////////////////
	// STATIC VARS //
	/////////////////
	public static Locale locale = Locale.getDefault(); 
	public static String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
	public static boolean jmeEnabled = true;
	
	
	///////////////////
	// INSTANCE VARS //
	///////////////////
	private ActionMap actionMap = new ActionMap();
	private Exercise exercise;
	private Set<JDraftingShape> selectedShapes;
	private double angle = 90.;
	private double distance = 0.1;
	private boolean useDistance = false;
	private double flatnessValue = 10000.;  // flatness for curved shapes
	private String saveFilename;
	private boolean visibleNames = true;
	// style
	private Color color = Color.BLACK;
	private BasicStroke stroke = JDStrokes.PLAIN_ROUND.getStroke();
	private Color pointColor = Color.DARK_GRAY;
	private BasicStroke pointStroke = 
							new BasicStroke( 8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
	// undo/redo
	public UndoManager undoManager;
	public UndoableEditSupport undoSupport;
	// GUI components
	public Container contentPanel;
	public JPanel centerPanel;
	public JPanel northPanel;
	public JPanel statusPanel;
	public CanvasPanel canvas;
	public JMenuBar menubar;
	public JMenu menuFile;
	public JMenu menuEdit;
	public JMenu menuStyle;
	public JMenu menuShapes;
	public JMenu menuExercise;
	public JMenu menuTools;
	public JMenu menuTransform;
	public JMenu menuTrianglePoints;
	public JMenu menuPolygon;
	public JMenu menuConics;
	public JMenu menuView;
	public JMenu menuAppearance;
	public JMenu menuHelp;
	public JToolBar styleToolbar,rulerProtToolbar, actionbar,shapebar,toolbar;
	public JSpinner spinThickness, spinPointThickness, spinAngle;
	public JButton buttonEyedropper, buttonPasteStyle;
	public JButton buttonColor, buttonPointColor;
	public JButton buttonRuler, buttonProtactor;
	public JCheckBox checkRuler;
	public JButton buttonMultiplier;
	public JComboBox<BasicStroke> comboLineStyle;
	public JToggleButton toggleNames;
	public JLabel labelStatus;
	public ShapeList shapeList;
	public JScrollPane scrollList;
	public JSplitPane splitPanel;
	public Toast currentToast;
	
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////
	public Application()
	{
		// easter egg joke
		final int year = Calendar.getInstance().get( Calendar.YEAR );
		if ( year >= 2030 )
			System.out.println( "\n\tYeah! My program still running in " + year + "!!\n" );
		
		// create app window
		initUI();
		createActions();
		canvas.requestFocus();
		setExtendedState( getExtendedState() | JFrame.MAXIMIZED_BOTH );
		
		// start new exercise
		setExercise( new Exercise() );

	    // set default Look and Feel
		JFrame.setDefaultLookAndFeelDecorated( false );
	    JDialog.setDefaultLookAndFeelDecorated( false );    

		try
		{ 
			UIManager.setLookAndFeel( lookAndFeelClassName );
		}
		catch ( Exception e )
		{
			System.out.println( e );
			System.exit( -1 );
		}
		SwingUtilities.updateComponentTreeUI( this );
		
		// exit app
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				Application.this.exit();
			}
		} );
	}
	
	
	/////////
	// GUI //
	/////////

	/**
	 * Initialize GUI components
	 */
	private void initUI()
	{
		// config frame
		setIconImage( getSmallIcon( "jdrafting.png" ).getImage() );
		setPreferredSize( JDUtils.getScreenSize( 0.8f, 0.8f ) );

		// --- MENUBAR
		setJMenuBar( menubar = new JMenuBar() );
		// menu File
		menubar.add( menuFile = new JMenu( getLocaleText( "file" ) ) );
		menuFile.setMnemonic( getLocaleMnemonic( "mne_menu_file" ) );
		// menu Edit
		menubar.add( menuEdit = new JMenu( getLocaleText( "edit" ) ) );
		menuEdit.setMnemonic( getLocaleMnemonic( "mne_menu_edit" ) );
		// menu Style
		menubar.add( menuStyle = new JMenu( getLocaleText( "style" ) ) );
		menuStyle.setMnemonic( getLocaleMnemonic( "mne_menu_style" ) );
		// menu Shapes
		menubar.add( menuShapes = new JMenu( getLocaleText( "shapes" ) ) );
		menuShapes.setMnemonic( getLocaleMnemonic( "mne_menu_shapes" ) );
		// menu Tools
		menubar.add( menuTools = new JMenu( getLocaleText( "tools" ) ) );
		menuTools.setMnemonic( getLocaleMnemonic( "mne_menu_tools" ) );
			// submenu transform
			menuTools.add( menuTransform = 
								new JMenu( getLocaleText( "transforms" ) ) );
			menuTransform.setMnemonic( 
									getLocaleMnemonic( "mne_menu_transform" ) );
			menuTools.addSeparator();
		// menu Exercise
		menubar.add( menuExercise = new JMenu( getLocaleText( "exercise" ) ) );
		menuExercise.setMnemonic( getLocaleMnemonic( "mne_menu_exercise" ) );
		// menu View
		menubar.add( menuView = new JMenu( getLocaleText( "view" ) ) );
		menuView.setMnemonic( KeyEvent.VK_V );
			// submenu Appearance
			menuView.add( menuAppearance = 
								new JMenu( getLocaleText( "appearance" ) ) );
				menuAppearance.setToolTipText( "GUI Theme" );
				menuAppearance.setMnemonic( 
										getLocaleMnemonic( "mne_menu_appea" ) );
			menuView.addSeparator();
		// menu Help
		menubar.add( menuHelp = new JMenu( getLocaleText( "help" ) ) );
		menuHelp.setMnemonic( getLocaleMnemonic( "mne_menu_help" ) );

		// --- PANELS
		// content panel
		contentPanel = getContentPane();
			// center panel
			contentPanel.add( centerPanel = new JPanel( new BorderLayout() ), 
							  BorderLayout.CENTER );
			// north panel
			contentPanel.add( northPanel = new JPanel(), BorderLayout.NORTH );
			northPanel.setLayout( 
							new BoxLayout( northPanel, BoxLayout.LINE_AXIS ) );
				JPanel auxPanel = new JPanel();
				northPanel.add( auxPanel );
				auxPanel.setBorder( 
						BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
				auxPanel.setLayout( 
							new BoxLayout( auxPanel, BoxLayout.LINE_AXIS ) );
				// style panel
				styleToolbar = new JToolBar( getLocaleText( "tit_style" ) );
				styleToolbar.addHierarchyListener( 
									(evt) -> Application.this.revalidate() );
				auxPanel.add( styleToolbar );
				styleToolbar.setLayout(
						new BoxLayout( styleToolbar, BoxLayout.LINE_AXIS ) );
				styleToolbar.setBorder( BorderFactory.createTitledBorder( 
											getLocaleText( "tit_style" ) ) );
				// ruler & protractor panel
				rulerProtToolbar = 
								new JToolBar( getLocaleText( "ruler_prot" ) );
				rulerProtToolbar.addHierarchyListener( 
									(evt) -> Application.this.revalidate() );
				auxPanel.add( rulerProtToolbar );
				rulerProtToolbar.setLayout( 
					new BoxLayout( rulerProtToolbar, BoxLayout.LINE_AXIS ) );
				rulerProtToolbar.setBorder( BorderFactory.createTitledBorder(
											getLocaleText( "ruler_prot" ) ) );
			// status panel
			contentPanel.add( 
				statusPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) ),
				BorderLayout.SOUTH );
			statusPanel.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
		
		// --- TOOLBARS
		// actionbar
		centerPanel.add( actionbar = new JToolBar( getLocaleText( "actions" ) ),
					 	 BorderLayout.NORTH );
		// shapebar
		centerPanel.add( shapebar = new JToolBar( getLocaleText( "shapes" ),
							  					  JToolBar.VERTICAL ),
						 BorderLayout.WEST );
		// toolbar
		centerPanel.add( toolbar = new JToolBar( getLocaleText( "tools" ) ), 
						 BorderLayout.SOUTH );
		
		// --- STYLE PANEL
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );		
		// eyedropper button
		styleToolbar.add( buttonEyedropper = new JButton() );
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		// paste style button
		styleToolbar.add( buttonPasteStyle = new JButton() );
		styleToolbar.add( Box.createHorizontalStrut( 12 ) );
		// jlabel line thickness
		JLabel labelThickness =
					new JLabel( getLocaleText( "thickness" ), JLabel.RIGHT );
		labelThickness.setMaximumSize( labelThickness.getPreferredSize() );
		styleToolbar.add( labelThickness );
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		// line thikness spinner
		styleToolbar.add( spinThickness = new JSpinner( new SpinnerNumberModel( 
			getStroke().getLineWidth(), .1, Double.POSITIVE_INFINITY, 1. ) ) );
		spinThickness.setPreferredSize( new Dimension( 60, 30 ) );
		spinThickness.setMinimumSize( spinThickness.getPreferredSize() );
		spinThickness.setMaximumSize( spinThickness.getPreferredSize() );
		spinThickness.addChangeListener( evt -> {
			float thickness = 
						(float) (double) spinThickness.getModel().getValue();
			BasicStroke bs = (BasicStroke) comboLineStyle.getSelectedItem();
			setStroke( JDStrokes.getStroke( bs,	thickness ) );
		});
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		// line color button
		styleToolbar.add( buttonColor = new JButton() {
			@Override
			protected void paintComponent( Graphics g ) {
				super.paintComponent( g );
				g.setColor( getColor() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}
		} );
		buttonColor.setPreferredSize( new Dimension( 30, 30 ) );
		buttonColor.setMinimumSize( buttonColor.getPreferredSize() );
		buttonColor.setMaximumSize( buttonColor.getPreferredSize() );
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		// jcombobox line style
		styleToolbar.add( comboLineStyle = new LineStyleComboBox( this ) );
		comboLineStyle.setPreferredSize( new Dimension( 200, 30 ) );
		comboLineStyle.setMinimumSize( new Dimension( 100, 30 ) );
		comboLineStyle.setMaximumSize( comboLineStyle.getPreferredSize() );
		styleToolbar.add( Box.createHorizontalStrut( 12 ) );
		// jlabel point thickness
		JLabel labelPoint =
				new JLabel( getLocaleText( "point_thickness" ), JLabel.RIGHT );
		labelPoint.setMaximumSize( labelPoint.getPreferredSize() );
		styleToolbar.add( labelPoint );
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		// point thickness spinner
		styleToolbar.add( spinPointThickness = new JSpinner( 
				new SpinnerNumberModel( getPointStroke().getLineWidth(), .1, 
										Double.POSITIVE_INFINITY, 1. ) ) );
		spinPointThickness.setPreferredSize( new Dimension( 60, 30 ) );
		spinPointThickness.setMinimumSize( 
										spinPointThickness.getPreferredSize() );
		spinPointThickness.setMaximumSize( 
										spinPointThickness.getPreferredSize() );
		spinPointThickness.addChangeListener( evt -> {
			float thickness = 
				(float) (double) spinPointThickness.getModel().getValue();
			BasicStroke bs = getPointStroke();
			setPointStroke( new BasicStroke( thickness, bs.getEndCap(),
				bs.getLineJoin(), bs.getMiterLimit(), bs.getDashArray(),
				bs.getDashPhase() ) );
		});
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		// point color button
		styleToolbar.add( buttonPointColor = new JButton() {
			@Override
			protected void paintComponent( Graphics g ) {
				super.paintComponent( g );
				g.setColor( getPointColor() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}
		});
		buttonPointColor.setPreferredSize( new Dimension( 30, 30 ) );
		buttonPointColor.setMinimumSize( buttonPointColor.getPreferredSize() );
		buttonPointColor.setMaximumSize( buttonPointColor.getPreferredSize() );
		styleToolbar.add( Box.createHorizontalStrut( 3 ) );
		
		// --- RULER & PROTRACTOR PANEL
		rulerProtToolbar.add( Box.createHorizontalStrut( 3 ) );
		// ruler button
		rulerProtToolbar.add( buttonRuler = new JButton() );
		rulerProtToolbar.add( Box.createHorizontalStrut( 3 ) );
		// checkbox fixed distance
		rulerProtToolbar.add( checkRuler = 
								new JCheckBox( getLocaleText( "fix_dist" ) ) );
		checkRuler.setSelected( isUsingRuler() );
		checkRuler.setMaximumSize( checkRuler.getPreferredSize() );
		checkRuler.setMinimumSize( checkRuler.getPreferredSize() );
		checkRuler.addActionListener( 
							evt -> setUseDistance( checkRuler.isSelected() ) );
		// multiplier button
		rulerProtToolbar.add( buttonMultiplier = 
			new JButton( "<html><font color=blue size=2>xN</font></html>" ) );
		buttonMultiplier.setMaximumSize( buttonMultiplier.getPreferredSize() );
		buttonMultiplier.addActionListener( evt -> { 
			String result = (String) JOptionPane.showInputDialog( 
						this, getLocaleText( "dist_mult_dlg" ), "xN",
						JOptionPane.QUESTION_MESSAGE, 
						getLargeIcon( "ruler.png" ), null, "1" );
			if ( result == null )  return;
			try
			{
				setDistance( getDistance() * Double.valueOf( result ) );
			}
			catch ( NumberFormatException e )
			{
				JOptionPane.showMessageDialog( 
									this, e, "xN", JOptionPane.ERROR_MESSAGE );
			}
		} );
		rulerProtToolbar.add( Box.createHorizontalStrut( 12 ) );
		// protractor button
		rulerProtToolbar.add( buttonProtactor = new JButton() );
		// spinner angle
		rulerProtToolbar.add( spinAngle = new JSpinner( new SpinnerNumberModel( 
								getAngle(), 0., Math.nextDown( 360. ), 1. ) ) );
		spinAngle.setPreferredSize( new Dimension( 70, 30 ) );
		spinAngle.setMaximumSize( spinAngle.getPreferredSize() );
		spinAngle.setMinimumSize( spinAngle.getPreferredSize() );
		spinAngle.addFocusListener( new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e)
			{
				setAngle( (double) spinAngle.getModel().getValue() );
			}
		});
		spinAngle.addKeyListener( new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e)
			{
				setAngle( (double) spinAngle.getModel().getValue() );
			}
		});
		spinAngle.addChangeListener( 
				evt -> setAngle( (double) spinAngle.getModel().getValue() ) );
		rulerProtToolbar.add( new JLabel( getLocaleText( "degrees" ) ) );
		// final glue
		northPanel.add( Box.createHorizontalGlue() );

		// --- STATUS PANEL
		// status label
		statusPanel.add( labelStatus = new JLabel( " " ) );
		labelStatus.setFont( new Font( "serif", Font.ITALIC, 16 ) );
				
		// --- CENTER PANEL
		// canvas
		canvas = new CanvasPanel( this, new Viewport() );
		canvas.getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW ).put( 
								KeyStroke.getKeyStroke( "ESCAPE" ), "cancel" );
		canvas.getActionMap().put( "cancel", new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				canvas.setCanvasListener( new HandListener( canvas ) );
				canvas.repaint();
			}
		} );		
		// shape list
		scrollList = new JScrollPane( shapeList = new ShapeList( this ) );		
		// add split panel with scroll list and canvas to center
		splitPanel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, 
									 canvas, scrollList );
		splitPanel.setOneTouchExpandable( true );
		splitPanel.setDividerSize( 12 );
		splitPanel.setResizeWeight( 1. );  // extra space to canvas on resize
		centerPanel.add( splitPanel, BorderLayout.CENTER );
		
		// pack frame and set location
		pack();
		setLocationRelativeTo( null );  // center window
	}
	
	/**
	 * Define and place GUI actions
	 */
	private void createActions()
	{
		Action action;
		
		// New exercise
		menuFile.add( action = new NewAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Open exercise
		menuFile.add( action = new OpenAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuFile.addSeparator();
		// Save exercise
		menuFile.add( action = new SaveAction( this, false ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Save as ...
		menuFile.add( action = new SaveAction( this, true ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Save exercise as image
		menuFile.add( action = new SaveImageAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuFile.addSeparator();
		// Print as image
		menuFile.add( action = new PrintAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuFile.addSeparator();
		// Exit
		menuFile.add( action = new ExitAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Undo/Redo
		menuEdit.add( action = new UndoAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuEdit.add( action = new RedoAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuEdit.addSeparator();
		// Rectangular selection
		menuEdit.add( action = new SelectionAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Invert selection
		menuEdit.add( action = new SelectAllAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Invert selection
		menuEdit.add( action = new InvertSelectionAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuEdit.addSeparator();
		// Up shape
		menuEdit.add( action = new MoveZBufferAction( this, true ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Down shape
		menuEdit.add( action = new MoveZBufferAction( this, false ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuEdit.addSeparator();
		// Copy
		menuEdit.add( action = new CopySelectedAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Delete
		menuEdit.add( action = new DeleteSelectedAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Zoom all
		menuView.add( action = new ZoomAllAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Zoom in
		menuView.add( action = new ZoomInOutAction( this, true ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Zoom out
		menuView.add( action = new ZoomInOutAction( this, false ) );
		actionMap.put( action.getValue( Action.NAME ), action );		
		menuView.addSeparator();
		// Visible names
		menuView.add( action = new TextVisibleAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuView.addSeparator();
		// See/Hide toolbars
		List<AbstractButton> buttonList = new LinkedList<>();
		JCheckBoxMenuItem checkMenuItemStyleBar = new JCheckBoxMenuItem( 
					getLocaleText( "item_style" ), styleToolbar.isVisible() );
		checkMenuItemStyleBar.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				styleToolbar.setVisible( checkMenuItemStyleBar.isSelected() );
			}
		});
		menuView.add( checkMenuItemStyleBar );
		buttonList.add( checkMenuItemStyleBar );
		JCheckBoxMenuItem checkMenuItemRulerProtBar = new JCheckBoxMenuItem( 
			getLocaleText( "item_ruler_prot" ), rulerProtToolbar.isVisible() );
		checkMenuItemRulerProtBar.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				rulerProtToolbar.setVisible( 
									checkMenuItemRulerProtBar.isSelected() );
			}
		});
		menuView.add( checkMenuItemRulerProtBar );
		buttonList.add( checkMenuItemRulerProtBar );
		JCheckBoxMenuItem checkMenuItemStatusBar = new JCheckBoxMenuItem( 
					getLocaleText( "item_status" ), statusPanel.isVisible() );
		checkMenuItemStatusBar.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				statusPanel.setVisible( checkMenuItemStatusBar.isSelected() );
			}
		});
		menuView.add( checkMenuItemStatusBar );
		buttonList.add( checkMenuItemStatusBar );
		JCheckBoxMenuItem checkMenuItemActionBar = new JCheckBoxMenuItem( 
						getLocaleText( "item_action" ), actionbar.isVisible() );
		checkMenuItemActionBar.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				actionbar.setVisible( checkMenuItemActionBar.isSelected() );
			}
		});
		menuView.add( checkMenuItemActionBar );
		buttonList.add( checkMenuItemActionBar );
		JCheckBoxMenuItem checkMenuItemShapebar = new JCheckBoxMenuItem( 
						getLocaleText( "item_shape" ), shapebar.isVisible() );
		checkMenuItemShapebar.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				shapebar.setVisible( checkMenuItemShapebar.isSelected() );
			}
		});
		menuView.add( checkMenuItemShapebar );
		buttonList.add( checkMenuItemShapebar );
		JCheckBoxMenuItem checkMenuItemToolbar = new JCheckBoxMenuItem( 
							getLocaleText( "item_tool" ), toolbar.isVisible() );
		checkMenuItemToolbar.addActionListener( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				toolbar.setVisible( checkMenuItemToolbar.isSelected() );
			}
		});
		menuView.add( checkMenuItemToolbar );
		buttonList.add( checkMenuItemToolbar );
		// Show/Hide all
		menuView.add( new AbstractAction() {
			{
				putValue( Action.NAME, getLocaleText( "hide_all" ) );
				putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_E, InputEvent.SHIFT_MASK ) );
			}
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for ( AbstractButton b : buttonList )
					if ( b.isSelected() )
					{
						b.doClick();
						b.setSelected( false );
					}
			}
		});
		menuView.add( new AbstractAction() {
			{
				putValue( Action.NAME, getLocaleText( "show_all" ) );
				putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_S, InputEvent.SHIFT_MASK ) );
			}
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for ( AbstractButton b : buttonList )
					if ( !b.isSelected() )
					{
						b.doClick();
						b.setSelected( true );
					}
			}
		});		
		// Point shape
		menuShapes.add( action = new PointAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Segment shape
		menuShapes.add( action = new SegmentAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Arc shape
		menuShapes.add( action = new ArcAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Circumference shape
		menuShapes.add( action = new CircumferenceAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Angle shape
		menuShapes.add( action = new AngleAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Arrow shape
		menuShapes.add( action = new ArrowAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuShapes.addSeparator();
		// Triangle notable points menu
		menuShapes.add(	menuTrianglePoints = 
							new JMenu( getLocaleText( "triangle_tools" ) ) );
		menuTrianglePoints.setMnemonic( getLocaleMnemonic( "mne_menu_trian" ) );
		menuTrianglePoints.setIcon( getSmallIcon( "triangle_popup.png" ) );
		// Triangle shape
		menuTrianglePoints.add( action = new TriangleAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTrianglePoints.addSeparator();
		// Triangle notable points
		menuTrianglePoints.add( action = new TrianglePointsAction( 
								this, TrianglePointsListener.BARICENTER ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTrianglePoints.add( action = new TrianglePointsAction( 
								this, TrianglePointsListener.CIRCUMCENTER ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTrianglePoints.add( action = new TrianglePointsAction( 
								this, TrianglePointsListener.INCENTER ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTrianglePoints.add( action = new TrianglePointsAction( 
								this, TrianglePointsListener.ORTOCENTER ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Rectangle shape
		menuShapes.add( action = new RectangleAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Polygon menu
		menuShapes.add( 
				menuPolygon = new JMenu( getLocaleText( "polygon_tools" ) ) );
		menuPolygon.setMnemonic( getLocaleMnemonic( "mne_menu_poly" ) );
		menuPolygon.setIcon( getSmallIcon( "polygon_popup.png" ) );
		// Regular polygon shape
		menuPolygon.add( action = new RegularPolygonAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Polygon shape
		menuPolygon.add( action = new PolygonAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Polyline shape
		menuPolygon.add( action = new PolyLineAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Conics menu
		menuShapes.add( menuConics = new JMenu( getLocaleText( "conics" ) ) );
		menuConics.setMnemonic( getLocaleMnemonic( "mne_menu_conics" ) );
		menuConics.setIcon( getSmallIcon( "conics.png" ) );
		// Circumference shape (repeated)
		menuConics.add( new CircumferenceAction( this ) );
		// Ellipse shape
		menuConics.add( action = new EllipseAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Parabola shape
		menuConics.add( action = new ParabolaAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Hyperbola shape
		menuConics.add( action = new HyperbolaAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuShapes.addSeparator();
		// Spline shape
		menuShapes.add( action = new SplineAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Free hand shape
		menuShapes.add( action = new FreeHandAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Function shape
		if ( jmeEnabled )
		{
			menuShapes.add( action = new MathFunctionAction( this ) );
			actionMap.put( action.getValue( Action.NAME ), action );
		}
		// Translation transform
		menuTransform.add( action = new TranslationAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Rotation transform
		menuTransform.add( action = new RotationAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Homothety transform
		menuTransform.add( action = new HomothetyAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Central symmetry transform
		menuTransform.add( action = new CentralSymmetryAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Axial symmetry transform
		menuTransform.add( action = new AxialSymmetryAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Perpendicular tool
		menuTools.add( action = new PerpendicularAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Parallel tool
		menuTools.add( action = new ParallelAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTools.addSeparator();
		// Mediatrix tool
		menuTools.add( action = new MediatrixAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Bisectrix tool
		menuTools.add( action = new BisectrixAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Capable arc tool
		menuTools.add( action = new CapableArcAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTools.addSeparator();
		// Modify segment tool
		menuTools.add( action = new ModifySegmentAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTools.addSeparator();
		// Midpoint tool
		menuTools.add( action = new MidpointAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Vertex tool
		menuTools.add( action = new VertexAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Extremes tool
		menuTools.add( action = new ExtremesAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Divisions points tool
		menuTools.add( action = new DivisionPointsAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Intersections tool
		menuTools.add( action = new IntersectionsAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Rectangle bounds tool
		menuTools.add( action = new BoundsAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		menuTools.addSeparator();
		// Fragment shape tool
		menuTools.add( action = new FragmentAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Fusion shape tool
		menuTools.add( action = new FusionAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Select shape color
		menuStyle.add( action = new ShapeColorAction( this ) );
		buttonColor.setAction( action );
		buttonColor.setIcon( null );
		buttonColor.setText( "" );
		buttonColor.setContentAreaFilled( false );
		buttonColor.setBorderPainted( false );
		buttonColor.setFocusPainted( true );
		buttonColor.setOpaque( true );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Select point color
		menuStyle.add( action = new PointColorAction( this ) );
		buttonPointColor.setAction( action );
		buttonPointColor.setIcon( null );
		buttonPointColor.setText( "" );
		buttonPointColor.setContentAreaFilled( false );
		buttonPointColor.setBorderPainted( false );
		buttonPointColor.setFocusPainted( true );
		buttonPointColor.setOpaque( true );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Canvas background color
		menuStyle.add( action = new CanvasColorAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Eyedropper
		buttonEyedropper.setAction( action = new EyedropperAction( this ) );
		buttonEyedropper.setText( "" );
		menuStyle.addSeparator();
		menuStyle.add( action );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Paste style
		buttonPasteStyle.setAction( action = new PasteStyleAction( this ) );
		buttonPasteStyle.setText( "" );
		menuStyle.add( action );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Ruler
		buttonRuler.setAction( action = new RulerAction( this ) );
		//buttonRuler.setPreferredSize( new Dimension( 36, 30 ) );
		buttonRuler.setContentAreaFilled( true );
		buttonRuler.setBorderPainted( false );
		buttonRuler.setFocusPainted( true );
		buttonRuler.setOpaque( false );
		buttonRuler.setText( "" );
		menuTools.addSeparator();
		menuTools.add( action );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Protractor
		buttonProtactor.setAction( action = new ProtractorAction( this ) );
		//buttonProtactor.setPreferredSize( new Dimension( 48, 30 ) );
		buttonProtactor.setContentAreaFilled( true );
		buttonProtactor.setBorderPainted( false );
		buttonProtactor.setFocusPainted( true );
		buttonProtactor.setOpaque( false );
		buttonProtactor.setText( "" );
		menuTools.add( action );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Rewind exercise
		menuExercise.add( action = new RewindAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Backward exercise
		menuExercise.add( action = new BackwardAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Forward exercise
		menuExercise.add( action = new ForwardAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// End exercise
		menuExercise.add( action = new EndAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Exercise metadata
		menuExercise.addSeparator();
		menuExercise.add( action = new ExerciseMetadataAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		// Look & Feel
		action = new LookFeelAction( this, menuAppearance );
		actionMap.put( action.getValue( Action.NAME ), action );
		LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
		ButtonGroup group = new ButtonGroup();
		for ( LookAndFeelInfo lafi : laf )
		{
			JRadioButtonMenuItem radioItem = new JRadioButtonMenuItem();
			radioItem.setAction( action );
			radioItem.setText( lafi.getName() );
			menuAppearance.add( radioItem );
			group.add( radioItem );
			if ( lookAndFeelClassName.equals( lafi.getClassName() ) )
				radioItem.setSelected( true );
		}
		// Project page
		menuHelp.add( new AbstractAction() {
			private java.net.URI uri; 
			
			{
				try
				{ 
					uri = new URI( PROJECT_PAGE );
					putValue( Action.NAME,
						"<html>Project page: " + "<a href=" + uri.toString()
						+ ">" + uri.toString() + "</a></html>" );
					putValue( Action.SMALL_ICON, 
							  getSmallIcon( "homepage.png" ) );
				}
				catch ( URISyntaxException e ) { System.err.println( e ); }
			}
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if ( Desktop.isDesktopSupported() 
					 && 
					 Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) )
					try { Desktop.getDesktop().browse( uri ); }
					catch ( IOException e ) { System.err.println( e ); }
			}
		} );
		// Github Repository
		menuHelp.add( new AbstractAction() {
			private java.net.URI uri; 
			
			{
				try
				{ 
					uri = new URI( GITHUB_REPOSITORY );
					putValue( Action.NAME,
						"<html>Github repository: " + "<a href=" 
						+ uri.toString() + ">"
						+ uri.toString() + "</a></html>" );
					putValue( Action.SMALL_ICON, 
							  getSmallIcon( "github.png" ) );
				}
				catch ( URISyntaxException e ) { System.err.println( e ); }
			}
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if ( Desktop.isDesktopSupported() 
					 && 
					 Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) )
					try { Desktop.getDesktop().browse( uri ); }
					catch ( IOException e ) { System.err.println( e ); }
			}
		} );
		menuHelp.addSeparator();
		// About...
		menuHelp.add( action = new AboutAction( this ) );
		actionMap.put( action.getValue( Action.NAME ), action );
		
		// --- TOOLBARS ACTIONS
		// actionbar
		actionbar.add( actionMap.get( getLocaleText( "new" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "open" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "save" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "save_image" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "print" ) ) );
		actionbar.addSeparator( HSEP );
		actionbar.add( actionMap.get( getLocaleText( "fileinfo" ) ) );
		actionbar.addSeparator( HSEP );
		actionbar.add( actionMap.get( getLocaleText( "undo" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "redo" ) ) );
		actionbar.addSeparator( HSEP );
		actionbar.add( actionMap.get( getLocaleText( "move_up" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "move_down" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "copy" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "delete" ) ) );
		actionbar.addSeparator( HSEP );
		actionbar.add( actionMap.get( getLocaleText( "color" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "point_color" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "background_color" ) ) );
		actionbar.addSeparator( HSEP );
		actionbar.add( actionMap.get( getLocaleText( "zoom_all" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "zoom_in" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "zoom_out" ) ) );
		actionbar.addSeparator( HSEP );
		JButton button;
		Color bColor = new Color( 44, 33, 22 );
		button = actionbar.add( actionMap.get( getLocaleText( "rewind" ) ) );
		button.setBackground( bColor );
		actionbar.add( Box.createHorizontalStrut( 2 ) );		
		button = actionbar.add( actionMap.get( getLocaleText( "backward" ) ) );
		button.setBackground( bColor );
		actionbar.add( Box.createHorizontalStrut( 2 ) );		
		button = actionbar.add( actionMap.get( getLocaleText( "forward" ) ) );
		button.setBackground( bColor );
		actionbar.add( Box.createHorizontalStrut( 2 ) );		
		button = actionbar.add( actionMap.get( getLocaleText( "end" ) ) );
		button.setBackground( bColor );
		actionbar.add( Box.createHorizontalStrut( 2 ) );		
		actionbar.addSeparator( HSEP );
		actionbar.add( toggleNames =
				new JToggleButton( actionMap.get( getLocaleText( "text" ) ) ) );
		toggleNames.setText( null );
		toggleNames.setSelected( isVisibleNames() );
		actionbar.addSeparator();
		actionbar.add( actionMap.get( getLocaleText( "select_all" ) ) );
		actionbar.add( actionMap.get( getLocaleText( "invert" ) ) );
		/*actionbar.addSeparator();
		actionbar.add( actionMap.get( getLocaleText( "exit" ) ) );*/
		// shapebar
		shapebar.add( actionMap.get( getLocaleText( "selection" ) ) );
		shapebar.addSeparator( VSEP );
		shapebar.add( actionMap.get( getLocaleText( "point" ) ) );
		shapebar.add( actionMap.get( getLocaleText( "segment" ) ) );
		shapebar.add( actionMap.get( getLocaleText( "arc" ) ) );
		shapebar.add( actionMap.get( getLocaleText( "circumference" ) ) );
		shapebar.add( actionMap.get( getLocaleText( "angle" ) ) );
		shapebar.add( actionMap.get( getLocaleText( "arrow" ) ) );
		shapebar.addSeparator( VSEP );
		JButton trianglePopup = new JButton( new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Component src = (Component) e.getSource();
				JPopupMenu popup = new JPopupMenu();
				popup.add( actionMap.get( getLocaleText( "triangle" ) ) );
				popup.addSeparator();
				popup.add( actionMap.get( getLocaleText( "baricenter" ) ) );
				popup.add( actionMap.get( getLocaleText( "ortocenter" ) ) );
				popup.add( actionMap.get( getLocaleText( "circumcenter" ) ) );
				popup.add( actionMap.get( getLocaleText( "incenter" ) ) );
				for ( Component c : popup.getComponents() )
					if ( c instanceof JMenuItem )
						( (JMenuItem) c ).setIcon( (Icon) ( (JMenuItem) c )
							.getAction().getValue( Action.LARGE_ICON_KEY ) );
				popup.show( src, (int) src.getBounds().getWidth() - 4, -2 );
			}
		});
		trianglePopup.setToolTipText( getLocaleText( "triangle_tools" ) );
		trianglePopup.setIcon( getLargeIcon( "triangle_popup.png" ) );
		shapebar.add( trianglePopup );
		shapebar.add( actionMap.get( getLocaleText( "rectangle" ) ) );
		JButton polygonPopup = new JButton( new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Component src = (Component) e.getSource();
				JPopupMenu popup = new JPopupMenu();
				popup.add( actionMap.get( getLocaleText( "reg_poly" ) ) );
				popup.add( actionMap.get( getLocaleText( "polygon" ) ) );
				popup.add( actionMap.get( getLocaleText( "polyline" ) ) );
				for ( Component c : popup.getComponents() )
					if ( c instanceof JMenuItem )
						( (JMenuItem) c ).setIcon( (Icon) ( (JMenuItem) c )
							.getAction().getValue( Action.LARGE_ICON_KEY ) );
				popup.show( src, (int) src.getBounds().getWidth() - 4, -2 );
			}
		});
		polygonPopup.setToolTipText( getLocaleText( "polygon_tools" ) );
		polygonPopup.setIcon( getLargeIcon( "polygon_popup.png" ) );
		shapebar.add( polygonPopup );
		JButton conicsPopup = new JButton( new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Component src = (Component) e.getSource();
				JPopupMenu popup = new JPopupMenu();
				popup.add( new CircumferenceAction( Application.this ) );
				popup.add( actionMap.get( getLocaleText( "ellipse" ) ) );
				popup.add( actionMap.get( getLocaleText( "parabola" ) ) );
				popup.add( actionMap.get( getLocaleText( "hyperbola" ) ) );
				for ( Component c : popup.getComponents() )
					if ( c instanceof JMenuItem )
						( (JMenuItem) c ).setIcon( (Icon) ( (JMenuItem) c )
							.getAction().getValue( Action.LARGE_ICON_KEY ) );
				popup.show( src, (int) src.getBounds().getWidth() - 4, -2 );
			}
		});
		conicsPopup.setToolTipText( getLocaleText( "conics" ) );
		conicsPopup.setIcon( getLargeIcon( "conics.png" ) );
		shapebar.add( conicsPopup );
		shapebar.addSeparator( VSEP );
		shapebar.add( actionMap.get( getLocaleText( "spline" ) ) );
		shapebar.add( actionMap.get( getLocaleText( "free_hand" ) ) );
		if ( jmeEnabled )
			shapebar.add( actionMap.get( getLocaleText( "func" ) ) );
		// toolbar
		toolbar.add( actionMap.get(	getLocaleText( "perp" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "para" ) ) );
		toolbar.addSeparator( HSEP );
		toolbar.add( actionMap.get(	getLocaleText( "mediatrix" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "bisectrix" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "capable_arc" ) ) );
		toolbar.addSeparator( HSEP );
		toolbar.add( actionMap.get(	getLocaleText( "modify" ) ) );
		toolbar.addSeparator( HSEP );
		toolbar.add( actionMap.get(	getLocaleText( "midpoint" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "vertex" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "extremes" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "divisions" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "inter" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "bounds" ) ) );
		toolbar.addSeparator( HSEP );
		toolbar.add( actionMap.get(	getLocaleText( "fragment" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "fusion" ) ) );
		toolbar.addSeparator( HSEP );
		toolbar.add( actionMap.get(	getLocaleText( "translation" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "rotation" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "homothety" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "central_sym" ) ) );
		toolbar.add( actionMap.get(	getLocaleText( "axial_sym" ) ) );
	}

	/**
	 * Set automatic title bar text
	 */
	public void setAppTitle()
	{
		String title = APPNAME + "   v" + VERSION;
		if ( getExercise() != null && getExercise().getTitle().length() > 0 )
			title += " - " + getExercise().getTitle();
		if ( saveFilename != null && saveFilename.length() > 0 )
			title += " (" + saveFilename + ")";
		else
			title += " (" + getLocaleText( "not_saved" ) + ")";
		if ( undoManager != null && undoManager.canUndo() )
			title += "*";
		
		setTitle( title );
	}
	
	
	/////////////////////
	// GETTERS/SETTERS //
	/////////////////////
	
	// canvas
	public CanvasPanel getCanvas() { return canvas; }

	// shape color
	public Color getColor() { return color; }
	public void setColor( Color color )
	{ 
		this.color = color;
		buttonColor.repaint();
		comboLineStyle.repaint();
	}
	
	// point color
	public Color getPointColor() { return pointColor; }
	public void setPointColor( Color pointColor )
	{	
		this.pointColor = pointColor;
		buttonPointColor.repaint();
	}
	
	// canvas back color
	public Color getBackColor() { return getExercise().getBackgroundColor(); }
	public void setBackColor( Color backColor )
	{
		getExercise().setBackgroundColor( backColor );
		canvas.setBackground( backColor );
	}

	// shape stroke	
	public BasicStroke getStroke() { return stroke; }
	public void setStroke( BasicStroke stroke )
	{ 
		this.stroke = stroke;
		spinThickness.getModel().setValue( (double) stroke.getLineWidth() );
		comboLineStyle.repaint();
	}
	
	// point stroke
	public BasicStroke getPointStroke() { return pointStroke; }
	public void setPointStroke( BasicStroke pointStroke )
	{
		this.pointStroke = pointStroke;
		spinPointThickness.getModel().setValue( 
										(double) pointStroke.getLineWidth() );
	}
	
	// ruler distance
	public double getDistance() { return distance; }
	public void setDistance( double distance )
	{
		this.distance = distance > 0. ? distance : Math.ulp( 0. );
	}
	
	// flag for distance using
	public boolean isUsingRuler() { return useDistance; }
	public void setUseDistance( boolean useDistance )
	{
		this.useDistance = useDistance;
		canvas.repaint();
	}
	
	// angle	
	public double getAngle() { return angle; }
	public void setAngle( double angle )
	{
		this.angle = angle;
		spinAngle.getModel().setValue( angle );
	}
	
	// action map
	public ActionMap getActionMap() { return actionMap; }
	
	// exercise
	@NotNull
	public Exercise getExercise() { return exercise; }	
	public void setExercise(@NotNull Exercise exercise, String filename )
	{ 
		this.exercise = exercise;
		exercise.setFrameAtEnd();
		setSelectedShapes( new HashSet<>() );
		canvas.setViewport( exercise.isEmpty() 
							? new Viewport()
							: new Viewport( exercise.getBounds() ) );
		canvas.setBackground( getExercise().getBackgroundColor() );
		setSaveFilename( filename );
		setAppTitle();
		
		// start new undo/redo system		
		initializeUndoRedoSystem();
		
		// delete old shapelist and reset
		shapeList.getModel().removeAllElements();
		for ( JDraftingShape jdshape : getExercise().getShapes() )
			shapeList.getModel().addElement( jdshape );

		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	public void setExercise(@NotNull Exercise exercise )
	{
		setExercise( exercise, null );
	}

	// selected shapes
	public Set<JDraftingShape> getSelectedShapes() { return selectedShapes; }
	public void setSelectedShapes( Set<JDraftingShape> selected )
	{
		selectedShapes = selected;
	}
	
	// filename
	public String getSaveFilename() { return saveFilename; }
	public void setSaveFilename( String saveFilename )
	{
		this.saveFilename = saveFilename;
		setAppTitle();
	}
	
	// flag to show shape names
	public boolean isVisibleNames() { return visibleNames; }
	public void setVisibleNames( boolean visibleNames )
	{
		this.visibleNames = visibleNames;
		toggleNames.setSelected( visibleNames );
	}
	
	// flatness value to convert curved shapes
	public double getFlatnessValue() { return flatnessValue; }
	public void setFlatnessValue( double flat ) { flatnessValue = flat; } 
	
	// status bar text
	/**
	 * Set text and tooltip on status bar
	 * @param text status message
	 */
	public void setStatusText( String text )
	{
		labelStatus.setText( text );
		labelStatus.setToolTipText( text );
	}
	
	
	/////////////
	// HELPERS //
	/////////////
	
	public void openFile( File file )
	{
		// load exercise
		try ( FileInputStream is = new FileInputStream( file ) )
		{
			ObjectInputStream ois = new ObjectInputStream( is );
			setExercise( (Exercise) ois.readObject(), file.getAbsolutePath() );
		}
		catch ( IOException | ClassNotFoundException ex )
		{
			JOptionPane.showMessageDialog( this, 
				"<html>Can't open " + file.getName() 
				+ ":<br/><font color='red'>" + ex + "</font></html>", 
				"Error while open " + file.getAbsolutePath(), 
				JOptionPane.ERROR_MESSAGE );
		}
	}
	
	/**
	 * Adds to exercise a Path2D representation of the shape with the
	 * specified parameters.
	 * @param pit shape path iterator
	 * @param name shape identifier
	 * @param description shape comment
	 * @param color color of the shape
	 * @param stroke stroke of the shape
	 * @param transaction transaction to add the shape for undo/redo system
	 * without launch UndoSupport listeners. 
	 * A null value add the new UndoableEdit to UndoSupport main object, and 
	 * launch listeners 
	 * @return the inserted new shape
	 */
	public JDraftingShape addShapeFromIterator( PathIterator pit, String name,
		String description, Color color, BasicStroke stroke,
		CompoundEdit transaction )
	{
		// create shape from iterator
		JDraftingShape jdshape = JDraftingShape.createFromIterator(
										pit, name, description, color, stroke );
		
		// add new JDrafting shape
		int index = getExercise().addShape( jdshape );
		shapeList.getModel().add( index, jdshape );
		
		// undo/redo system
		UndoableEdit addEdit = new EditAddShapeToExercise( jdshape, index );
		if ( transaction == null )
			undoSupport.postEdit( addEdit );
		else
			transaction.addEdit( addEdit );

		// return new JDrafting shape from iterator
		return jdshape;
	}

	/**
	 * See {@link #addShapeFromIterator(PathIterator, String, String, Color, 
	 * BasicStroke, CompoundEdit)}
	 */
	public JDraftingShape addShapeFromIterator( PathIterator pit, String name,
			String description, Color color, BasicStroke stroke )
	{
		return 
			addShapeFromIterator( pit, name, description, color, stroke, null );
	}
		
	/**
	 * 
	 * @param jdshape
	 */
	public int removeShape( JDraftingShape jdshape, CompoundEdit transaction )
	{
		// remove shape
		int index = getExercise().removeShape( jdshape );
		shapeList.getModel().removeElement( jdshape );
		
		// undo/redo system
		UndoableEdit remove = new EditRemoveShapeFromExercise( jdshape, index );
		if ( transaction == null )
			undoSupport.postEdit( remove );
		else
			transaction.addEdit( remove );
		
		return index;
	}
	
	/**
	 * See {@link #removeShape(JDraftingShape, CompoundEdit)} 
	 */
	public int removeShape( JDraftingShape jdshape )
	{
		return removeShape( jdshape, null );
	}

	/**
	 * Exit application
	 */
	public void exit()
	{
		// check exit dialog
		if ( undoManager.canUndo() )
		{
			int option = JOptionPane.showConfirmDialog( this,
									getLocaleText( "exit_msg" ), 
									getLocaleText( "exit_dlg" ) + " " + APPNAME, 
									JOptionPane.YES_NO_OPTION );
			if ( option != JOptionPane.YES_OPTION )  return;  // cancel exit
		}
		System.out.println( APPNAME + " exited." );
		
		// stop canvas movement thread
		canvas.getMovementThread().stopMe();
		try { canvas.getMovementThread().join(); }
		catch ( InterruptedException e ) {}

		// close app
		dispose();
		//System.exit( 0 );
	}

	
	//////////////////////
	// UNDO/REDO SYSTEM //
	//////////////////////

	/**
	 * Initializes undo manager and support
	 */
	public void initializeUndoRedoSystem()
	{
		undoManager = new UndoManager();
		undoManager.setLimit( 100 );
		undoSupport = new UndoableEditSupport();
		undoSupport.addUndoableEditListener( new UndoRedoListener() ); 
		refreshUndoRedo();
	}

	public class UndoRedoListener implements UndoableEditListener
	{
		@Override
		public void undoableEditHappened( UndoableEditEvent e )
		{
			undoManager.addEdit( e.getEdit() );
			refreshUndoRedo();
		}		
	}
	
	/**
	 * Refresh action Undo/Redo
	 */
	public void refreshUndoRedo()
	{
		Action action;
		action = actionMap.get( getLocaleText( "undo" ) );
		action.setEnabled( undoManager.canUndo() );
		action.putValue( Action.SHORT_DESCRIPTION, 
						 undoManager.getUndoPresentationName() );
		action = actionMap.get( getLocaleText( "redo" ) );
		action.setEnabled( undoManager.canRedo() );
		action.putValue( Action.SHORT_DESCRIPTION,
						 undoManager.getRedoPresentationName() );

		setAppTitle();
		/*getActionMap().get( getLocaleText( "save" ) )
			.setEnabled( undoManager.canUndo() );*/
	}
	
	/**
	 * UndoableEdit for add shapes to exercise 
	 */
	public class EditAddShapeToExercise extends AbstractUndoableEdit
	{
		private JDraftingShape jdshape;
		private int index;
		
		public EditAddShapeToExercise( @NotNull JDraftingShape jdshape, int index )
		{
			this.jdshape = jdshape;
			this.index = index;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			getExercise().removeShape( index );			
			shapeList.getModel().remove( index );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			getExercise().addShape( index, jdshape );			
			shapeList.getModel().add( index, jdshape );
		}
		
		@Override
		public boolean canRedo() { return true; }
		
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getPresentationName()
		{
			return "Add " + jdshape.toShortString();
		}
	}

	/**
	 * UndoableEdit for remove individual shapes from exercise 
	 */
	public  class EditRemoveShapeFromExercise extends AbstractUndoableEdit
	{
		private JDraftingShape jdshape;
		private int index;
		
		public EditRemoveShapeFromExercise(@NotNull JDraftingShape jdsShape, 
										   int index )
		{
			this.jdshape = jdsShape;
			this.index = index;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			getExercise().addShape( index, jdshape );
			shapeList.getModel().add( index, jdshape );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			getExercise().removeShape( index );			
			shapeList.getModel().remove( index );
		}
		
		@Override
		public boolean canRedo() { return true; }
		
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getPresentationName()
		{
			return "Remove " + jdshape.toShortString();
		}
	}


	//////////////////
	// GUI LAUNCHER //
	//////////////////	

	/**
	 * Launch GUI
	 * @param args %1 filename to load
	 */
	public static void main( String[] args )
	{
		SwingUtilities.invokeLater( () -> {
			try
			{
				// parse and execute parameters before app instantiation
				// (some parameters like lang, lookfeel need to be executed 
				// before app intantiation)
				JDraftingArgs argsParser = new JDraftingArgs( null );
				ParsedParameterMap parsedMap = argsParser.parseAndExecute( args );

				// application instance
				Application app = new Application();
	
				// parse and execute parameters after app instantiation
				argsParser.setApp( app );
				argsParser.execute( parsedMap );

				// launch app
				app.setVisible( true );
			
				// load file from console
				// (file load must be executed with a visible app)
				if ( parsedMap.containsParam( "file" ) )
					app.openFile( new File( parsedMap.getValues( "file" )[0] ) );
			}
			catch ( NoSuchElementException e )
			{
				System.out.printf( 
						"Launch error (%s: %s)\n-help for parameter info", 
						e.getClass().getSimpleName(), e.getMessage() );
				System.exit( -1 );
			}
		} );
	}
}
