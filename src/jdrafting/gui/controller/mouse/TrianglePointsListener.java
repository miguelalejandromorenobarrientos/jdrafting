package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.baricenter;
import static jdrafting.geom.JDMath.circumcenter;
import static jdrafting.geom.JDMath.incenter;
import static jdrafting.geom.JDMath.ortocenter;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import jdrafting.geom.JDPoint;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create bari/orto/circum/in-center using mouse control 
 */
public class TrianglePointsListener extends AbstractCanvasMouseListener
{
	private static final Map<Integer,Cursor> CURSOR;							
	public static final int BARICENTER = 0;
	public static final int INCENTER = 1;
	public static final int CIRCUMCENTER = 2;
	public static final int ORTOCENTER = 3;
	private CanvasPanel canvas;
	private Application app;

	private int type;
	private Point2D A, B;
	
	static {
		CURSOR = new HashMap<>();
		CURSOR.put( BARICENTER, 
					JDUtils.getCustomCursor( "baricenter_cursor.png" ) );
		CURSOR.put( INCENTER, 
					JDUtils.getCustomCursor( "incenter_cursor.png" ) );
		CURSOR.put( CIRCUMCENTER, 
					JDUtils.getCustomCursor( "circumcenter_cursor.png" ) );
		CURSOR.put( ORTOCENTER, 
					JDUtils.getCustomCursor( "ortocenter_cursor.png" ) );
	}
	
	public TrianglePointsListener( CanvasPanel canvas, int type )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		this.type = type;

		canvas.setCursor( CURSOR.get( type ) );
		
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

		if ( A == null )
			A = logicMouse;
		else if ( B == null )
			B = logicMouse;
		else
		{
			Point2D point = getPoint( logicMouse );
			if ( point == null )
				JOptionPane.showMessageDialog( app, "null point", 
							"Error in triangle", JOptionPane.ERROR_MESSAGE  );
			else
			{
				Color color = e.isShiftDown() 
							  ? app.getColor()
							  : app.getPointColor();
				BasicStroke stroke = e.isShiftDown() 
									 ? app.getStroke()
									 : app.getPointStroke();
				app.addShapeFromIterator( 
							new JDPoint( point ).getPathIterator( null ), "",
							"> " + getName( type ), color, stroke );
			}
			
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
		
		g2.setColor( Application.TOOL_MAIN_COLOR );
		g2.setStroke( new BasicStroke( 1f ) );
		
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
		
		// draw temporary triangle point
		Point2D point = getPoint( logicMouse );
		if ( point != null )
		{
			transform.transform( point, point );
			g2.fill( new Ellipse2D.Double( 
								point.getX() - 4, point.getY() - 4, 8, 8 ) );
		}
	}	
	
	// --- HELPERS ---
	
	/**
	 * Get action triangle point
	 * @param logicMouse mouse logic position
	 * @return the suitable point
	 */
	private Point2D getPoint( Point2D logicMouse )
	{
		switch( type )
		{
			case INCENTER:     return incenter( A, B, logicMouse );
			case ORTOCENTER:   return ortocenter( A, B, logicMouse );
			case BARICENTER:   return baricenter( A, B, logicMouse );
			case CIRCUMCENTER: return circumcenter( A, B, logicMouse );
			default: 		   return null;
		}
	}
	
	/**
	 * Get action name
	 * @param type point type (defined constants)
	 * @return an action string name in locale lenguage
	 */
	public static String getName( int type )
	{
		switch ( type )
		{
			case TrianglePointsListener.INCENTER: 
				return getLocaleText( "incenter" );
			case TrianglePointsListener.ORTOCENTER: 
				return getLocaleText( "ortocenter" );
			case TrianglePointsListener.CIRCUMCENTER: 
				return getLocaleText( "circumcenter" );
			case TrianglePointsListener.BARICENTER: 
				return getLocaleText( "baricenter" );
			default: return "Triangle notable points";
		}
	}
}
