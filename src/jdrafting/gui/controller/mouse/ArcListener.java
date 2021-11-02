package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.geom.JDMath.vectorArg;
import static jdrafting.gui.JDUtils.getLocaleText;

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

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDMath;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Creates an arc using mouse control 
 */
public class ArcListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "arc_cursor.png" );
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
		
		if ( center != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
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
			final Point2D end = getEnd( logicMouse );
			
			// create arc
			final double radius = center.distance( end ),
						 startAng = Math.toDegrees( vectorArg( vector( center, start ) ) ),
						 extent = getArcExtent( end, logicMouse );
			final Arc2D arc = new Arc2D.Double( center.getX() - radius, center.getY() - radius,
										  		2 * radius, 2 * radius, 
										  		-startAng, -extent, 
										  		Arc2D.OPEN );
			final double flatness = JDMath.length( arc, null ) /*arc.getWidth()*/ / app.getFlatnessValue();

			final String descHtml = String.format( "<small>(%.1f�)</small>", extent );
			
			//////////////////////////// TRANSACTION ////////////////////////////
			final CompoundEdit transaction = new JDCompoundEdit( 
														  getLocaleText( "arc" ) + " " + descHtml );
			
			// add shape to exercise
			app.addShapeFromIterator( arc.getPathIterator( null, flatness ), "", 
							    	  getLocaleText( "new_arc" ) + " " + descHtml, app.getColor(), 
							    	  null, app.getStroke(), transaction );

			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////

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
			final AffineTransform transform = canvas.getTransform();

			// mouse position in logic viewport
			final Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setStroke( new BasicStroke( 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
			g2.setColor( Application.toolMainColor );
			
			// draw center-start radius and circumference
			if ( start == null )
			{
				final Point2D tempStart = getStart( logicMouse );
				final double radius = center.distance( tempStart );
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
				final double radius = center.distance( start );
				g2.draw( transform.createTransformedShape( 
						new Ellipse2D.Double( center.getX() - radius,
											  center.getY() - radius,
											  2 * radius, 2 * radius ) ) );
				// radius
				final Point2D end = getEnd( logicMouse );
				g2.draw( transform.createTransformedShape( new Line2D.Double( center, end ) ) );
				// arc
				g2.setStroke( new BasicStroke( 5f, BasicStroke.CAP_BUTT,
											   BasicStroke.JOIN_ROUND ) );
				final double startAng = Math.toDegrees( vectorArg( vector( center, start ) ) ),
							 extent = getArcExtent( end, logicMouse );
				final Arc2D arc = new Arc2D.Double( center.getX() - radius, center.getY() - radius,
													2 * radius, 2 * radius,
													-startAng, -extent, Arc2D.OPEN );
				g2.draw( transform.createTransformedShape( arc ) );
				// draw angle info
				final int mouseX = mouse().getX(), mouseY = mouse().getY();
				final String angInfo = String.format( "%.2f", extent ) + "�";
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
		final double radius = center.distance( start );
		
		if ( isAngleFixed() )  // fixed angle
		{
			final Line2D startLine = new Line2D.Double( center, start );
			final double offAng = vectorArg( vector( center, start ) ),
						 fixAng = startLine.relativeCCW( logicMouse ) < 0
								  ? app.getAngle()
								  : -app.getAngle();
			end = pointRelativeToCenter( center, offAng + Math.toRadians( fixAng ), radius );
		}
		else  // free angle
			end = sumVectors( center, adjustVectorToSize( vector( center, logicMouse ), radius ) );
		
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
		final double startAng = vectorArg( vector( center, start ) ),
					 endAng = vectorArg( vector( center, end ) );
		
		double extent = startAng < endAng
						? endAng - startAng
						: 2 * Math.PI + ( endAng - startAng );		
		extent = Math.toDegrees( extent );
		
		if ( isAngleConjugated() )
			extent = extent - 360;

		if ( isAngleInteger() )
			extent = Math.rint( extent );
			
		return extent;
	}
}
