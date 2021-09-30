package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.linesIntersection;
import static jdrafting.geom.JDMath.midpoint;
import static jdrafting.geom.JDMath.normal;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
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
			Path2D triangle = getTriangle( logicMouse );
			
			// add triangle to exercise
			app.addShapeFromIterator( triangle.getPathIterator( null ), "", 
									  getLocaleText( "new_triangle" ), 
									  app.getColor(), null, app.getStroke() );
			
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
		g2.setColor( Application.toolMainColor );
		g2.setStroke( new BasicStroke( 1f ) );
		
		// draw triangle
		g2.draw( 
				transform.createTransformedShape( getTriangle( logicMouse ) ) );
	}
	
	// --- HELPERS
	
	// check modifiers
	private boolean isRectangle() { return mouse().isControlDown(); }
	private boolean isEquilateral() { return mouse().isShiftDown(); }
	
	private Path2D getTriangle( Point2D logicMouse )
	{
		if ( A == null )  return null;
		
		Path2D triangle = new Path2D.Double();
		
		triangle.moveTo( A.getX(), A.getY() );		
		if ( B == null )
		{
			triangle.lineTo( logicMouse.getX(), logicMouse.getY() );
			return triangle;  // return line (A,mouse)
		}
		triangle.lineTo( B.getX(), B.getY() );
		if ( isRectangle() )
		{
			Point2D v = vector( A, B );
			Point2D n = normal( v );
			Point2D p;
			if ( logicMouse.distance( A ) < logicMouse.distance( B ) )
				p = A;
			else
				p = B;
			Point2D C = linesIntersection( p, sumVectors( p, n ), logicMouse, 
										   sumVectors( logicMouse, v ) );
			triangle.lineTo( C.getX(), C.getY() );
		}
		else if ( isEquilateral() )
		{
			double factor = Line2D.Double.relativeCCW( 
									A.getX(), A.getY(), B.getX(), B.getY(), 
									logicMouse.getX(), logicMouse.getY() ) < 0
							? Math.sqrt( 3 ) / 2.
							: -Math.sqrt( 3 ) / 2.;
			Point2D v = vector( A, B );
			Point2D n = 
				adjustVectorToSize( normal( v ), v.distance( 0, 0 ) * factor );
			Point2D C = sumVectors( midpoint( A, B ), n );
			triangle.lineTo( C.getX(), C.getY() );
		}
		else
			triangle.lineTo( logicMouse.getX(), logicMouse.getY() );
		triangle.lineTo( A.getX(), A.getY() );
		
		return triangle;
	}
}
