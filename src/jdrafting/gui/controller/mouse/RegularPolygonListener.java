package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import jdrafting.geom.JDMath;
import jdrafting.geom.JDPoint;
import jdrafting.geom.JDStrokes;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Create regular polygon by mouse control 
 */
public class RegularPolygonListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "reg_poly_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private int n; 
	private Point2D center;
	
	public RegularPolygonListener( CanvasPanel canvas, int n )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		this.n = n;
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_reg_poly1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		if ( center != null || app.isUsingRuler() )		
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		// put center
		if ( center == null && !app.isUsingRuler() )
		{
			center = logicMouse;
			app.setStatusText( getLocaleText( "txt_reg_poly2" ) );
			canvas.repaint();
		}
		// add polygon
		else
		{
			if ( addMore() )  // add complete transaction
			{
				//////////////////////////// TRANSACTION ////////////////////////////				
				final JDCompoundEdit transaction = new JDCompoundEdit( 
					   getLocaleText( "reg_poly" ) + " <small>(& center & circumscribed)</small>" );
				
				final Map<String,Object> map = getPolygon( logicMouse );
				// add polygon
				app.addShapeFromIterator( ( (Path2D) map.get( "polygon" ) ).getPathIterator( null ), 
										  "", getLocaleText( "new_regular_polygon" ), 
										  app.getColor(), null, app.getStroke(), transaction );
				// add center
				app.addShapeFromIterator( ( new JDPoint( (Point2D) map.get( "center" ) ) )
											.getPathIterator( null ), 
										  "", getLocaleText( "new_center_reg" ), 
										  app.getPointColor(), null, app.getPointStroke(), 
										  transaction );
				// add circumference circumscribed
				final double flatness = 
								( JDMath.length( (Ellipse2D) map.get( "circumscribed" ), null ) ) 
								/ app.getFlatnessValue();
				app.addShapeFromIterator( ( (Ellipse2D) map.get( "circumscribed" ) )
												.getPathIterator( null, flatness ), 
										  "", getLocaleText( "new_circumscribed" ), app.getColor(), 
										  null, app.getStroke(), transaction );
				
				transaction.end();
				app.undoRedoSupport.postEdit( transaction );
				/////////////////////////////////////////////////////////////////////
			}
			else
				// add polygon to exercise
				app.addShapeFromIterator( ( (Path2D) getPolygon( logicMouse ).get( "polygon" ) )
											.getPathIterator( null ), "", 
										  getLocaleText( "new_regular_polygon" ), 
										  app.getColor(), null, app.getStroke() );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( center != null || app.isUsingRuler() )
		{
			final AffineTransform transform = canvas.getTransform();

			// mouse position in logic viewport
			final Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );
			
			final Map<String,Object> map = getPolygon( logicMouse );
			
			// draw polygon
			g2.draw( transform.createTransformedShape( 
											(Path2D) map.get( "polygon" ) ) );
			// draw center
			g2.setStroke( new BasicStroke( 
						8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "center" ) ) ) );
			// draw circumference circumscribed
			g2.setStroke( 
					JDStrokes.getStroke( JDStrokes.DASHED.getStroke(), 1f ) );
			g2.draw( transform.createTransformedShape(
									(Ellipse2D) map.get( "circumscribed" ) ) );
		}
	}
	
	// --- HELPERS ---

	// check modifiers
	private boolean addMore() { return mouse().isControlDown(); }
	
	/**
	 * Gets the polygon
	 * @param logicMouse
	 * @return the polygon in the logic viewport
	 */
	private Map<String,Object> getPolygon( Point2D logicMouse )
	{
		// calculate radius and center
		final double radius = app.isUsingRuler() 
							  ? app.getDistance() 
							  : center.distance( logicMouse );
		final Point2D center = this.center != null ? this.center : logicMouse;
		
		// create polygon
		final Path2D polygon = new Path2D.Double();
		final double sideAng = 2 * Math.PI / n,
					 offsetAng = Math.PI * ( 3. / 2. - 1. / n ) % sideAng;
		double ang = offsetAng;
		
		polygon.moveTo( center.getX() + radius * Math.cos( ang ),
						center.getY() + radius * Math.sin( ang ) );
		
		for ( int i = 1; i < n; i++ )
		{
			ang += sideAng;
			polygon.lineTo( center.getX() + radius * Math.cos( ang ),
							center.getY() + radius * Math.sin( ang ) );
		}
		polygon.lineTo( center.getX() + radius * Math.cos( offsetAng ),
						center.getY() + radius * Math.sin( offsetAng ) );
		//polygon.closePath();
		
		// return results
		final Map<String,Object> map = new HashMap<>();
		map.put( "polygon", polygon );
		map.put( "center", center );
		map.put( "circumscribed", new Ellipse2D.Double( 
													center.getX() - radius, center.getY() - radius, 
													2 * radius, 2 * radius ) );		
		return map;
	}
}
