package jdrafting.gui.controller.actions;

import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;

import javax.swing.AbstractAction;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.Viewport;

@SuppressWarnings("serial")
public class SVGAction extends AbstractAction 
{
	private Application app;
	
	public SVGAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, "svg" );
		/*putValue( SHORT_DESCRIPTION, getLocaleText( "angle_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_N );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_5, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "angle.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "angle.png" ) );*/
	}
		
	@Override
	public void actionPerformed( ActionEvent e )
	{
	    // Get a DOMImplementation.
	    DOMImplementation domImpl =
	      GenericDOMImplementation.getDOMImplementation();

	    // Create an instance of org.w3c.dom.Document.
	    String svgNS = "http://www.w3.org/2000/svg";
	    Document document = domImpl.createDocument(svgNS, "svg", null);

	    // Create an instance of the SVG Generator.
	    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

	    // Ask the test to render into the SVG Graphics2D implementation.
	    Rectangle2D start = app.getExercise().getBounds();
	    Rectangle2D end = new Rectangle2D.Double( 0., 0., 1000., 1000. * start.getHeight() / start.getWidth() );
		Rectangle2D bounds = CanvasPanel.getExerciseBounds( app.getExercise(), 
								new Viewport( start ), new Viewport( end ) );
		CanvasPanel.drawExercise( svgGenerator,
			CanvasPanel.getTransform( 
				new Viewport( bounds ), new Viewport( end ) ),
			app.getExercise(), new HashSet<>(), app.isVisibleNames() );

	    // Finally, stream out SVG to the standard output using
	    // UTF-8 encoding.
	    boolean useCSS = true; // we want to use CSS style attributes
	    try
	    {
	    	Writer out = new OutputStreamWriter(System.out, "UTF-8");
	    	svgGenerator.stream(out, useCSS);
	    }
	    catch ( UnsupportedEncodingException | SVGGraphics2DIOException ex ) {}
	}
}
