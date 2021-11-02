package jdrafting.gui.controller.mouse;

import static java.lang.Math.PI;
import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.geom.JDMath.vectorArg;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create an arrow using mouse control 
 */
public class ArrowListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "arrow_cursor.png" );
	private static final double ANGLE_INTERVAL = PI / 4.;
	private CanvasPanel canvas;
	private Application app;

	private Point2D start;
	
	public ArrowListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_arrow1" ) );
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
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_arrow2" ) );
			canvas.repaint();
		}
		else
		{
			// add shape to exercise
			app.addShapeFromIterator( getArrow( logicMouse ).getPathIterator( null ), "", 
									  getLocaleText( "new_arrow" ), 
									  app.getColor(), null, app.getStroke() );

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
			
			// draw segment
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );

			g2.draw( canvas.getTransform().createTransformedShape( getArrow( logicMouse ) ) );
		}
	}
	
	
	// --- HELPERS ---

	// check modifiers
	private boolean isFixedAngle() { return mouse().isShiftDown(); }
	private boolean isDouble() { return mouse().isControlDown(); }
	
	/**
	 * Get arrow in logic viewport
	 * @param logicMouse
	 * @return the segment
	 */
	private Path2D getArrow( Point2D logicMouse )
	{
		Point2D end = logicMouse;
		
		// adjust to basic main angles
		if ( isFixedAngle() )
		{
			double ang = Math.atan2( end.getY() - start.getY(), 
									 end.getX() - start.getX() );
			double newAng = ANGLE_INTERVAL * Math.round( ang / ANGLE_INTERVAL );
			end = pointRelativeToCenter( start, newAng,	start.distance( end ) );
		}
		
		// fixed distance
		if ( app.isUsingRuler() )
			end = sumVectors( start, adjustVectorToSize( vector( start, end ), 
					   									 app.getDistance() ) ); 

		final double dist = start.distance( end ),
					 afactor = 1.075, dfactor = Math.log( Math.E + dist / 1000 ) * 4.,
					 ang = vectorArg( vector( start, end ) );
		final Path2D arrow = new Path2D.Double();
		
		arrow.moveTo( start.getX(), start.getY() );
		arrow.lineTo( end.getX(), end.getY() );
		Point2D aux = pointRelativeToCenter( end, ang + PI * afactor, dist / dfactor );
		arrow.lineTo( aux.getX(), aux.getY() );
		arrow.moveTo( end.getX(), end.getY() );
		aux = pointRelativeToCenter( end, ang - PI * afactor, dist / dfactor );
		arrow.lineTo( aux.getX(), aux.getY() );
		if ( isDouble() )
		{
			arrow.moveTo( start.getX(), start.getY() );
			aux = pointRelativeToCenter( start, ang + PI * ( afactor - 1 ), dist / dfactor );
			arrow.lineTo( aux.getX(), aux.getY() );
			arrow.moveTo( start.getX(), start.getY() );
			aux = pointRelativeToCenter( start, ang - PI * ( afactor - 1 ), dist / dfactor );
			arrow.lineTo( aux.getX(), aux.getY() );
		}
		arrow.moveTo( end.getX(), end.getY() );  // (for extremes tool)
		
		return arrow;
	}
}
