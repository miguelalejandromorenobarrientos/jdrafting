package jdrafting.gui.controller.actions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.Viewport;

@SuppressWarnings("serial")
public class SaveImageAction extends AbstractAction
{
	private Application app;
	private CanvasPanel canvas;
	private static ImageChooser fileChooser;
	private static int counter = 1;
	private static final int MINI_SIZE = 120;
	
	public SaveImageAction( Application app )
	{
		this.app = app;
		canvas = app.getCanvas();
		
		putValue( NAME, Application.getLocaleText( "save_image" ) );
		putValue( SHORT_DESCRIPTION, 
					Application.getLocaleText( "save_image_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_I );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, Application.getSmallIcon( "save_image.png" ) );
		putValue( LARGE_ICON_KEY,
				Application.getLargeIcon( "save_image.png" ) );

		// create static file chooser dialog
		fileChooser = new ImageChooser();
	}
		
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getExercise().isEmpty() )
			return;
		
		// update file chooser L&F
		SwingUtilities.updateComponentTreeUI( fileChooser );
		
		// set png format as the unique filter
		Arrays.stream( fileChooser.getChoosableFileFilters() )
			  .forEach( f -> fileChooser.removeChoosableFileFilter( f ) );
		FileNameExtensionFilter svgFilter = 
							new FileNameExtensionFilter( "SVG images", "svg" );
		FileNameExtensionFilter pngFilter = 
							new FileNameExtensionFilter( "PNG images", "png" );
		fileChooser.addChoosableFileFilter( svgFilter );
		fileChooser.addChoosableFileFilter( pngFilter );

		// update file name
		fileChooser.setSelectedFile( new File( "jd_" + ( counter++ ) ) );

		fileChooser.textHeight.setText( 
				 				String.valueOf( getAutomaticHeight( 1000 ) ) );	
		fileChooser.labelImage.setIcon(	new ImageIcon( 
								createBImage( MINI_SIZE, MINI_SIZE, false ) ) );
		fileChooser.checkText.setSelected( app.isVisibleNames() );
		
		if ( fileChooser.showSaveDialog( app ) == JFileChooser.APPROVE_OPTION )
		{
			// get file selected and add filter extension
			File filename = fileChooser.getSelectedFile();
			String ext = "." 
				+ ( (FileNameExtensionFilter) fileChooser.getFileFilter() )
					.getExtensions()[0].toLowerCase();
			if ( !filename.getName().toLowerCase().endsWith( ext ) )
				filename = new File( filename.getAbsolutePath() + ext );
			
			// overwrite dialog
			if ( filename.exists() )
			{
				int option = JOptionPane.showConfirmDialog( 
									app,
									Application.getLocaleText( "overwrite1" ),
									Application.getLocaleText( "overwrite2" ) 
										+ " " + filename.getName(), 
									JOptionPane.YES_NO_OPTION );
				if ( option == JOptionPane.NO_OPTION )
					return;
			}
			
			// save image
			try
			{
				int width = Integer.parseInt( fileChooser.textWidth.getText() );
				int height = 
						Integer.parseInt( fileChooser.textHeight.getText() );

				// saving image as PNG
				if ( filename.getName().toLowerCase().endsWith( ".png" ) )
				{
					BufferedImage img = createBImage( width, height,
										fileChooser.checkText.isSelected() );				
					ImageIO.write( img, "png", filename );
				}
				// saving image as SVG
				else
				{
					toSVG( filename, width, height );
				}
			}
			catch ( Exception ex )
			{
				JOptionPane.showMessageDialog( app, ex, "Save error",
													JOptionPane.ERROR_MESSAGE );
			}
		}
	}
	
	private int getAutomaticHeight( int width )
	{
		Rectangle2D bound = app.getExercise().getBounds();
		
		return (int) Math.round( width * bound.getHeight() / bound.getWidth() );
	}
	
	/**
	 * Save SVG file using Apache Batik libraries 
	 * @param filename SVG file name
	 * @param width image width
	 * @param height image height
	 * @throws UnsupportedEncodingException
	 * @throws SVGGraphics2DIOException
	 */
	private void toSVG( File filename, int width, int height ) 
				throws UnsupportedEncodingException, SVGGraphics2DIOException
	{
	    // Get a DOMImplementation.
	    DOMImplementation domImpl =
	      GenericDOMImplementation.getDOMImplementation();

	    // Create an instance of org.w3c.dom.Document.
	    String svgNS = "http://www.w3.org/2000/svg";
	    Document document = domImpl.createDocument( svgNS, "svg", null );

	    // Create an instance of the SVG Generator.
	    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		// draw background in SVG generator
	    if ( fileChooser.checkBackground.isSelected() )
	    {
	    	svgGenerator.setColor( canvas.getBackground() );
			svgGenerator.fillRect( 0, 0, width, height );
	    }
	    
	    // draw exercise to SVG generator
		CanvasPanel.drawExercise( svgGenerator,
			CanvasPanel.getTransform( 
								new Viewport( app.getExercise().getBounds() ), 
								new Viewport( 0., width, 0., height ) ),
			app.getExercise(), new HashSet<>(), 
			fileChooser.checkText.isSelected() );

	    // Stream out SVG to output using UTF-8 encoding
	    boolean useCSS = true; // we want to use CSS style attributes
    	//Writer out = new OutputStreamWriter( System.out, "UTF-8" );
	    try ( PrintWriter out = 
	    			new PrintWriter( filename, "UTF-8" ) )
	    {
	    	svgGenerator.stream( out, useCSS );	    	
	    }
	    catch ( FileNotFoundException e ) {}
	}
	
	private BufferedImage createBImage( int width, int height, boolean text )
	{
		Rectangle2D bounds = CanvasPanel.getExerciseBounds( app.getExercise(), 
				new Viewport( app.getExercise().getBounds() ), 
				new Viewport( 0, width, 0, height ) );
		BufferedImage img = 
				new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		// draw background in image
		g2.setColor( fileChooser.checkBackground.isSelected()
					 ? canvas.getBackground()
					 : new Color( 0, 0, 0, 0 ) );
		g2.fillRect( 0, 0, img.getWidth(), img.getHeight() );
		
		// draw exercise in image context
		CanvasPanel.drawExercise( g2, CanvasPanel.getTransform(
				new Viewport( bounds ),
				new Viewport( 0, img.getWidth() - 1, 0, img.getHeight() - 1 ) ),
				app.getExercise(), new HashSet<>(), text );
		
		return img;
	}
	
	private class ImageChooser extends JFileChooser
	{
		private JTextField textWidth = new JTextField( "1000" );
		private JTextField textHeight = new JTextField();
		private JCheckBox checkBackground = new JCheckBox( 
						Application.getLocaleText( "save_image_acce2" ), true );
		private JCheckBox checkText = new JCheckBox( 
								Application.getLocaleText( "save_image_acce3" ),
								app.isVisibleNames() );
		private JLabel labelImage = new JLabel();
				
		private ImageChooser()
		{
			// custom accessory
			textWidth.setPreferredSize( new Dimension( 80, 30 ) );
			textWidth.getDocument()
				.addDocumentListener( new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e)
				{
					try
					{
						textHeight.setText( 
							String.valueOf( getAutomaticHeight( 
								Integer.parseInt( textWidth.getText() ) ) ) );
					}
					catch ( NumberFormatException ex ) {}
				}
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					insertUpdate( e );
				}
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					insertUpdate( e );
				}
			});
			textHeight.setPreferredSize( new Dimension( 80, 30 ) );
			JLabel labelWidth =
					new JLabel( Application.getLocaleText( "width" ) + ":" );
			JLabel labelHeight =
					new JLabel( Application.getLocaleText( "height" ) + ":" );
			checkBackground.addItemListener( new ItemListener() {
				@Override
				public void itemStateChanged( ItemEvent e )
				{
					labelImage.setIcon(	new ImageIcon( 
								createBImage( MINI_SIZE, MINI_SIZE, false ) ) );
				}
			});

			JPanel panel = new JPanel();
			panel.setBorder( BorderFactory.createTitledBorder( 
							Application.getLocaleText( "save_image_acce1" ) ) );
			
			GroupLayout layout = new GroupLayout( panel );
			panel.setLayout( layout );
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			layout.setHorizontalGroup( layout.createSequentialGroup()
				.addGroup( layout.createParallelGroup(
												GroupLayout.Alignment.CENTER )
					.addComponent( labelImage )
					.addGroup( layout.createSequentialGroup()
						.addComponent( labelWidth )
						.addComponent( textWidth ) )
					.addGroup( layout.createSequentialGroup()
						.addComponent( labelHeight )
						.addComponent( textHeight ) )
					.addComponent( checkBackground )
					.addComponent( checkText ) ) );
			layout.setVerticalGroup( layout.createSequentialGroup()
				.addComponent( labelImage )
				.addGroup( layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE )
					.addComponent( labelWidth )
					.addComponent( textWidth ) )
				.addGroup( layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE )
					.addComponent( labelHeight )
					.addComponent( textHeight ) )
				.addComponent( checkBackground )
				.addComponent( checkText ) );
			layout.linkSize( labelWidth, labelHeight );
			layout.linkSize( textWidth, textHeight );
			
			setAccessory( panel );
		}
	}
}
