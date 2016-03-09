package jdrafting.geom;

import static java.lang.Double.isNaN;
import static java.lang.Math.abs;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * Math utility operations
 */
public class JDMath
{
	/**
	 * Avoid instantiation
	 */
	private JDMath() {} 

	/**
	 * Round double to the nearest integer
	 * @param d value
	 * @return d rounded to int
	 */
	public static int nearInt( double d ) { return (int) ( d + 0.5 ); }
	
	/**
	 * Return vector p1 to p2
	 * @param p1 start point
	 * @param p2 dest point
	 * @return vector p1 to p2
	 */
	public static Point2D vector( Point2D p1, Point2D p2 )
	{
		return new Point2D.Double( p2.getX() - p1.getX(), 
								   p2.getY() - p1.getY() );
	}

	/**
	 * Return argument of a vector
	 * @param v vector
	 * @return the argument in -pi to pi range
	 */
	public static double vectorArg( Point2D v )
	{
		return Math.atan2( v.getY(), v.getX() );
	}
	
	/**
	 * Return normal vector using levorotation
	 * @param v vector
	 * @return normal vector
	 */
	public static Point2D normal( Point2D v )
	{
		return new Point2D.Double( -v.getY(), v.getX() );
	}
	
	/**
	 * Multiply vector by scalar
	 * @param v vector
	 * @param k scalar
	 * @return the product vector
	 */
	public static Point2D mulVector( Point2D v, double k )
	{
		return new Point2D.Double( v.getX() * k, v.getY() * k );
	}
	
	/**
	 * Sum to vectors
	 * @param v term1
	 * @param w term2
	 * @return sum vector
	 */
	public static Point2D sumVectors( Point2D v, Point2D w )
	{
		return new Point2D.Double( v.getX() + w.getX(), v.getY() + w.getY() );
	}
	
	/**
	 * Adjust a vector to a same direction vector with modulus size 
	 * @param v vector
	 * @param size modulus
	 * @return the adjusted vector
	 */
	public static Point2D adjustVectorToSize( Point2D v, double size )
	{
		return mulVector( unitary( v ), size );
	}
	
	/**
	 * Get unitary vector
	 * @param v vector
	 * @return the unitary vector v
	 */
	public static Point2D unitary( Point2D v )
	{
		double mod = v.distance( 0, 0 );
		return mod > 0.
			   ? new Point2D.Double( v.getX() / mod, v.getY() / mod )
			   : (Point2D) v.clone();
	}
	
	/**
	 * Get midpoint between p1 and p2
	 * @param p1 extreme point
	 * @param p2 extreme point
	 * @return midpoint
	 */
	public static Point2D midpoint( Point2D p1, Point2D p2 )
	{
		return new Point2D.Double( ( p1.getX() + p2.getX() ) / 2.,
								   ( p1.getY() + p2.getY() ) / 2. );
	}
	
	/**
	 * Get a position using center, angle and radius
	 * @param center center point
	 * @param ang angle in radians
	 * @param radius distance to center
	 * @return the new point
	 */
	public static Point2D pointRelativeToCenter( Point2D center, double ang, 
																double radius )
	{
		return new Point2D.Double( center.getX() + radius * Math.cos( ang ),
								   center.getY() + radius * Math.sin( ang ) );
	}
	
	/**
	 * Get angle between two segments
	 * @param l1 segment
	 * @param l2 segment
	 * @return angle in 0 to pi range
	 */
	public static double lineAng( Line2D l1, Line2D l2 )
	{
		Point2D v1 = new Point2D.Double( l1.getX2() - l1.getX1(),
										 l1.getY2() - l1.getY1() );
		Point2D v2 = new Point2D.Double( l2.getX2() - l2.getX1(),
				 l2.getY2() - l2.getY1() );
		
		//  |a||b|cos(ang)=a*b  ==>  ang = acos((a*b)/(|a||b|)
		double dot = v1.getX() * v2.getX() + v1.getY() * v2.getY();
		double modv1 = v1.distance( 0, 0 ), modv2 = v2.distance( 0, 0 );
		
		return Math.acos( dot / ( modv1 * modv2 ) );
	}
	
	/**
	 * Dot product
	 * @param v term1
	 * @param w term2
	 * @return the dot product v*w
	 */
	public static double scalar( Point2D v, Point2D w )
	{
		return v.getX() * w.getX() + v.getY() * w.getY();
	}
	
	/**
	 * Projection w over v<br>
	 * <i>p=|v*w/|v||</i>
	 * @param v vector
	 * @param w vector
	 * @return modulus of projection
	 */
	public static double projection( Point2D v, Point2D w )
	{
		return abs( scalar( v, w ) / v.distance( 0, 0 ) );
	}
	
	/**
	 * Shape length (poligonal length)
	 * @param shape the shape
	 * @param transform optional transform or null
	 * @return length
	 */
	public static double length( Shape shape, AffineTransform transform )
	{
		double length = 0.;		
		PathIterator path = shape.getPathIterator( transform );		
		double[] current = new double[6];
		double x = 0., y = 0.;
		path.currentSegment( current );
		double x0 = current[0], y0 = current[1];
		
		while ( !path.isDone() )
		{
			int type = path.currentSegment( current );
			
			if ( type == PathIterator.SEG_CLOSE 
				 && ( current[0] != x0 || current[1] == y0 ) )
			{
				length += Point2D.distance( x0, y0, current[0], current[1] );
				return length;
			}
			else if ( type != PathIterator.SEG_MOVETO )
				length += Point2D.distance( x, y, current[0], current[1] );
				
			x = current[0];
			y = current[1];
			
			path.next();
		}
		
		return length;
	}
	
	/////////////////////
	public static double[] solveQuadratic( double a, double b, double c )
	{
		double disc = Math.sqrt( b * b - 4 * a * c );
		
		return new double[] { ( -b + disc ) / ( 2 * a ), ( -b - disc ) / ( 2 * a ) };
	}
	
	
	
	
	/**
	 * @param d  -2*x1
	 * @param e  -2*y1
	 * @param f  x1^2+y1^2-r1^2
	 * @param d_  -2*x2
	 * @param e_  -2*y2
	 * @param f_  x2^2+y2^2-r2^2
	 * @return a vector with tangency points or null if circles are externals or overlapping
	 */
	public static Point2D.Double[] circlesIntersection( double d, double e, double f, double d_, double e_, double f_ )
	{
		double dd = d - d_, de = e_ - e, df = f_ - f;
		double dd2 = dd * dd, de2 = de * de, df2 = df * df;
		
		if ( de == 0 )  // same 'y' coordinates (e_-e=0)
		{
			double x = df / dd;
			double[] solY = solveQuadratic( 1, e, x * x + d * x + f );

			if ( isNaN( solY[0] ) )  // external or overlapping circles
				return null;

			if ( solY[0] == solY[1] )  // tangent circles
				return new Point2D.Double[] { new Point2D.Double( x, solY[0] ) };
			
			// secant circles
			return new Point2D.Double[] { new Point2D.Double( x, solY[0] ), new Point2D.Double( x, solY[1] ) };
		}
		
		double u = de2 + dd2;
		double v = de2 * d - 2 * df * dd + e * de * dd;
		double w = df2 - e * de * df + f * de2;
		
		double[] solX = solveQuadratic( u, v, w );
		
		if ( isNaN( solX[0] ) )  // external circles
			return null;

		if ( solX[0] == solX[1] )  // tangent circles
			return new Point2D.Double[] { new Point2D.Double( solX[0], ( dd * solX[0] - df ) / de ) };

		// secant circles
		return new Point2D.Double[] { new Point2D.Double( solX[0], ( dd * solX[0] - df ) / de ), new Point2D.Double( solX[1], ( dd * solX[1] - df ) / de ) };
	}
		
	public static Point2D.Double[] circlesIntersection( Point2D.Double center1, double radius1, Point2D.Double center2, double radius2 )
	{
		return circlesIntersection( -2 * center1.x, -2 * center1.y, center1.distanceSq( 0, 0 ) - radius1 * radius1, -2 * center2.x, -2 * center2.y, center2.distanceSq( 0, 0 ) - radius2 * radius2 );
	}
		
	public static Point2D.Double[] circleLineIntersection( double a, double b, double c, double d, double e, double f )
	{
		if ( b == 0 )  // vertical line
		{
			double x = -c / a;
			
			double[] solY = solveQuadratic( a * a, a * a * e, c * c + a * ( a * f - c * d ) );
			
			if ( isNaN( solY[0] ) )  // line and circle are external
				return null;
			
			if ( solY[0] == solY[1] )  // line is tangent
				return new Point2D.Double[] { new Point2D.Double( x, solY[0] ) };
			
			// line and circle are secants 
			return new Point2D.Double[] { new Point2D.Double( x, solY[0] ), new Point2D.Double( x, solY[1] ) };
		}
		
		double[] solX = solveQuadratic( a * a + b * b, 2 * a * c + b * ( b * d - a * e ), c * c + b * ( b * f - e * c ) );
		
		if ( isNaN( solX[0] ) )  // line and circle are external
			return null;
		
		if ( solX[0] == solX[1] )  // line is tangent
			return new Point2D.Double[] { new Point2D.Double( solX[0], ( -c - a * solX[0] ) / b ) };

		// line and circle are secants
		return new Point2D.Double[] { new Point2D.Double( solX[0], ( -c - a * solX[0] ) / b ), new Point2D.Double( solX[1], ( -c - a * solX[1] ) / b ) };
	}
	
	public static Point2D.Double[] circleLineIntersection( Point2D.Double p1, Point2D.Double p2, Point2D.Double center, double radius )
	{
		return circleLineIntersection( p2.y - p1.y, p1.x - p2.x, p2.x * p1.y - p1.x * p2.y, -2 * center.x, -2 * center.y, center.distanceSq( 0, 0 ) - radius * radius );
	}
	
	/**
	 * Get intersection point between two segments by general straight equation
	 * @param a (a)x+by+cz=0
	 * @param b ax+(b)y+cz=0
	 * @param c ax+by+(c)z=0
	 * @param a_ (a_)x+b_y+c_z=0
	 * @param b_ a_x+(b_)y+c_z=0
	 * @param c_ a_x+b_y+(c_)z=0
	 * @return intersection point or null if lines are parallel or coincident
	 */
	public static Point2D linesIntersection( double a, double b, double c,
											 double a_, double b_, double c_ )
	{
		double err = 1E-12;  // (solve accuracy bugs in vertical lines)
		// second line is vertical 
		if ( abs( b_ ) < err )  
		{
			// first line is vertical  (parallel or coincident)			
			if ( abs( b ) < err )
				return null;
			
			// lines are secant
			return new Point2D.Double( -c_ / a_,
									   ( a * c_ - a_ * c ) / ( a_ * b ) );
		}
		
		double div = b_ * a - b * a_;

		// lines are parallel or coincident
		if ( div == 0 )
			return null;		

		// lines are secant
		double x = ( b * c_ - b_ * c ) / div;
		
		return new Point2D.Double( x, ( -c_ - a_ * x ) / b_ );
	}
	
	/**
	 * Get intersection point between two segments by extreme points
	 * @param p1 first segment start
	 * @param p2 first segment end
	 * @param q1 second segment start
	 * @param q2 second segment end
	 * @return intersection point or null if lines are parallel or coincident
	 */
	public static Point2D linesIntersection( Point2D p1, Point2D p2, 
											 Point2D q1, Point2D q2 )
	{
		return linesIntersection(
						p2.getY() - p1.getY(),  // a
						p1.getX() - p2.getX(),  // b
						p2.getX() * p1.getY() - p1.getX() * p2.getY(),  // c
						q2.getY() - q1.getY(),  // a_
						q1.getX() - q2.getX(),  // b_
						q2.getX() * q1.getY() - q1.getX() * q2.getY() );  // c_
	}
		
	public static void main( String[] args )
	{
		java.awt.geom.Path2D.Double shape = new java.awt.geom.Path2D.Double();
		shape.moveTo( 0., 0. );
		shape.lineTo( 1., 0. );
		shape.lineTo( 1., 1. );
		shape.lineTo( 10., 1. );
		//shape.closePath();
		
		double length = length( shape, null );
		
		System.out.println( length );
	}
}
