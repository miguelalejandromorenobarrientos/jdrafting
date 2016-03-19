package jdrafting.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("serial")
public class JDPoint extends Line2D.Double
{
	public JDPoint( Point2D point ) { super( point, point ); }
	public JDPoint( double x, double y ) { super( x, y, x, y ); }
	
	public Point2D.Double toPoint2Ddouble()
	{ 
		return new Point2D.Double( getX1(), getY1() );
	}
	
	@Override
	public Rectangle2D getBounds2D()
	{
		return new Rectangle2D.Double( 
				getX1(), getY1(), Math.ulp( 0. ), Math.ulp( 0. ) );
	}
}
