package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDStrokes;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create hyperbola from bounds by mouse control 
 */
public class HyperbolaListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "hyperbola_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private Point2D start, end;
	
	public HyperbolaListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_hyperbola1" ) );
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
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		// put first corner of bounds
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_hyperbola2" ) );
			canvas.repaint();
		}
		// finish hyperbola bounds
		else if ( end == null )
		{
			end = logicMouse;
			app.setStatusText( getLocaleText( "txt_hyperbola3" ) );
			canvas.repaint();
		}
		// set 'a' value (semiaxis)
		else
		{
			// create undo/redo transaction
			@SuppressWarnings("serial")
			CompoundEdit transaction = new CompoundEdit() {
				@Override
				public boolean canRedo() { return true; };
				@Override
				public boolean canUndo() { return true; };
				@Override
				public String getRedoPresentationName()
				{
					return "Redo hyperbola";
				}
				@Override
				public String getUndoPresentationName()
				{
					return "Undo hyperbola";
				}
			};
			
			Map<String,Object> map = getHyperbola( logicMouse );
			// add hyperbola branches
			app.addShapeFromIterator( 
					( (Path2D) map.get( "branch1" ) ).getPathIterator( null ), 
					"", getLocaleText( "new_hype_branch" ) + " 1", app.getColor(), 
					app.getStroke(), transaction );
			app.addShapeFromIterator( 
					( (Path2D) map.get( "branch2" ) ).getPathIterator( null ), 
					"", getLocaleText( "new_hype_branch" ) + " 2", app.getColor(), 
					app.getStroke(), transaction );
			// add more hyperbola elements
			if ( addMore() )
			{
				app.addShapeFromIterator( ( (Rectangle2D) map.get( "bounds" ) )
													.getPathIterator( null ), 
						"", getLocaleText( "new_hype_bounds" ), app.getColor(), 
						app.getStroke(), transaction );
				app.addShapeFromIterator( ( (Line2D) map.get( "main_axis" ) )
													.getPathIterator( null ), 
						"", getLocaleText( "new_main_axis" ), app.getColor(), 
						app.getStroke(), transaction );
				app.addShapeFromIterator( ( (Line2D) map.get( "img_axis" ) )
													.getPathIterator( null ), 
						"", getLocaleText( "new_img_axis" ), app.getColor(), 
						app.getStroke(), transaction );
				app.addShapeFromIterator( 
						( new JDPoint( (Point2D) map.get( "center" ) ) )
													.getPathIterator( null ), 
						"", getLocaleText( "new_hype_center" ), 
						app.getPointColor(), app.getPointStroke(), transaction );
				app.addShapeFromIterator( 
					( new JDPoint( (Point2D) map.get( "vertex1" ) ) )
													.getPathIterator( null ), 
					"", getLocaleText( "new_hype_vertex" ), 
					app.getPointColor(), app.getPointStroke(), transaction );
				app.addShapeFromIterator( 
					( new JDPoint( (Point2D) map.get( "vertex2" ) ) )
													.getPathIterator( null ), 
					"", getLocaleText( "new_hype_vertex" ), 
					app.getPointColor(), app.getPointStroke(), transaction );
				app.addShapeFromIterator( 
						( new JDPoint( (Point2D) map.get( "focus1" ) ) )
													.getPathIterator( null ), 
						"", getLocaleText( "new_focus" ), app.getPointColor(), 
						app.getPointStroke(), transaction );
				app.addShapeFromIterator( 
						( new JDPoint( (Point2D) map.get( "focus2" ) ) )
													.getPathIterator( null ), 
						"", getLocaleText( "new_focus" ), app.getPointColor(), 
						app.getPointStroke(), transaction );
			}
			
			transaction.end();
			app.undoSupport.postEdit( transaction );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			AffineTransform transform = canvas.getTransform();

			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setColor( Application.toolMainColor );
			
			// get hyperbola data
			Map<String,Object> map = getHyperbola( logicMouse );
			
			// draw hiperbola branches
			g2.setStroke( new BasicStroke( 1f ) );
			g2.draw( transform.createTransformedShape( 
											(Path2D) map.get( "branch1" ) ) );
			g2.draw( transform.createTransformedShape( 
											(Path2D) map.get( "branch2" ) ) );
			// draw bounds
			g2.setStroke( 
					JDStrokes.getStroke( JDStrokes.DASHED.getStroke(), 1f ) );
			g2.draw( transform.createTransformedShape( 
										(Rectangle2D) map.get( "bounds" ) ) );
			// draw main axis
			g2.draw( transform.createTransformedShape(
											(Line2D) map.get( "main_axis" ) ) );
			g2.draw( transform.createTransformedShape(
											(Line2D) map.get( "img_axis" ) ) );
			// draw center
			g2.setStroke( new BasicStroke( 4f ) );
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "center" ) ) ) );
			// draw vertexes
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "vertex1" ) ) ) );
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "vertex2" ) ) ) );
			// draw focuses
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "focus1" ) ) ) );
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "focus2" ) ) ) );
		}
	}
	
	// --- HELPERS ---

	// check modifiers
	private boolean addMore() { return mouse().isControlDown(); }
	
	/**
	 * Gets the hyperbola
	 * @param logicMouse
	 * @return the hyperbola in the logic viewport
	 */
	private Map<String,Object> getHyperbola( Point2D logicMouse )
	{
		Point2D endPoint = end == null ? logicMouse : end;
		
		// calculate coords
		double x = app.isUsingRuler()
				   ? start.getX() > endPoint.getX()
					 ? start.getX() - app.getDistance()
					 : start.getX() + app.getDistance()  
				   : endPoint.getX();
		double minX = Math.min( start.getX(), x );
		double maxX = Math.max( start.getX(), x );
		double minY = Math.min( start.getY(), endPoint.getY() );
		double maxY = Math.max( start.getY(), endPoint.getY() );
		
		// create hyperbola rectangle bounds
		Rectangle2D bounds = 
				new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
		
		// create hyperbola
		Path2D branch1 = new Path2D.Double(), branch2 = new Path2D.Double();

		double w = bounds.getWidth(), h = bounds.getHeight();
		double a = end == null // main semi-axis
				   ? h / 4 
				   : Math.abs( logicMouse.getY() - bounds.getCenterY() );
		double b = w * a / Math.sqrt( h * h - 4 * a * a );  // img semi-axis
		double c = Math.sqrt( a * a + b * b );  // focal semi-distance
		int n = (int) Math.sqrt( app.getFlatnessValue() );

		for ( int i = 0; i < n; i++ )
		{
			double xh = -w / 2. + w / ( n - 1 ) * i;
			// y=a*sqrt(x^2/b^2+1)  (vertical branches)
			double yh = a * Math.sqrt( xh * xh / ( b * b ) + 1 );
			if ( i != 0 )
			{
				branch1.lineTo( xh, yh );
				branch2.lineTo( xh, -yh );
			}
			else
			{
				branch1.moveTo( xh, yh );
				branch2.moveTo( xh, -yh );
			}
		}
		
		// center
		Point2D center = new Point2D.Double( bounds.getCenterX(), 
											 bounds.getCenterY() );
		// vertexes
		Point2D vertex1 = new Point2D.Double( bounds.getCenterX(), 
											  bounds.getCenterY() + a );
		Point2D vertex2 = new Point2D.Double( bounds.getCenterX(), 
											  bounds.getCenterY() - a );
		// focuses
		Point2D focus1 = 
					new Point2D.Double( center.getX(), center.getY() + c );
		Point2D focus2 = 
					new Point2D.Double( center.getX(), center.getY() - c );
		// axis
		Line2D mainAxis = new Line2D.Double( vertex1, vertex2 );
		Line2D imgAxis = new Line2D.Double( 
			new Point2D.Double(	bounds.getCenterX() - b, bounds.getCenterY() ),
			new Point2D.Double(	bounds.getCenterX() + b, 
								bounds.getCenterY() ) );
		
		// translate hyperbola to center
		AffineTransform translation = AffineTransform.getTranslateInstance( 
									bounds.getCenterX(), bounds.getCenterY() ); 
		branch1.transform( translation );
		branch2.transform( translation );
		
		// return results
		Map<String,Object> map = new HashMap<>();
		map.put( "branch1", branch1 );
		map.put( "branch2", branch2 );
		map.put( "center", center );
		map.put( "vertex1", vertex1 );
		map.put( "vertex2", vertex2 );
		map.put( "focus1", focus1 );
		map.put( "focus2", focus2 );
		map.put( "bounds", bounds );
		map.put( "main_axis", mainAxis );
		map.put( "img_axis", imgAxis );
		
		return map;
	}
}
