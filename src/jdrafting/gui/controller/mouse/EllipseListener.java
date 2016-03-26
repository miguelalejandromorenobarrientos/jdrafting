package jdrafting.gui.controller.mouse;

import static jdrafting.gui.Application.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.geom.JDMath;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

public class EllipseListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
							CanvasPanel.getCustomCursor( "ellipse_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D centerOrCorner;
	
	public EllipseListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_ellipse1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( centerOrCorner != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		// set center or corner
		if ( centerOrCorner == null )
		{
			centerOrCorner = logicMouse;
			app.setStatusText( getLocaleText( "txt_ellipse2" ) );
			canvas.repaint();
		}
		// set ellipse
		else		
		{
			// create ellipse
			Ellipse2D ellipse = getEllipse( logicMouse );
			double flatness = ellipse.getWidth() / app.getFlatnessValue();

			// add ellipse to exercise
			app.addShapeFromIterator( ellipse.getPathIterator( null, flatness ),
									"", "", app.getColor(), app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}		
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( centerOrCorner != null )
		{
			AffineTransform transform = canvas.getTransform();
			
			// mouse position on logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set tool style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.TOOL_MAIN_COLOR );
			
			// draw ellipse
			Ellipse2D ellipse = getEllipse( logicMouse );
			g2.draw( transform.createTransformedShape( ellipse ) );
			// draw axis
			g2.draw( transform.createTransformedShape( 
				new Line2D.Double( ellipse.getMinX(), ellipse.getCenterY(),
								ellipse.getMaxX(), ellipse.getCenterY() ) ) );
			g2.draw( transform.createTransformedShape( 
					new Line2D.Double( ellipse.getCenterX(), ellipse.getMinY(),
								ellipse.getCenterX(), ellipse.getMaxY() ) ) );
		}
	}
		
	// --- HELPERS ---
	
	// check modifiers
	private boolean isNotCentered() { return mouse().isShiftDown(); }

	/**
	 * Get the ellipse in the logic viewport 
	 * @param logicMouse
	 * @return the ellipse
	 */
	private Ellipse2D getEllipse( Point2D logicMouse )
	{
		Point2D corner;  // upper-left corner
		double w, h;  // ellipse width, height

		// diagonal
		Point2D diag = JDMath.vector( centerOrCorner, logicMouse );
		if ( app.isUsingRuler() )
			diag = new Point2D.Double( 
				Math.signum( diag.getX() ) * app.getDistance(), diag.getY() );
		// ellipse from corner
		if ( isNotCentered() )
		{
			w = diag.getX();
			h = diag.getY();
			corner = centerOrCorner;
		}
		// ellipse from center
		else
		{
			w = 2 * diag.getX();
			h = 2 * diag.getY();
			corner = new Point2D.Double( centerOrCorner.getX() - diag.getX(),
										 centerOrCorner.getY() - diag.getY() );
		}
		// adjust corner to mouse side
		if ( w < 0. )
			corner = new Point2D.Double( corner.getX() + w, corner.getY() );
		if ( h < 0. )
			corner = new Point2D.Double( corner.getX(), corner.getY() + h );
		
		return new Ellipse2D.Double( corner.getX(), corner.getY(),
									 Math.abs( w ), Math.abs( h ) );
	}
}
