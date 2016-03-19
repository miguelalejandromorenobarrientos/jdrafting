package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import jdrafting.geom.JDPoint;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

public class PointListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
							CanvasPanel.getCustomCursor( "point_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public PointListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( Application.getLocaleText( "txt_point" ) );
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
		// try to adjust to intersection
		else
		{
			Point2D joint = canvas.adjustPointToIntersection( e.getPoint() );
			if ( joint != null )  // intersection
				point = new JDPoint( joint );
			else  // free point
				point = new JDPoint( canvas.getInverseTransform()
											.transform( e.getPoint(), null ) );
		}

		// add point to exercise
		Color color = isLineStyle() ? app.getColor() : app.getPointColor();
		BasicStroke stroke = 
					isLineStyle() ? app.getStroke() : app.getPointStroke();
		app.addShapeFromIterator( point.getPathIterator( null ), 
								  "", "", color, stroke );

		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		int cross = 7;  // cross size		
		Point2D point = mouse().getPoint();
		// adjust to intersection
		if ( !isFreePoint() )
		{
			Point2D joint =
						canvas.adjustPointToIntersection( mouse().getPoint() );
			if ( joint != null )  // intersection
			{
				point = canvas.getTransform().transform( joint, null );
				cross = 11;
			}
		}
		
		g2.setColor( Application.TOOL_MAIN_COLOR );
		g2.setStroke( new BasicStroke( 1f ) );
		g2.drawOval( (int) Math.round( point.getX() - cross / 2. ), 
					 (int) Math.round( point.getY() - cross / 2. ),
					 cross, cross );
	}
	
	// --- HELPERS
	
	// check modifiers
	private boolean isFreePoint() { return mouse().isControlDown(); }
	private boolean isLineStyle() { return mouse().isShiftDown(); }
}
