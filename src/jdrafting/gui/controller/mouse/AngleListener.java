package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.projection;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.geom.JDMath;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create segment by angle using mouse control 
 */
public class AngleListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "angle_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D vertex, p1;	
	
	public AngleListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_angle1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( vertex != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// put vertex
		if ( vertex == null )
		{
			vertex = logicMouse;
			app.setStatusText( getLocaleText( "txt_angle2" ) );
		}
		// put first angle side
		else if ( p1 == null )
		{
			p1 = logicMouse;
			app.setStatusText( getLocaleText( "txt_angle3" ) );
		}
		// put second angle side
		else
		{
			Line2D segment = getSegment( logicMouse );
			
			// add segment to exercise
			app.addShapeFromIterator( segment.getPathIterator( null ), "", 
									  getLocaleText( "new_segment" ), 
									  app.getColor(), null, app.getStroke() );
			if ( isTwoSides() )  // add first side
				app.addShapeFromIterator( new Line2D.Double( vertex, p1 ).getPathIterator( null ), 
										  "", getLocaleText( "new_segment" ), 
										  app.getColor(), null, app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
		
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( vertex != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// get transform
			AffineTransform transform = canvas.getTransform();

			// set tool style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );			
			
			// draw first side
			Line2D line1 = new Line2D.Double( vertex, p1 == null ? logicMouse : p1 );			
			g2.draw( transform.createTransformedShape( line1 ) );			

			if ( p1 != null )
			{
				// draw second side
				Line2D line2 = getSegment( logicMouse );
				g2.draw( transform.createTransformedShape( line2 ) );

				// draw angle
				double ang = Math.toDegrees( JDMath.lineAng( line1, line2 ) );	
				double dist = Math.min( vertex.distance( p1 ), 
										vertex.distance( line2.getP2() ) ) / 2.;
				double offang = Math.toDegrees( Math.atan2(
					p1.getY() - vertex.getY(), p1.getX() - vertex.getX() ) );
				
				Arc2D arc = new Arc2D.Double( 
					vertex.getX() - dist / 2, vertex.getY() - dist / 2,
					dist, dist,
					-offang, line1.relativeCCW( logicMouse ) <= 0 ? -ang : ang,
					Arc2D.PIE );
				
				g2.setColor( Color.MAGENTA );
				g2.setStroke( new BasicStroke( 2f, BasicStroke.CAP_ROUND, 
							BasicStroke.JOIN_ROUND, 0f, new float[] { 3f, 5f }, 
							(float) Math.random() * 4 ) );
				g2.draw( transform.createTransformedShape( arc ) );					
				
				// draw angle info
				if ( !Double.isNaN( ang ) )
				{
					int mouseX = mouse().getX(), mouseY = mouse().getY();
					String angInfo = String.format( "%.2f", ang ) + "º";
					g2.setFont( new Font( Font.SERIF, Font.BOLD, 16 ) );
					g2.setColor( new Color( 40, 40, 180 ) );
					g2.drawString( angInfo, mouseX + 21, mouseY - 9 );
					g2.setColor( Color.LIGHT_GRAY );			
					g2.drawString( angInfo, mouseX + 20, mouseY - 10 );
				}
			}
		}
	}

	
	// --- HELPERS ---
	
	// check modifiers
	private boolean isSuplementary() { return mouse().isShiftDown(); }
	private boolean isComplementary() { return mouse().isControlDown(); }
	private boolean isTwoSides() { return mouse().isAltDown(); }
	
	/**
	 * Gets the segment in the logic viewport
	 * @param logicMouse
	 * @return the segment
	 */
	private Line2D getSegment( Point2D logicMouse )
	{
		// get angle
		double s1Ang = Math.atan2( p1.getY() - vertex.getY(),
								   p1.getX() - vertex.getX() );
		double extent = app.getAngle() <= 180 
						? app.getAngle()
						: 360 - app.getAngle();
		if ( isSuplementary() && extent <= 180 )
			extent = 180 - extent;
		else if ( isComplementary() && extent <= 90 )
			extent = 90 - extent;
		if ( new Line2D.Double( vertex, p1 ).relativeCCW( logicMouse ) > 0  )
			extent = -extent;
		double ang = s1Ang + Math.toRadians( extent );
		
		// return segment
		Point2D p2;
		if ( app.isUsingRuler() )
			p2 = pointRelativeToCenter( vertex, ang, app.getDistance() );
		else  // mouse->side projection
		{
			p2 = pointRelativeToCenter( vertex, ang, 1. );
			Point2D v = vector( vertex, p2 );
			Point2D w = vector( vertex, logicMouse );
			p2 = pointRelativeToCenter( vertex, ang, projection( v, w ) );
		}

		return new Line2D.Double( vertex, p2 );
	}
}
