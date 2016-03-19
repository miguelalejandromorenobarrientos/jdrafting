package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.geom.JDMath.vectorArg;
import static jdrafting.gui.Application.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

/**
 * Creates an arc using mouse control 
 */
public class ArcListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
								CanvasPanel.getCustomCursor( "arc_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D center, start;
	
	public ArcListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );	
		
		app.setStatusText( getLocaleText( "txt_arc1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		// set center
		if ( center == null )
		{
			center = logicMouse;
			app.setStatusText( getLocaleText( "txt_arc2" ) );
		}
		// set arc start point
		else if ( start == null )
		{
			start = getStart( logicMouse );
			app.setStatusText( getLocaleText( "txt_arc3" ) );
		}
		// set arc
		else
		{
			// get end arc point
			Point2D end = getEnd( logicMouse );
			
			// create arc
			double radius = center.distance( end );
			double startAng = Math.toDegrees( vectorArg( 
											vector( center, start ) ) );
			Arc2D arc = new Arc2D.Double( 
				center.getX() - radius, center.getY() - radius,
				2 * radius, 2 * radius,
				-startAng, -getArcExtent( end, logicMouse ), Arc2D.OPEN );
			double flatness = arc.getWidth() / app.getFlatnessValue();

			// add shape to exercise
			app.addShapeFromIterator( arc.getPathIterator( null, flatness ), 
									"", "",	app.getColor(), app.getStroke() );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
		
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( center != null )
		{
			// get transform
			AffineTransform transform = canvas.getTransform();

			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setStroke( new BasicStroke( 1f, BasicStroke.CAP_ROUND, 
										   BasicStroke.JOIN_ROUND ) );
			g2.setColor( Application.TOOL_MAIN_COLOR );
			
			// draw center-start radius and circumference
			if ( start == null )
			{
				Point2D tempStart = getStart( logicMouse );
				double radius = center.distance( tempStart );
				g2.draw( transform.createTransformedShape( 
									new Line2D.Double( center, tempStart ) ) );
				g2.draw( transform.createTransformedShape( 
						new Ellipse2D.Double( center.getX() - radius,
											  center.getY() - radius,
											  2 * radius, 2 * radius ) ) );
			}
			// draw center-end radius, circumference and arc
			else
			{
				// circumference
				double radius = center.distance( start );
				g2.draw( transform.createTransformedShape( 
						new Ellipse2D.Double( center.getX() - radius,
											  center.getY() - radius,
											  2 * radius, 2 * radius ) ) );
				// radius
				Point2D end = getEnd( logicMouse );
				g2.draw( transform.createTransformedShape( 
										new Line2D.Double( center, end ) ) );
				// arc
				g2.setStroke( new BasicStroke( 5f, BasicStroke.CAP_BUTT,
											   BasicStroke.JOIN_ROUND ) );
				double startAng =
						Math.toDegrees( vectorArg( vector( center, start ) ) );
				double extent = getArcExtent( end, logicMouse );
				Arc2D arc = new Arc2D.Double( 
								center.getX() - radius, center.getY() - radius,
								2 * radius, 2 * radius,
								-startAng, -extent, Arc2D.OPEN );
				g2.draw( transform.createTransformedShape( arc ) );
				// draw angle info
				int mouseX = mouse().getX(), mouseY = mouse().getY();
				String angInfo = String.format( "%.2f", extent ) + "º";
				g2.setFont( new Font( Font.SERIF, Font.BOLD, 16 ) );
				g2.setColor( new Color( 40, 40, 180 ) );
				g2.drawString( angInfo, mouseX + 21, mouseY - 9 );
				g2.setColor( Color.LIGHT_GRAY );			
				g2.drawString( angInfo, mouseX + 20, mouseY - 10 );
			}
		}
	}
	
	
	// --- HELPERS ---
	
	// check modifiers
	private boolean isAngleFixed() { return mouse().isShiftDown(); }
	private boolean isAngleConjugated() { return mouse().isControlDown(); }
	private boolean isAngleInteger() { return mouse().isAltDown(); }
	
	/**
	 * Get start point of the arc
	 * @param logicMouse
	 * @return logic start point
	 */
	private Point2D getStart( Point2D logicMouse )
	{
		return app.isUsingRuler()
			   ? sumVectors( center, adjustVectorToSize(
					   	vector( center, logicMouse ), app.getDistance() ) )
			   : logicMouse;
	}
	
	/**
	 * Get end point of the arc
	 * @param logicMouse
	 * @return logic end point
	 */
	private Point2D getEnd( Point2D logicMouse )
	{
		Point2D end;
		double radius = center.distance( start );
		
		if ( isAngleFixed() )  // fixed angle
		{
			Line2D startLine = new Line2D.Double( center, start );
			double offAng = vectorArg( vector( center, start ) );
			double fixAng = startLine.relativeCCW( logicMouse ) < 0
							? app.getAngle()
							: -app.getAngle();
			end = pointRelativeToCenter( 
							center, offAng + Math.toRadians( fixAng ), radius );
		}
		else  // free angle
			end = sumVectors( center, adjustVectorToSize( 
					 			vector( center, logicMouse ), radius ) );
		
		return end;
	}

	/**
	 * Get extent angle of the arc
	 * @param end endpoint
	 * @param logicMouse
	 * @return the extent angle in degrees
	 */
	private double getArcExtent( Point2D end, Point2D logicMouse )
	{
		double startAng = vectorArg( vector( center, start ) );
		double endAng = vectorArg( vector( center, end ) );
		
		double extent = startAng < endAng
						? endAng - startAng
						: 2 * Math.PI + ( endAng - startAng );
		
		extent = Math.toDegrees( extent );
		
		if ( isAngleConjugated() )
			extent = extent - 360;

		if ( isAngleInteger() )
			extent = Math.rint( extent );
		
		/*Line2D startLine = new Line2D.Double( center, start );
		if ( isAngleFixed() )
		{
			if ( startLine.relativeCCW( logicMouse ) > 0  )
				extent = extent > 0 ? -( 2 * Math.PI - extent ) : -( 2 * Math.PI + extent );
		}*/
			
		return extent;
	}
}
