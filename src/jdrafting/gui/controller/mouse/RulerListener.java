package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.geom.JDStrokes;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Capture a distance by mouse control 
 */
public class RulerListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "ruler_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D start;
	
	public RulerListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_ruler1" ) );
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved( e );
		
		if ( start != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_ruler2" ) );
		}
		else
		{
			// capture distance with ruler
			double distance = start.distance( logicMouse );
			if ( add() )
				distance += app.getDistance();
			else if ( sub() )
				distance = Math.max( app.getDistance() - distance, Math.ulp( 0 ) );			
			app.setDistance( distance );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );			
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			// mouse position in logic viewport
			final Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// draw line
			g2.setStroke( JDStrokes.getStroke( JDStrokes.DASHED.getStroke(), 1f ) );
			g2.setColor( Application.toolMainColor );	
			g2.draw( canvas.getTransform().createTransformedShape( 
														 new Line2D.Double( start, logicMouse ) ) );
		}
	}
	
	// --- HELPERS
	
	// check modifiers
	private boolean add() { return mouse().isShiftDown(); }
	private boolean sub() { return mouse().isControlDown(); }
}
