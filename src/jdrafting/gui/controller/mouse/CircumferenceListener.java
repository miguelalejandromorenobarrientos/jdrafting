package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.geom.JDMath;
import jdrafting.geom.JDPoint;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

public class CircumferenceListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "circumference_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D center;
	
	public CircumferenceListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_circ1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( app.isUsingRuler() || center != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		if ( center == null && !app.isUsingRuler() )
		{
			center = logicMouse;
			app.setStatusText( getLocaleText( "txt_circ2" ) );
		}
		else		
		{
			// create circumference
			final Ellipse2D circ = getCircumference( logicMouse );
			center = center == null ? logicMouse : center;
			final double flatness = JDMath.length( circ, null )/*circ.getWidth()*/ / app.getFlatnessValue();

			// add shape to exercise
			if ( addCenter() )
			{
				//////////////////////////// TRANSACTION ////////////////////////////
				final JDCompoundEdit transaction = new JDCompoundEdit( 
								  getLocaleText( "circumference" ) + " <small>(& center)</small>" );
				
				// add circumference
				app.addShapeFromIterator( JDUtils.openShape( circ )
										  .getPathIterator( null, flatness ), "", 
										  getLocaleText( "new_circumference" ), 
										  app.getColor(), null, app.getStroke(), transaction );
				// add center
				app.addShapeFromIterator( new JDPoint( center ).getPathIterator( null ), "", 
										  getLocaleText( "new_circumference_center" ), 
										  app.getPointColor(), null, app.getPointStroke(), 
										  transaction );
				
				transaction.end();
				app.undoRedoSupport.postEdit( transaction );		
				/////////////////////////////////////////////////////////////////////
			}
			else  // add circumference
				app.addShapeFromIterator( JDUtils.openShape( circ )
										  .getPathIterator( null, flatness ), "", 
										  getLocaleText( "new_circumference" ), 
										  app.getColor(), null, app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}		

		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( center != null || app.isUsingRuler() )
		{
			final AffineTransform transform = canvas.getTransform();
			
			// mouse position on logic viewport
			final Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set tool style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );
			
			// draw circumference
			g2.draw( transform.createTransformedShape( 
											getCircumference( logicMouse ) ) );
			// draw radius
			if ( !app.isUsingRuler() )
				g2.draw( transform.createTransformedShape( 
					new Line2D.Double( center, 
									   isDiameter() 
									   ? JDMath.midpoint( center, logicMouse )
									   : logicMouse ) ) );
		}
	}
	
	
	// --- HELPERS ---
	
	// check modifiers
	private boolean isDiameter() { return mouse().isShiftDown(); }
	private boolean addCenter() { return mouse().isControlDown(); }

	/**
	 * Get the circumference in the logic viewport 
	 * @param logicMouse
	 * @return the circumference
	 */
	private Ellipse2D getCircumference( Point2D logicMouse )
	{
		Point2D c;
		double dist;
		if ( app.isUsingRuler() )
		{
			c = center == null ? logicMouse : center;
			dist = app.getDistance();
		}
		else
		{
			c = center;
			dist = c.distance( logicMouse );
		}
		if ( isDiameter() )
			dist /= 2.;
		
		return new Ellipse2D.Double( c.getX() - dist, c.getY() - dist, 2 * dist, 2 * dist );
	}
}
