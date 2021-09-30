package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

import static jdrafting.gui.JDUtils.getLocaleText;

/**
 * Creates a polygon or polyline using mouse control
 */
public class PolygonListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR_POLYGON = JDUtils.getCustomCursor( "polygon_cursor.png" ); 
	private static final Cursor CURSOR_POLYLINE = JDUtils.getCustomCursor( "polyline_cursor.png" ); 
	private final Cursor CURSOR;
	private CanvasPanel canvas;
	private Application app;

	private Path2D polygon = new Path2D.Double();
	private Point2D start;
	private boolean closed;
	
	public PolygonListener( CanvasPanel canvas, boolean closed )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		this.closed = closed;
		
		CURSOR = closed ? CURSOR_POLYGON : CURSOR_POLYLINE;
		canvas.setCursor( CURSOR );
		
		app.setStatusText( JDUtils.getLocaleText( "txt_poly" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		if ( start != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// finish polygon capture
		if ( e.getClickCount() == 2 )
		{
			// add start point to end point (needed for some operations)
			if ( closed ) 
				polygon.lineTo( start.getX(), start.getY() );
				//polygon.closePath();  // "Fragment" would ignore last side

			// add polygon to exercise
			app.addShapeFromIterator( polygon.getPathIterator( null ), "", 
									  getLocaleText( closed ? "new_polygon" : "new_polyline" ), 
									  app.getColor(), null, app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
		// add new point to polygon
		else
		{
			if ( polygon.getCurrentPoint() != null )
				polygon.lineTo( logicMouse.getX(), logicMouse.getY() );
			else  // first point
			{
				polygon.moveTo( logicMouse.getX(), logicMouse.getY() );
				start = logicMouse;
			}
		}		
		
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		AffineTransform transform = canvas.getTransform();

		// mouse position on logic viewport
		Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

		g2.setStroke( new BasicStroke( 1f ) );
		g2.setColor( Application.toolMainColor );
		
		g2.draw( transform.createTransformedShape( polygon ) );
		
		PathIterator path = polygon.getPathIterator( null );
		double[] coords = new double[6];
		
		int sides = 0;
		while ( !path.isDone() )
		{
			sides++;
			path.currentSegment( coords );
			path.next();
		}
		
		if ( sides > 0 )
			g2.draw( transform.createTransformedShape( new Line2D.Double( 
									coords[0], coords[1], 
									logicMouse.getX(), logicMouse.getY() ) ) );
	}
}
