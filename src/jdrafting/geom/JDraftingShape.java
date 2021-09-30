package jdrafting.geom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * JDrafting shape model
 * @since 0.1.0
 * @version 0.1.11.2
 */
public class JDraftingShape implements Serializable
{
	// instance vars
	private Shape shape;
	private String name = "";
	private String description = "";
	private Color color = Color.BLACK, fill;
	private Point2D textPosition = new Point2D.Double( 0.5, 0.5 );
	private boolean isText = false;
	private Font font;
	transient private BasicStroke stroke = JDStrokes.PLAIN_ROUND.getStroke();
	
	// constructors
	public JDraftingShape() {}
	
	public JDraftingShape( Shape shape, Color color, Color fill, BasicStroke stroke )
	{ 
		this.shape = shape;
		this.color = color;
		this.fill = fill;
		this.stroke = stroke;
		name = toString();
	}
	
	public JDraftingShape( String name, String description, Shape shape, Color color, Color fill, 
						   BasicStroke stroke )
	{
		this( shape, color, fill, stroke );
		this.name = name;
		this.description = description;
	}

	public JDraftingShape( JDraftingShape other )
	{
		shape = other.getShape();
		name = other.name;
		description = other.description;
		stroke = other.stroke;
		color = other.color;
		fill = other.fill;
		font = other.font;
		isText = other.isText;
	}
	
	// I/O methods
	private void writeObject( ObjectOutputStream oos ) throws IOException
	{
		oos.defaultWriteObject();
		oos.writeFloat( stroke.getLineWidth() );
		oos.writeInt( stroke.getEndCap() );
		oos.writeInt( stroke.getLineJoin() );
		oos.writeFloat( stroke.getMiterLimit() );
		oos.writeObject( stroke.getDashArray() );
		oos.writeFloat( stroke.getDashPhase() );
	}

	private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException
	{
		ois.defaultReadObject();
		float lineWidth = ois.readFloat();
		int endCap = ois.readInt();
		int lineJoin = ois.readInt();
		float miterLimit = ois.readFloat();
		float[] dashArray = (float[]) ois.readObject();
		float dashPhase = ois.readFloat();
		stroke = new BasicStroke( lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase );
	}
	
	// getters
	public Shape getShape() { return shape; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public BasicStroke getStroke() { return stroke; }
	public Color getColor() { return color; }
	public Color getFill() { return fill; }
	public boolean isText() { return isText; }
	public Font getFont() { return font; }
	public Point2D getTextPosition() { return textPosition; }
	

	// setters
	public Shape setShape( Shape shape ) { this.shape = shape; return shape; }
	public String setName( String name ) { this.name = name; return name; }
	public String setDescription( String description )
	{ 
		this.description = description;
		return description;
	}
	public Stroke setStroke( BasicStroke stroke ) 
	{ 
		this.stroke = stroke;
		return stroke;
	}
	public Color setColor( Color color )
	{ 
		this.color = color;
		return color;
	}
	public void setFill( Color fill ) 
	{
		this.fill = fill;
	}
	public void setAsText( boolean isText ) 
	{
		this.isText = isText;
	}
	public void setFont( Font font ) 
	{
		this.font = font;
	}
	public void setTextPosition( Point2D textPosition )
	{
		this.textPosition = textPosition;
	}
	
	// methods
	public static JDraftingShape createFromIterator( PathIterator pit, String name, 
								   String description, Color color, Color fill, BasicStroke stroke )
	{
		final Path2D path = new Path2D.Double();
		path.append( pit, false );

		return new JDraftingShape( name, description, path, color, fill, stroke );
	}
	
	/**
	 * Translate shape
	 * @param dx delta x
	 * @param dy delta y
	 * @return current shape translated
	 */
	public JDraftingShape move( double dx, double dy )
	{
		setShape( AffineTransform.getTranslateInstance( dx, dy )
								 .createTransformedShape( getShape() ) );
		return this;
	}
	
	/**
	 * Vertex list from path iterator
	 * @return vertex list
	 */
	public List<Point2D> getVertex()
	{
		final List<Point2D> vertex = new ArrayList<>();
		
		final PathIterator pit = getShape().getPathIterator( null );
		double[] coords = new double[6];
		while ( !pit.isDone() )
		{
			int type = pit.currentSegment( coords );
			if ( type != PathIterator.SEG_CLOSE )
				vertex.add( new Point2D.Double( coords[0], coords[1] ) );
			pit.next();
		}
		
		return vertex;
	}
	
	/**
	 * Segment list from path iterator
	 * @return segment list
	 */
	public List<Line2D> getSegments()
	{
		List<Line2D> segments = new ArrayList<>();
		
		PathIterator pit = getShape().getPathIterator( null );
		double[] coords = new double[6];
		double oldX = Double.NaN, oldY = Double.NaN;
		while ( !pit.isDone() )
		{
			int type = pit.currentSegment( coords );
			if ( type == PathIterator.SEG_LINETO )
			{
				segments.add( 
						new Line2D.Double( oldX, oldY, coords[0], coords[1] ) );
				oldX = coords[0];
				oldY = coords[1];
			}
			else if ( type == PathIterator.SEG_MOVETO )
			{
				oldX = coords[0];
				oldY = coords[1];
			}
			else if ( type == PathIterator.SEG_QUADTO )
			{
				oldX = coords[2];
				oldY = coords[3];
			}
			else if ( type == PathIterator.SEG_CUBICTO )
			{
				oldX = coords[4];
				oldY = coords[5];
			}
			pit.next();
		}
		
		return segments;
	}
	
	/**
	 * Check whether shape is closed
	 * @param vertex vertex list
	 * @return {@code true} whether shape is closed
	 */
	public boolean isClosed( List<Point2D> vertex )
	{
		return vertex.get( 0 ).equals( vertex.get( vertex.size() - 1 ) );
	}
	
	/**
	 * Check whether shape is a point
	 * @param vertex vertex list
	 * @return {@code true} if so
	 */
	public boolean isPoint( List<Point2D> vertex )
	{
		return getSides( vertex ) == 0;
	}
	
	/**
	 * Check whether shape is a segment
	 * @param vertex vertex list
	 * @return {@code true} if so
	 */
	public boolean isSegment( List<Point2D> vertex )
	{
		return getSides( vertex ) == 1;
	}
	
	/**
	 * Number of sides of the shape
	 * @param vertex vertex list
	 * @return sides (0: point, 1: line)
	 */
	public int getSides( List<Point2D> vertex )
	{
		return vertex.size() == 2 && isClosed( vertex )
			   ? 0  // point
			   : vertex.size() - 1;
	}
	
	/**
	 * Get suitable font size for text type
	 * @param g2 canvas graphics
	 * @param transform canvas transform
	 * @return closest font size
	 */
	public Font adjustFontSizeToBounds( Graphics2D g2, AffineTransform transform )
	{
		// get text box bounds
		final Shape transShape = transform.createTransformedShape( getShape() );
		final double boundsWidth = transShape.getBounds2D().getWidth(),
					 boundsHeight = transShape.getBounds2D().getHeight();
		
		// split text description
		final String[] lines = getDescription().split("\n");
		
		// adjust temporary font size to box using binary search
		Font tmpfont;
		int minSize = 4, maxSize = font.getSize();
		while ( true )
		{
			int midpointSize = (minSize + maxSize) / 2;
			tmpfont = new Font( font.getFamily(), font.getStyle(), midpointSize );

			// finish, closest font to rectangle
			if ( Math.abs(maxSize - minSize) <= 1 )
				return tmpfont;

			// get temporary text bounds
			final FontMetrics fm = g2.getFontMetrics( tmpfont );

			Rectangle2D txtBounds = Stream.of( lines )  // max width line
						.max( (s1,s2) -> Double.compare( fm.getStringBounds( s1, g2 ).getWidth(), 
														 fm.getStringBounds( s2, g2 ).getWidth() ) )
						.map( s -> fm.getStringBounds( s, g2 ) )
						.get();
			
			txtBounds = new Rectangle2D.Double( txtBounds.getX(), txtBounds.getY(), 
												txtBounds.getWidth(), midpointSize * lines.length );

			// text inside rectangle
			if ( boundsWidth >= txtBounds.getWidth() && boundsHeight >= txtBounds.getHeight() )
				minSize = midpointSize;
			// overflow
			else
				maxSize = midpointSize;
		}
	}
	
	/**
	 * Short shape description text
	 * @return short description text
	 */
	public String toShortString()
	{
		return String.format( "[name=%s,description=%s]", 
				getName() != null && getName().length() > 0
				? getName()
				: "(unnamed)",
				getDescription() != null && getDescription().length() > 0
			    ? getDescription()
			    : "" );
	}
	
	@Override
	public String toString()
	{
	    double[] current = new double[6];
		PathIterator path = getShape().getPathIterator( null );
	    StringBuilder sb = new StringBuilder();
		
		sb.append( "[name="
			+ ( getName() != null && getName().length() > 0
				? getName()
				: "(unnamed)" )
			+ ( getDescription() != null && getDescription().length() > 0
			    ? ",description=" + getDescription()
			    : "" )
			+ ",color=" + getColor()
			+ ",fill=" + (getFill() != null ? getFill() : "")
			+ ",isText=" + isText()
			+ ",font=" + (getFont() != null ? getFont() : "")
			+ ",length=" + JDMath.length( getShape(), null ) + ",path=" );
		
		while ( !path.isDone() )
		{			 
			sb.append( "{" );
		    int type = path.currentSegment( current );
		    switch( type )
		    {
		    	case PathIterator.SEG_MOVETO:
		    		sb.append( "move to " + current[0] + ", " + current[1] );
		    		break;
			    case PathIterator.SEG_LINETO:
			    	sb.append( "line to " + current[0] + ", " + current[1] );
			    	break;
			    case PathIterator.SEG_QUADTO:
			    	sb.append( "quadratic to " + current[0] + ", " + current[1]
			    		+ ", " + current[2] + ", " + current[3] );
			    	break;
			    case PathIterator.SEG_CUBICTO:
			    	sb.append( "cubic to " + current[0] + ", " + current[1]
			    		+ ", " + current[2] + ", " + current[3] + ", "
			    		+ current[4] + ", " + current[5] );
			    	break;
			    case PathIterator.SEG_CLOSE:
			    	sb.append( "close" );
			    	break;
		    }
			path.next();
			sb.append( "}" + ( path.isDone() ? "" : ", " ) );
		}
		sb.append( "]" );
		
		return sb.toString();
	}

	private static final long serialVersionUID = -3554942998890931450L;
}
