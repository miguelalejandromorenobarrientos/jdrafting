package jdrafting.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jdrafting.Exercise;
import jdrafting.geom.JDMath;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.controller.mouse.AbstractCanvasMouseListener;
import jdrafting.gui.controller.mouse.HandListener;

/**
 * Panel to draw application exercises 
 */
@SuppressWarnings("serial")
public class CanvasPanel extends JPanel
{
	// Aspect Ratio constants
	public static final int ADJUST_CANVAS = 1, SAME_RATIO = 2; 
	
	// this comparator order points at the beginning, and the other
	// shapes using the reverse order in the exercise
	private final Comparator<JDraftingShape> zBufferComparator =
											new Comparator<JDraftingShape>() {
		@Override
		public int compare( JDraftingShape s1, JDraftingShape s2 )
		{
			boolean s1isPoint = s1.isPoint( s1.getVertex() );
			boolean s2isPoint = s2.isPoint( s2.getVertex() );
			if ( s1isPoint && s2isPoint )
				return 0;
			else if ( s1isPoint )
				return -1;
			else if ( s2isPoint )
				return 1;
			
			return -Integer.compare( 
				getApplication().getExercise().getShapes().indexOf( s1 ),
				getApplication().getExercise().getShapes().indexOf( s2 ) );
		}		
	};	
	
	// instance vars
	private Application app;
	private Viewport viewport;  // logic viewport
	private int aspectRatio = SAME_RATIO;
	private double borderPrecision = 8.; // min precision pixel to select shapes
	
	private static Font textFont = new Font( Font.MONOSPACED, Font.BOLD, 16 );
	
	private AbstractCanvasMouseListener canvasListener;
	private MoveCanvasThread movementThread;
	
	/**
	 * New canvas
	 * @param app Application parent
	 * @param viewport current logic viewport
	 */
	public CanvasPanel( Application app, Viewport viewport )
	{
		this.app = app;
		this.viewport = viewport;

		// register listeners
		setCanvasListener( new HandListener( this ) );
		
		// set background color
		setOpaque( true );
		//setBackground( Color.WHITE );
		
		// viewport movement when mouse over canvas border
		movementThread = new MoveCanvasThread();
		movementThread.start();
	}
	
	
	public Application getApplication() { return app; }	
	
	/////////////////////
	// GETTERS/SETTERS //
	/////////////////////
	
	/**
	 * Logic viewport of the canvas
	 * @return logiv viewport
	 */
	public Viewport getViewport() { return viewport; }
	
	/**
	 * Set the logic viewport of the canvas
	 * @param viewport logic viewport
	 */
	public void setViewport( Viewport viewport ) { this.viewport = viewport; }

	/**
	 * Get auto-movement thread
	 * @return movement thread instance
	 */
	public MoveCanvasThread getMovementThread() { return movementThread; }
	
	
	
	
	public AffineTransform getTransform()
	{
		return getTransform( getViewport(), getCanvasViewport() );
	}

	public static AffineTransform getTransform( Viewport orig, Viewport dest )
	{
		AffineTransform transform = new AffineTransform();
		// invert Y-axis
		transform.scale( 1, -1 );
		// put origin in left-lower corner
		transform.translate( 0, -dest.getHeight() );
		
		// transform from logic viewport to canvas viewport
		double sx = dest.getWidth() / orig.getWidth();
		double sy = dest.getHeight() / orig.getHeight();
		double tx = dest.getMinX() - orig.getMinX();
		double ty = dest.getMinY() - orig.getMinY();
		transform.scale( sx, sy );
		transform.translate( tx, ty );
		
		return transform;
	}
	
	public static AffineTransform getInverseTransform( AffineTransform at )
	{
		try
		{
			return at.createInverse();
		}
		catch ( NoninvertibleTransformException ex ) { return null; }
	}

	public AffineTransform getInverseTransform()
	{
		return getInverseTransform( getTransform() );
	}
	
	@Override
	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		
		if ( app.getExercise() == null )
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		
		// draw exercise shapes
		drawExercise( g2, getTransform(), app.getExercise(),
					  app.getSelectedShapes(), app.isVisibleNames() );
		
		// draw frame info
		if ( !app.getExercise().isIndexAtEnd() )
		{
			int fontSize = 22;
			g2.setFont( new Font( Font.SANS_SERIF, Font.BOLD, fontSize ) );
			String frameInfo = "Frame "
				+ app.getExercise().getFrameIndex()
				+ "/"
				+ app.getExercise().size();
			int width = getFontMetrics( g2.getFont() ).stringWidth( frameInfo );
			g2.setColor( Color.LIGHT_GRAY );
			g2.drawString( frameInfo, getWidth() - width - 4, fontSize + 2 );
			g2.setColor( Color.BLACK );
			g2.drawString( frameInfo, getWidth() - width - 6, fontSize );
		}
		
		// draw tool graphics
		canvasListener.paintTool( g2 );
	}

	public static void drawExercise( Graphics2D g2, AffineTransform transform, 
				Exercise exercise, Set<JDraftingShape> selected, boolean text )
	{
		// draw non-selected shapes
		for ( JDraftingShape jdshape : exercise.getFramesUntilIndex() )
		{
			if ( selected.contains( jdshape ) )
				continue;
			// transform shape to canvas
			Shape transShape = transform.createTransformedShape(
														jdshape.getShape() );
			// set style
			g2.setStroke( jdshape.getStroke() );
			g2.setColor( jdshape.getColor() );

			// draw shape
			g2.draw( transShape );
		}
		// draw selected shapes
		for ( JDraftingShape jdshape: selected )
		{
			// transform shape to canvas
			Shape transShape = transform.createTransformedShape(
														jdshape.getShape() );
			
			// set style
			BasicStroke stroke = jdshape.getStroke();
			if ( stroke.getLineWidth() < 8f )
				g2.setStroke( new BasicStroke( 8f, stroke.getEndCap(), 
												stroke.getLineJoin() ) );
			else
				g2.setStroke( stroke );
			g2.setColor( new Color( 200, 32, 32, 222 ) );
			
			// draw shape
			g2.draw( transShape );
		}
		// draw identifiers
		if ( text )
		{
			for ( JDraftingShape jdshape : exercise.getFramesUntilIndex() )
			{
				if ( jdshape.getName() != null  // non empty or null strings
					 && jdshape.getName().length() > 0 )
				{
					Color c = selected.contains( jdshape )
							  ? new Color( 200, 32, 32, 222 )
							  : jdshape.getColor();
					g2.setFont( textFont );
					g2.setColor( new Color( 0, 0, 0, 200 ) );
					
					Rectangle2D bounds =
									getShapeCanvasBounds( jdshape, transform);
			
					double textX = bounds.getCenterX() 
						+ jdshape.getTextPosition().getX() * bounds.getWidth();
					double textY = bounds.getCenterY() 
					   	- jdshape.getTextPosition().getY() * bounds.getHeight();
					float x = (float) textX, y = (float) textY;
					
					g2.drawString( jdshape.getName(), x + 1, y + 1 );
					g2.setColor( new Color( 
								c.getRed(), c.getGreen(), c.getBlue(), 150 ) );
					g2.drawString( jdshape.getName(), x, y );
				}
			}
		}
	}
	
	public Viewport getCanvasViewport()
	{
		switch( aspectRatio )
		{
			case ADJUST_CANVAS:
				return new Viewport( 0, getWidth() - 1, 0, getHeight() - 1 );
			case SAME_RATIO:
				Viewport logicView = getViewport();
				double canvasRatio = (double) getWidth() / getHeight();
				double logicRatio =
								logicView.getWidth() / logicView.getHeight();
				if ( canvasRatio >= logicRatio )
					return new Viewport( 
									0, getWidth() / canvasRatio * logicRatio, 
									0, getHeight() );
				else
					return new Viewport( 
									0, getWidth(), 
									0, getHeight() * canvasRatio / logicRatio );
			default:
				return null;
		}
	}
	
	/**
	 * Get all shapes near a canvas point (borderPrecision value)
	 * @param canvasPoint point on canvas viewport
	 * @return a list with shapes near canvas point
	 */
	public List<JDraftingShape> getAllShapesAtCanvasPoint( Point2D canvasPoint )
	{
		double x = canvasPoint.getX(), y = canvasPoint.getY();
		List<JDraftingShape> shapes = new ArrayList<>();
		
		for ( JDraftingShape jdshape : app.getExercise().getShapes() )
		{
			double[] coords = new double[6];
			PathIterator pit = jdshape.getShape().getPathIterator( null ); 
			double lineWidth = jdshape.getStroke().getLineWidth();
			double border = Math.max( borderPrecision, lineWidth ); 
			Shape mouseRect = getInverseTransform().createTransformedShape( 
				new Rectangle2D.Double( x - border / 2., 
										y - border / 2., 
										border, 
										border ) );		
			pit.currentSegment( coords );
			pit.next();
			Point2D start = new Point2D.Double( coords[0], coords[1] );
			Point2D end;
			while ( !pit.isDone() )
			{
				pit.currentSegment( coords );
				pit.next();
				end = new Point2D.Double( coords[0], coords[1] );
				Line2D line = new Line2D.Double( start, end );
				start = end;
				if ( line.intersects( mouseRect.getBounds2D() ) )
				{
					shapes.add( jdshape );
					break;
				}
			}
		}
		
		return shapes;
	}
	
	/**
	 * Get shape with max zbuffer near canvas point (borderPrecision value)
	 * @param canvasPoint point on canvas viewport
	 * @return the shape with max zbuffer near canvas point
	 */
	public JDraftingShape getShapeAtCanvasPoint( Point2D canvasPoint )
	{
		double x = canvasPoint.getX(), y = canvasPoint.getY();
		
		AffineTransform inverse = getInverseTransform();
		
		// iterate over every shape in custom order
		List<JDraftingShape> reordered = 
			app.getExercise().getFramesUntilIndex()
			.stream()
			.sorted( zBufferComparator )
			.collect( Collectors.toList() );
		for ( JDraftingShape jdshape : reordered )
		{
			double[] coords = new double[6];
			PathIterator pit = jdshape.getShape().getPathIterator( null ); 
			double lineWidth = jdshape.getStroke().getLineWidth();
			double border = Math.max( borderPrecision, lineWidth ); 
			Shape mouseRect = inverse.createTransformedShape( 
				new Rectangle2D.Double( x - border / 2., y - border / 2., 
										border, border ) );		

			pit.currentSegment( coords );
			pit.next();
			Point2D start = new Point2D.Double( coords[0], coords[1] );
			Point2D end;
			while ( !pit.isDone() )
			{
				pit.currentSegment( coords );
				pit.next();
				end = new Point2D.Double( coords[0], coords[1] );
				Line2D line = new Line2D.Double( start, end );
				start = end;
				if ( line.intersects( mouseRect.getBounds2D() ) )
					return jdshape;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the position of a point near to mouse position or the logic mouse
	 * position if there isn't near points
	 * @param canvasPoint canvas position
	 * @return the logic position of the point or the logic mouse position
	 */
	public Point2D adjustToPoint( Point2D canvasPoint )
	{
		// check for shapes at mouse position
		JDraftingShape jdshape = getShapeAtCanvasPoint( canvasPoint ); 
		if ( jdshape != null )
		{
			// check whether shape is a point
			List<Point2D> vertex = jdshape.getVertex();
			if ( jdshape.isPoint( vertex ) )
				return vertex.get( 0 );  // return point
		}
		// no point, return logic mouse position
		return getInverseTransform().transform( canvasPoint, null ); 
	}
	
	/**
	 * Get the position of a vertex near to mouse position (no points)
	 * @param canvasPoint canvas position
	 * @return the logic position of the vertex or null if doesn't exist
	 */
	public Point2D adjustToVertex( Point2D canvasPoint )
	{
		// check for shapes at mouse position
		JDraftingShape jdshape = getShapeAtCanvasPoint( canvasPoint ); 
		if ( jdshape != null )
		{
			List<Point2D> vertex = jdshape.getVertex();
			if ( jdshape.isPoint( vertex ) )  // don't adjust to points
				return null;  
			double x = canvasPoint.getX(), y = canvasPoint.getY();
			double lineWidth = jdshape.getStroke().getLineWidth();
			double border = Math.max( borderPrecision, lineWidth ); 
			Shape mouseRect = getInverseTransform().createTransformedShape( 
				new Rectangle2D.Double( x - border / 2., y - border / 2., 
										border, border ) );		
			
			for ( Point2D v : vertex )				
				if ( mouseRect.contains( v ) )
					return v;  // return a vertex near canvas point 
		}
		// no vertex
		return null; 
	}
	
	public JDraftingShape getShapeWithNameAtCanvasPoint( Point2D canMouse )
	{
		AffineTransform transform = getTransform();

		// iterate over every shape with name defined in custom order
		List<JDraftingShape> reordered = 
			app.getExercise().getFramesUntilIndex()
			.stream()
			.filter( s -> s.getName() != null && s.getName().length() > 0 )
			.sorted( zBufferComparator )
			.collect( Collectors.toList() );
		
		for ( JDraftingShape jdshape : reordered )
		{
			Rectangle2D bounds = getShapeCanvasBounds( jdshape, transform );

			Point2D namePosition = getNamePosition( bounds, jdshape );

			FontMetrics metrics = getGraphics().getFontMetrics( textFont );
			int w = metrics.stringWidth( jdshape.getName() );
			int h = textFont.getSize() + metrics.getDescent();
			
			if ( new Rectangle2D.Double( namePosition.getX(),
						namePosition.getY() - h + metrics.getDescent(),	w, h )
						.contains( canMouse ) )
				return jdshape;
		}
		
		return null;
	}
	
	public static Point2D getNamePosition( Rectangle2D bounds, 
										   JDraftingShape jdshape )
	{
		return new Point2D.Double(
					bounds.getCenterX() 
					+ jdshape.getTextPosition().getX() * bounds.getWidth(),
					bounds.getCenterY() 
					- jdshape.getTextPosition().getY() * bounds.getHeight() );
	}
	
	/**
	 * Get intersection points between two shapes
	 * @param jdshape1 one shape
	 * @param jdshape2 should be a different shape
	 * @return a list with the intersection points (can be empty)
	 */
	public static List<Point2D> intersectionPoints( JDraftingShape jdshape1,
											 		JDraftingShape jdshape2 )
	{
		List<Line2D> segments1 = jdshape1.getSegments();
		List<Line2D> segments2 = jdshape2.getSegments();
		List<Point2D> intersections = new LinkedList<>();
		if ( jdshape1.isPoint( jdshape1.getVertex() ) 
			 || jdshape2.isPoint( jdshape2.getVertex() ) )
			return intersections; // one of them is a point, ignore intersection
		
		for ( Line2D seg1 : segments1 )
			for ( Line2D seg2 : segments2 )
				// if lines intersect, add joins
				if ( seg1.intersectsLine( seg2 ) )
				{
					Point2D join = JDMath.linesIntersection(
												seg1.getP1(), seg1.getP2(),
												seg2.getP1(), seg2.getP2() );
					if ( join != null )  // (avoid coincidents)
						intersections.add( join );
				}

		return intersections;
	}
	
	/**
	 * Get the nearest intersection to mouse
	 * @param mousePosition canvas mouse position
	 * @return the nearest intersection or null if there is no intersections
	 */
	public Point2D adjustPointToIntersection( Point2D mousePosition )
	{
		List<JDraftingShape> shapes =
									getAllShapesAtCanvasPoint( mousePosition ); 
		if ( shapes.size() < 2 )
			return null;
		
		Point2D logicMouse =
						getInverseTransform().transform( mousePosition, null );
		JDraftingShape jdshape1 = shapes.get( 0 );
		JDraftingShape jdshape2 = shapes.get( 1 );
		
		List<Point2D> intersections = intersectionPoints( jdshape1, jdshape2 );

		return intersections // nearest intersection to mouse
			   .stream()
			   .min( ( point1, point2 ) -> Double.compare(
											point1.distanceSq( logicMouse ),
											point2.distanceSq( logicMouse ) ) )
			   .orElse( null );
	}

	public static Rectangle2D getExerciseBounds( Exercise exercise, 
												 Viewport orig, Viewport dest )
	{
		if ( !exercise.getShapes().isEmpty() )
		{
			AffineTransform transform = getTransform( orig, dest );
			
			Rectangle2D enclosure = getShapeCanvasBounds( 
									exercise.getShapes().get( 0 ), transform ); 
			
			for ( JDraftingShape jdshape : exercise.getShapes() )
				enclosure = enclosure.createUnion( getShapeCanvasBounds(
														jdshape, transform ) );
			
			return getInverseTransform( transform )
							.createTransformedShape( enclosure ).getBounds2D();
		}
		
		return null;
	}
	
	public static Rectangle2D getShapeCanvasBounds( JDraftingShape jdshape, 
													AffineTransform transform )
	{
		Rectangle2D bounds = transform.createTransformedShape( 
							jdshape.getShape().getBounds2D() ).getBounds2D();
		float lineWidth = jdshape.getStroke().getLineWidth();

		return new Rectangle2D.Double( bounds.getMinX() - lineWidth / 2.,
									   bounds.getMinY() - lineWidth / 2., 
									   bounds.getWidth() + lineWidth,
									   bounds.getHeight() + lineWidth );
	}
	
	/**
	 * Load a custom mouse cursor by filename
	 * @param file filename
	 * @return a mouse cursor
	 */
	public static Cursor getCustomCursor( String file )
	{
		return Toolkit.getDefaultToolkit().createCustomCursor( 
			Application.getScaledIco( "cursors/" + file, 32, 32 ).getImage(),
			new Point( 0, 0 ), "custom cursor" );
	}
	
	/**
	 * Get current canvas mouse listener (segment tool, hand,...)
	 * @return current listener
	 */
	public AbstractCanvasMouseListener getCanvasListener()
	{
		return canvasListener;
	}
	
	/**
	 * Set new canvas mouse listener
	 * @param listener
	 */
	public void setCanvasListener( AbstractCanvasMouseListener listener )
	{
		// remove previous listener
		removeMouseListener( canvasListener );
		removeMouseMotionListener( canvasListener );
		removeMouseWheelListener( canvasListener );
		// update listener
		canvasListener = listener;
		addMouseListener( listener );
		addMouseMotionListener( listener );
		addMouseWheelListener( listener );
		
		repaint();  // (needed to refresh all tools graphs)
	}	
	
	/**
	 * Move viewport when mouse near of the canvas bounds 
	 */
	class MoveCanvasThread extends Thread
	{
		private static final int MAX_WAIT = 40;
		private int wait = MAX_WAIT;
		private boolean stop = false;

		public void stopMe() { stop = true; }
		
		@Override
		public void run() {
			while ( !stop )
			{
				try { Thread.sleep( wait < MAX_WAIT ? 20 : 200 ); }
				catch( InterruptedException e ) { return; }
				
				if ( stop )  return;
				
				SwingUtilities.invokeLater( () -> {
					
					Point mouse = getMousePosition();
					if ( mouse == null || getCanvasListener() instanceof HandListener )
					{
						wait = MAX_WAIT;
						return;
					}							
					int border = 32;
					Rectangle inner = new Rectangle( border, border, 
						getWidth() - 2 * border, getHeight() - 2 * border );
					if ( inner.contains( mouse ) )
					{
						wait = MAX_WAIT;
						return;
					}
					
					if ( wait > 0 )
					{
						wait--;
						return;
					}
					
					double factor = 100.;
					Viewport oldView = getViewport();
					double dw = oldView.getWidth() / factor;
					double dh = oldView.getHeight() / factor;
					if ( mouse.getY() > getHeight() - border )
					{
						setViewport(
							new Viewport( oldView.getMinX(), 
										  oldView.getMaxX(),
										  oldView.getMinY() - dh, 
										  oldView.getMaxY() - dh ) );
					}
					else if ( mouse.getY() <= border )
					{
						setViewport( 
							new Viewport( oldView.getMinX(), 
									      oldView.getMaxX(), 
									      oldView.getMinY() + dh, 
									      oldView.getMaxY() + dh ) );
					}
					oldView = getViewport();
					if ( mouse.getX() > getWidth() - border )
					{
						setViewport( 
							new Viewport( oldView.getMinX() + dw, 
										  oldView.getMaxX() + dw, 
										  oldView.getMinY(), 
										  oldView.getMaxY() ) );
					}
					else if ( mouse.getX() <= border )
					{
						setViewport(
							new Viewport( oldView.getMinX() - dw, 
										  oldView.getMaxX() - dw, 
										  oldView.getMinY(), 
										  oldView.getMaxY() ) );
					}
					repaint();
				} ); // lambda
			} // while 
		}  // method run
	} // class MoveCanvasThread
}
