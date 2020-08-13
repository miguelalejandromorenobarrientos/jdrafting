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
import java.awt.geom.Rectangle2D;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDMath;
import jdrafting.geom.JDPoint;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class EllipseListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
								JDUtils.getCustomCursor( "ellipse_cursor.png" );
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

			// add shape to exercise
			if ( addAxisFocus() )
			{
				@SuppressWarnings("serial")
				CompoundEdit transaction = new CompoundEdit() {
					@Override
					public boolean canRedo() { return true; };
					@Override
					public boolean canUndo() { return true; };
					@Override
					public String getRedoPresentationName()
					{
						return "Redo add ellipse, axis and focus";
					}
					public String getUndoPresentationName()
					{
						return "Undo add ellipse, axis and focus";
					}
				};

				// add ellipse
				Rectangle2D bounds = ellipse.getBounds2D();
				app.addShapeFromIterator( ellipse.getPathIterator( null, flatness ), "", 
										  getLocaleText( "new_ellipse" ), 
										  app.getColor(), app.getStroke(), transaction );
				// add axis
				Line2D axisH = new Line2D.Double( bounds.getMinX(), bounds.getCenterY(), 
												  bounds.getMaxX(), bounds.getCenterY() );
				Line2D axisV = new Line2D.Double( bounds.getCenterX(), bounds.getMinY(), 
												  bounds.getCenterX(), bounds.getMaxY() );
				app.addShapeFromIterator( axisH.getPathIterator( null ), "", 
										  getLocaleText( "new_h_axis" ), 
										  app.getColor(), app.getStroke(), transaction );
				app.addShapeFromIterator( axisV.getPathIterator( null ), "", 
										  getLocaleText( "new_v_axis" ), 
										  app.getColor(), app.getStroke(), transaction );
				// add focuses
				double w = ellipse.getWidth() / 2, h = ellipse.getHeight() / 2;
				Point2D f1, f2;
				if ( w > h )
				{
					double f = Math.sqrt( w * w - h * h );
					f1 = new Point2D.Double( bounds.getCenterX() - f,
											 bounds.getCenterY() );
					f2 = new Point2D.Double( bounds.getCenterX() + f,
							 				 bounds.getCenterY() );
				}
				else
				{
					double f = Math.sqrt( h * h - w * w );
					f1 = new Point2D.Double( bounds.getCenterX(),
											 bounds.getCenterY() + f );
					f2 = new Point2D.Double( bounds.getCenterX(),
											 bounds.getCenterY() - f );
				}
				app.addShapeFromIterator( new JDPoint( f1 ).getPathIterator( null ), "", 
										  getLocaleText( "new_focus" ) + " 1", app.getPointColor(), 
										  app.getPointStroke(), transaction );
				app.addShapeFromIterator( new JDPoint( f2 ).getPathIterator( null ), "", 
										  getLocaleText( "new_focus" ) + " 2", 
										  app.getPointColor(), app.getPointStroke(), transaction );
				
				transaction.end();
				app.undoSupport.postEdit( transaction );				
			}
			else
				app.addShapeFromIterator( ellipse.getPathIterator( null, flatness ), "", 
										  getLocaleText( "new_ellipse" ), 
										  app.getColor(), app.getStroke() );
			
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
			g2.setColor( Application.toolMainColor );
			
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
	private boolean addAxisFocus() { return mouse().isControlDown(); }

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
