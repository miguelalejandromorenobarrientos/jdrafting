package jdrafting.gui.controller.mouse;

import static java.lang.Math.PI;
import static jdrafting.geom.JDMath.mulVector;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.geom.JDMath.vectorArg;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Creates a cubic spline using mouse control
 * @version 0.1.11.1
 */
public class SplineListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "spline_cursor.png" ); 
	private CanvasPanel canvas;
	private Application app;

	private List<Point2D> points = new LinkedList<>();

	public SplineListener( CanvasPanel canvas )
	{
		super( canvas );

		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( JDUtils.getLocaleText( "txt_poly" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// finish curve capture
		if ( e.getClickCount() == 2 )
		{
			Path2D spline = (Path2D) getSpline( points )[0];
			
			// add polygon to exercise
			double flatness = spline.getBounds2D().getWidth() / app.getFlatnessValue();
			app.addShapeFromIterator( spline.getPathIterator( null, flatness ), "", 
									  getLocaleText( "new_spline" ), 
									  app.getColor(), app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
		// add new point to curve
		else
			points.add( logicMouse );
		
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( points.isEmpty() )  return;
		
		AffineTransform transform = canvas.getTransform();

		// mouse position on logic viewport
		Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

		// knots plus logicMouse
		List<Point2D> _points = new LinkedList<>( points );
		_points.add( logicMouse );
		
		// set style
		g2.setStroke( new BasicStroke( 1f ) );
		g2.setColor( Application.toolMainColor );

		// draw spline
		Object[] data = getSpline( _points );
		g2.draw( transform.createTransformedShape( (Path2D) data[0] ) );
		/*
		// draw control points		
		g2.setColor( Color.LIGHT_GRAY );
		for ( int i = 1; i < points.size(); i++ )
			g2.draw( transform.createTransformedShape( new Line2D.Double( points.get( i - 1 ), points.get( i ) ) ) );
		Point2D[][] ctrlPoints = (Point2D[][]) data[1];
		Iterator<Point2D> it = _points.iterator();
		for ( Point2D[] pair : ctrlPoints )
		{
			Point2D p;
			Point2D point = transform.transform( it.next(), null );
			if ( pair[0] != null )
			{
				p = transform.transform( pair[0], null );
				g2.setColor( Color.BLACK );
				g2.draw( new Line2D.Double( point.getX(), point.getY(), p.getX(), p.getY() ) );
				g2.setColor( Color.RED );
				g2.fill( new Rectangle2D.Double( p.getX() - 3, p.getY() - 3, 6, 6 ) );				
			}
			if ( pair[1] != null )
			{
				p = transform.transform( pair[1], null );
				g2.setColor( Color.BLACK );
				g2.draw( new Line2D.Double( point.getX(), point.getY(), p.getX(), p.getY() ) );
				g2.setColor( Color.RED );
				g2.fill( new Ellipse2D.Double( p.getX() - 4, p.getY() - 4, 8, 8 ) );
			}
		}*/
	}
	
	
	// --- HELPERS
	
	/**
	 * Create an spline from a set of points
	 * Note: the spline doesn't adjust control points globally
	 * and it doesn't follow any known algorythm (I think)
	 * TODO improve the spline algorythm
	 * @param points knots
	 * @return the spline as a path and an array with control points
	 */
	private Object[] getSpline( List<Point2D> points )
	{
		final Path2D path = new Path2D.Double();
		final CubicCurve2D arc = new CubicCurve2D.Double();
		final double ratio = 3.;
		final int n = points.size();		
		final Point2D[][] ctrlPoints = new Point2D[n][2];
		
		ctrlPoints[0] = new Point2D[] { 
			null, 
			sumVectors(	points.get( 0 ), mulVector( 
				vector( points.get( 0 ), points.get( 1 ) ), 1. / ratio ) ) };
		ctrlPoints[ n - 1 ] = new Point2D[] { 
			sumVectors( points.get( n - 1 ), mulVector( 
										vector( points.get( n - 2 ), 
										points.get( n - 1 ) ), -1. / ratio ) ), 
			null };
		
		for ( int i = 1; i < n - 1; i++ )
		{
			// three consecutive points		
			Point2D p[] = new Point2D[] { 
					points.get( i - 1 ), points.get( i ), points.get( i + 1 ) };

			// calculate control points
			Point2D v1 = vector( p[0], p[1] );
			Point2D v2 = vector( p[1], p[2] );
			double ang1 = vectorArg( v1 );
			double ang2 = vectorArg( v2 );
			double ang;
			if ( Math.abs( ang2 - ang1 ) > PI )
				ang = ( ang1 + ang2 ) / 2. + PI;
			else
				ang = ( ang1 + ang2 ) / 2.;			
			ctrlPoints[i][0] = pointRelativeToCenter( 
								p[1], ang + PI, v1.distance( 0, 0 ) / ratio );
			ctrlPoints[i][1] = pointRelativeToCenter( 
								p[1], ang, v2.distance( 0, 0 ) / ratio );
			
			// add cubic arc to path
			arc.setCurve( 
					p[0], ctrlPoints[ i - 1 ][1], ctrlPoints[ i ][0], p[1] );
			path.append( arc.getPathIterator(null), true );
		}
		// add last cubic arc
		arc.setCurve( points.get( n - 2 ), ctrlPoints[ n - 2 ][1], 
					  ctrlPoints[ n - 1 ][0], points.get( n - 1 ) );
		path.append( arc.getPathIterator(null), true );
		
		return new Object[] { path, ctrlPoints };
	}
}
