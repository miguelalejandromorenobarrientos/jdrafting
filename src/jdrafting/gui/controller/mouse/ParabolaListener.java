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
 * Create parabola from bounds by mouse control 
 */
public class ParabolaListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "parabola_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private Point2D start;
	
	public ParabolaListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_parabola1" ) );
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
		
		// put first corner
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_parabola2" ) );
			canvas.repaint();
		}
		// finish rectangle bounds
		else
		{
			if ( addMore() )  // add complete transaction
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
						return "Redo parabola";
					}
					@Override
					public String getUndoPresentationName()
					{
						return "Undo parabola";
					}
				};
				
				Map<String,Object> map = getParabola( logicMouse );
				// add parabola, bounds, vertex, focus, directrix
				app.addShapeFromIterator( ( (Path2D) map.get( "parabola" ) )
												.getPathIterator( null ), "", 
										  getLocaleText( "new_parabola" ), 
										  app.getColor(), null, app.getStroke(), transaction );
				app.addShapeFromIterator( ( (Rectangle2D) map.get( "bounds" ) )
												.getPathIterator( null ), "", 
										  getLocaleText( "new_para_bounds" ), 
										  app.getColor(), null, app.getStroke(), transaction );
				app.addShapeFromIterator( ( (Line2D) map.get( "directrix" ) )
												.getPathIterator( null ), "", 
										  getLocaleText( "new_directrix" ), 
										  app.getColor(), null, app.getStroke(), transaction );
				app.addShapeFromIterator( ( new JDPoint( (Point2D) map.get( "vertex" ) ) )
												.getPathIterator( null ), "", 
										  getLocaleText( "new_para_vertex" ), 
										  app.getPointColor(), null, app.getPointStroke(), 
										  transaction );
				app.addShapeFromIterator( ( new JDPoint( (Point2D) map.get( "focus" ) ) )
												.getPathIterator( null ), "", 
										  getLocaleText( "new_focus" ), 
										  app.getPointColor(), null, app.getPointStroke(), 
										  transaction );
				
				transaction.end();
				app.undoSupport.postEdit( transaction );
			}
			else
				// add parabola to exercise
				app.addShapeFromIterator( ( (Path2D) getParabola( logicMouse ).get( "parabola" ) )
											.getPathIterator( null ), "", 
										  getLocaleText( "new_parabola" ), 
										  app.getColor(), null, app.getStroke() );

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
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );
			
			Map<String,Object> map = getParabola( logicMouse );
			
			// draw parabola
			g2.draw( transform.createTransformedShape( 
											(Path2D) map.get( "parabola" ) ) );
			// draw bounds
			g2.setStroke( 
					JDStrokes.getStroke( JDStrokes.DASHED.getStroke(), 1f ) );
			g2.draw( transform.createTransformedShape( 
										(Rectangle2D) map.get( "bounds" ) ) );
			// draw directrix
			g2.draw( transform.createTransformedShape(
											(Line2D) map.get( "directrix" ) ) );
			// draw vertex
			g2.setStroke( new BasicStroke( 4f ) );
			g2.draw( transform.createTransformedShape( 
							new JDPoint( (Point2D) map.get( "vertex" ) ) ) );
			// draw focus
			g2.draw( transform.createTransformedShape( 
								new JDPoint( (Point2D) map.get( "focus" ) ) ) );
		}
	}
	
	// --- HELPERS ---

	// check modifiers
	private boolean isSquare() { return mouse().isShiftDown(); }
	private boolean addMore() { return mouse().isControlDown(); }
	
	/**
	 * Gets the parabola
	 * @param logicMouse
	 * @return the parabola in the logic viewport
	 */
	private Map<String,Object> getParabola( Point2D logicMouse )
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
		
		// create parabola rectangle bounds
		Rectangle2D rect = 
				new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
		if ( isSquare() )  // create square bounds
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
		
		// create parabola
		Path2D parabola = new Path2D.Double();

		double w = rect.getWidth(), h = rect.getHeight();		
		double a = 4 * h / ( w * w );  // y=ax^2
		int n = (int) Math.sqrt( app.getFlatnessValue() );

		for ( int i = 0; i < n; i++ )
		{
			double xp = -w / 2. + w / ( n - 1 ) * i;
			double yp = a * xp * xp;
			if ( i != 0 )
				parabola.lineTo( xp, yp );
			else
				parabola.moveTo( xp, yp );
		}
		
		// create vertex
		Point2D vertex = new Point2D.Double( 
					( rect.getMinX() + rect.getMaxX() ) / 2., rect.getMinY() );
		// create focus. [ p:focal distance -> a=1/4p -> p=1/(4a) ]
		Point2D focus = 
			new Point2D.Double( vertex.getX(), rect.getMinY() + 1 / ( 4 * a ) );
		// create directrix
		Line2D directrix = new Line2D.Double(
							rect.getMinX(), rect.getMinY() - 1 / ( 4 * a ),
							rect.getMaxX(), rect.getMinY() - 1 / ( 4 * a ) );
		
		// translate parabola to vertex
		parabola.transform(	AffineTransform.getTranslateInstance( 
											vertex.getX(), vertex.getY() ) );
		
		// return results
		Map<String,Object> map = new HashMap<>();
		map.put( "parabola", parabola );
		map.put( "vertex", vertex );
		map.put( "focus", focus );
		map.put( "bounds", rect );
		map.put( "directrix", directrix );
		
		return map;
	}
}
