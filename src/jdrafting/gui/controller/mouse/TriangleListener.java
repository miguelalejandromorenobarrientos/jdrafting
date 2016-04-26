package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create triangle using mouse control 
 */
public class TriangleListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "triangle_cursor.png" );							
	private CanvasPanel canvas;
	private Application app;

	private Point2D A, B;
	
	public TriangleListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_triangle_points1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( A != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// first vertex
		if ( A == null )
			A = logicMouse;
		// second vertex
		else if ( B == null )
			B = logicMouse;
		else
		{
			// create triangle
			Path2D triangle = new Path2D.Double();
			triangle.moveTo( A.getX(), A.getY() );
			triangle.lineTo( B.getX(), B.getY() );
			triangle.lineTo( logicMouse.getX(), logicMouse.getY() );
			triangle.lineTo( A.getX(), A.getY() );
			
			// add triangle to exercise
			app.addShapeFromIterator( triangle.getPathIterator( null ),
									"", "", app.getColor(), app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( A == null )  return;
		
		AffineTransform transform = canvas.getTransform();
		
		// mouse position on logic viewport
		Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
		
		// set tool style
		g2.setColor( Application.TOOL_MAIN_COLOR );
		g2.setStroke( new BasicStroke( 1f ) );
		
		// draw triangle
		if ( B == null )
		{
			g2.draw( transform.createTransformedShape( 
										new Line2D.Double( A, logicMouse ) ) );
			return;
		}
		else
			g2.draw( transform.createTransformedShape( 
												new Line2D.Double( A, B ) ) );
		g2.draw( transform.createTransformedShape( 
										new Line2D.Double( A, logicMouse ) ) );
		g2.draw( transform.createTransformedShape( 
										new Line2D.Double( B, logicMouse ) ) );
	}
	
	// --- HELPERS
	
	// check modifiers
	/*private boolean isRectangle() { return mouse().isControlDown(); }
	private boolean isEquilateral() { return mouse().isShiftDown(); }*/
}
