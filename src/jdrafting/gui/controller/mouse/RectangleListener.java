package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create a rectangle by mouse control 
 */
public class RectangleListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "rectangle_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private Point2D start;
	
	public RectangleListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_rect1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		if ( start == null )  return;
		
		// dynamic cursor
		Point2D pos = canvas.getInverseTransform().transform( e.getPoint(), null );
		if ( ( pos.getX() - start.getX() ) * ( pos.getY() - start.getY() ) > 0 )
			canvas.setCursor( new Cursor( Cursor.NE_RESIZE_CURSOR ) );
		else
			canvas.setCursor( new Cursor( Cursor.NW_RESIZE_CURSOR ) );
		
		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		// put first corner
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_rect2" ) );
		}
		// finish rectangle
		else
		{
			// add rectangle to exercise
			app.addShapeFromIterator( getRectangle( logicMouse ).getPathIterator( null ), "", 
									  getLocaleText( isSquare() ? "new_square" : "new_rectangle" ), 
									  app.getColor(), null, app.getStroke() );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}

		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setStroke( new BasicStroke( 1f, BasicStroke.CAP_SQUARE, 
													BasicStroke.JOIN_BEVEL ) );
			g2.setColor( Application.toolMainColor );
			
			// draw rectangle
			g2.draw( canvas.getTransform().createTransformedShape( 
												getRectangle( logicMouse ) ) );
		}
	}
	
	// --- HELPERS ---

	// check modifiers
	private boolean isSquare() { return mouse().isShiftDown(); }
	
	/**
	 * Gets the rectangle 
	 * @param logicMouse
	 * @return the rectangle in the logic viewport
	 */
	private Rectangle2D getRectangle( Point2D logicMouse )
	{
		// calculate coords
		double x = app.isUsingRuler()
				   ? start.getX() > logicMouse.getX()
					 ? start.getX() - app.getDistance()
					 : start.getX() + app.getDistance()  
				   : logicMouse.getX();
		double minX = Math.min( start.getX(), x );
		double maxX = Math.max( start.getX(), x );
		double minY = Math.min( start.getY(), logicMouse.getY() );
		double maxY = Math.max( start.getY(), logicMouse.getY() );
		
		// create rectangle
		Rectangle2D rect = new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
		if ( isSquare() )  // create square
		{
			double size = Math.min( rect.getWidth(), rect.getHeight() );
			double px = logicMouse.getX() < start.getX()
						? start.getX() - size
						: start.getX();
			double py = logicMouse.getY() < start.getY()
						? start.getY() - size
						: start.getY();
			rect = new Rectangle2D.Double( px, py, size, size );
		}
		
		return rect;
	}
}
