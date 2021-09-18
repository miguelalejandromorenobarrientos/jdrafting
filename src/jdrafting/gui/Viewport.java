package jdrafting.gui;

import static java.lang.String.format;

import java.awt.geom.Rectangle2D;

/**
 * Logic viewport
 * @author Miguel Alejandro Moreno Barrientos
 * @since 0.1.0
 */
public class Viewport
{
	private double xmin, ymin, xmax, ymax;
	
	/**
	 * Viewport from x extremes and y extremes
	 * @param xmin min x
	 * @param xmax max x
	 * @param ymin min y
	 * @param ymax max y
	 */
	public Viewport( double xmin, double xmax, double ymin, double ymax )
	{
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}

	/**
	 * Default viewport [-1000,1000]x[-1000,1000]
	 */
	public Viewport()
	{
		this( -1000., 1000., -1000., 1000. );
	}
	
	/**
	 * Viewport from Rectangle2D
	 * @param r rectangle
	 */
	public Viewport( Rectangle2D r )
	{
		this( r.getMinX(), r.getMaxX(), r.getMinY(), r.getMaxY() );
	}
	
	public double getMinX() { return xmin; }
	public double getMaxX() { return xmax; }
	public double getMinY() { return ymin; }
	public double getMaxY() { return ymax; }
	
	public double getWidth() { return getMaxX() - getMinX(); }
	public double getHeight() { return getMaxY() - getMinY(); }
	public double getCenterX() { return ( getMinX() + getMaxX() ) / 2; }
	public double getCenterY() { return ( getMinY() + getMaxY() ) / 2; }
	
	/**
	 * Zoom over viewport in {@code (x,y)} point
	 * @param x coord x
	 * @param y coord y
	 * @param factor zoom factor; &gt;1 for zoom out, &lt;1 for zoom in
	 * @return this viewport zoomed
	 */
	public Viewport zoom( double x, double y, double factor )
	{
		double oldwidth = getWidth();
		double oldheight = getHeight();
		xmin = x + ( xmin - x ) * factor;
		ymin = y + ( ymin - y ) * factor;
		xmax = xmin + oldwidth * factor;
		ymax = ymin + oldheight * factor;
		
		return this;
	}
	
	/**
	 * Translate viewport
	 * @param dx delta x
	 * @param dy delta y
	 * @return this viewport translated
	 */
	public Viewport move( double dx, double dy )
	{
		xmin += dx;
		xmax += dx;
		ymin += dy;
		ymax += dy;
		
		return this;
	}
	
	/**
	 * Coordinates at this viewport from coordinates in other viewport
	 * @param x old x
	 * @param y old y
	 * @param other old viewport
	 * @return [x,y] at this viewport
	 */
	public double[] toThisViewport( double x, double y, Viewport other )
	{
		double nx = getMinX() + ( ( x - other.getMinX() ) / other.getWidth() )
								* getWidth(); 
		double ny = getMinY() + ( ( y - other.getMinY() ) / other.getHeight() )
								* getHeight(); 
		
		return new double[] { nx, ny };
	}
	
	/**
	 * Get viewport as java.awt.geom.Rectangle2D (Double)
	 * @return rectangle
	 */
	public Rectangle2D getAsRectangle()
	{
		return new Rectangle2D.Double( getMinX(), getMaxY(), getWidth(), getHeight() );
	}
	
	@Override
	public String toString()
	{
		return format( "[xmin:%s,xmax:%s,ymin:%s,ymax:%s,width:%s,height:%s]",
					   getMinX(), getMaxX(), getMinY(), getMaxY(), 
					   getWidth(), getHeight() );
	}
	
}
