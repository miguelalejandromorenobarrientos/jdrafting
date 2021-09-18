package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import jdrafting.geom.JDPoint;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;


public class PointListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "point_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public PointListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( JDUtils.getLocaleText( "txt_point" ) );
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		
		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// create point
		JDPoint point;
		
		// force free point
		if ( isFreePoint() )
			point = new JDPoint( 
				canvas.getInverseTransform().transform( e.getPoint(), null ) );
		// try to adjust to intersection or vertex
		else
		{
			Point2D p = canvas.adjustPointToIntersection( e.getPoint() );
			p = p == null ? canvas.adjustToVertex( e.getPoint() ) : p;
			if ( p != null )
				point = new JDPoint( p );
			else
				point = new JDPoint( canvas.getInverseTransform().transform( e.getPoint(), null ) );
		}

		// add point to exercise
		Color color = isLineStyle() 
					  ? app.getColor() 
					  : app.getPointColor();
		BasicStroke stroke = isLineStyle()
							 ? app.getStroke()
							 : app.getPointStroke();
		app.addShapeFromIterator( point.getPathIterator( null ), 
								  "", getLocaleText( "new_point" ), color, stroke );

		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		int crossSize = 7;		
		Point2D point = mouse().getPoint();
		// adjust to intersection or vertex
		if ( !isFreePoint() )
		{
			Point2D p =	canvas.adjustPointToIntersection( mouse().getPoint() );
			p = p == null ? canvas.adjustToVertex( mouse().getPoint() ) : p;
			if ( p != null )
			{
				point = canvas.getTransform().transform( p, null );
				crossSize = 11;
			}
		}
		
		g2.setColor( Application.toolMainColor );
		g2.setStroke( new BasicStroke( 1f ) );
		g2.drawOval( (int) Math.round( point.getX() - crossSize / 2. ), 
					 (int) Math.round( point.getY() - crossSize / 2. ),
					 crossSize, crossSize );
	}
	
	// --- HELPERS
	
	// check modifiers
	private boolean isFreePoint() { return mouse().isControlDown(); }
	private boolean isLineStyle() { return mouse().isShiftDown(); }
}
