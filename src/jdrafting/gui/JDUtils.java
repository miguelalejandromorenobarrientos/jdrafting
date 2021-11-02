package jdrafting.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import jdrafting.geom.JDMath;

/**
 * Helper methods for JDrafting
 */
final public class JDUtils
{
	/**
	 * Avoid instantiation
	 */
	private JDUtils() {}
	
	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		throw new CloneNotSupportedException();
	}

	/**
	 * Method like Kotlin elvis operator
	 * @param value value maybe null
	 * @param def value if null
	 * @return value if non null, or def value
	 */
	public static Object elvis( Object value, Object def )
	{
		return value != null ? value : def;
	}
	
	/**
	 * Specification of elvis for null or empty strings
	 * @param value string maybe null or empty
	 * @param def value if null
	 * @return value if non null or empty, or def value
	 */
	public static String elvis( String value, String def )
	{
		return value == null || value.isEmpty() ? def : value;  
	}
	
	public static StringBuilder pathToString( final Shape shape )
	{
		final PathIterator pit = shape.getPathIterator( null );
	    final StringBuilder sb = new StringBuilder();
	    final double[] current = new double[6];
		
		while ( !pit.isDone() )
		{			 
			sb.append( "{" );
		    final int type = pit.currentSegment( current );
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
			pit.next();
			sb.append( "}" );
		}	    
	    
		return sb;
	}
	
	public static Path2D removeUnnecessarySegments( final Shape shape )
	{
		final Path2D.Double path = new Path2D.Double();
		final double[] coords = new double[6],
					   lastMove = new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
					   lastLine = new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		final PathIterator pit = shape.getPathIterator(null);
		
		while ( !pit.isDone() )
		{
			switch( pit.currentSegment( coords ) )
			{
				case PathIterator.SEG_LINETO:
					if ( Math.hypot( lastLine[0] - coords[0], lastLine[1] - coords[1] ) < 1E-9 )
						break;
					path.lineTo( coords[0], coords[1] );
					System.arraycopy( coords, 0, lastLine, 0, 2 );
					break;
				case PathIterator.SEG_MOVETO:
					path.moveTo( coords[0], coords[1] );
					System.arraycopy( coords, 0, lastMove, 0, 2 );
					break;
				case PathIterator.SEG_QUADTO:
					path.quadTo( coords[0], coords[1], coords[2], coords[3] );
					break;
				case PathIterator.SEG_CUBICTO:
					path.curveTo( coords[0], coords[1], coords[2], coords[3], coords[4], coords[5] );
					break;
				case PathIterator.SEG_CLOSE:
					if ( Math.hypot( lastLine[0] - lastMove[0], lastLine[1] - lastMove[1] ) < 1E-9 )
						break;
					path.closePath();
					break;
			}
			pit.next();
		}
		
		return path;
	}
	
	/**
	 * Remove close segment from a shape into a Path2D.Double
	 * @param closedShape shape with close segment
	 * @return open path
	 */
	public static Path2D openShape( final Shape closedShape )
	{
		final Path2D.Double path = new Path2D.Double();
		final double[] coords = new double[6];
		final PathIterator pit = closedShape.getPathIterator(null);
	
		while ( !pit.isDone() )
		{
			switch( pit.currentSegment( coords ) )
			{
				case PathIterator.SEG_LINETO:
					path.lineTo( coords[0], coords[1] );
					break;
				case PathIterator.SEG_MOVETO:
					path.moveTo( coords[0], coords[1] );
					break;
				case PathIterator.SEG_QUADTO:
					path.quadTo( coords[0], coords[1], coords[2], coords[3] );
					break;
				case PathIterator.SEG_CUBICTO:
					path.curveTo( coords[0], coords[1], coords[2], coords[3], coords[4], coords[5] );
					break;
				case PathIterator.SEG_CLOSE:
					return path;
			}
			pit.next();
		}
		return path;
	}

	/**
	 * Add final line start-end to a path obtained from a shape-PathIterator
	 * @param shape shape
	 * @param addClose add final close segment
	 * @return path closed with line (and a optional close)
	 */
	public static Path2D.Double closeShapeWithLine( final Shape shape, boolean addClose )
	{
		final Path2D.Double path = new Path2D.Double();
		final PathIterator pit = shape.getPathIterator(null);
		final double[] coords = new double[6],
					   init = new double[6];
		pit.currentSegment( init );
	
		while ( !pit.isDone() )
		{
			switch( pit.currentSegment( coords ) )
			{
				case PathIterator.SEG_LINETO:
					path.lineTo( coords[0], coords[1] );
					break;
				case PathIterator.SEG_MOVETO:
					path.moveTo( coords[0], coords[1] );
					break;
				case PathIterator.SEG_QUADTO:
					path.quadTo( coords[0], coords[1], coords[2], coords[3] );
					break;
				case PathIterator.SEG_CUBICTO:
					path.curveTo( coords[0], coords[1], coords[2], coords[3], coords[4], coords[5] );
					break;
			}
			pit.next();
		}
		// join start-end
		path.lineTo( init[0], init[1] );
		if ( addClose )
			path.closePath();
		return path;
	}

	public static int smallIconSize = 16;
	public static int largeIconSize = 24;
	
	/**
	 * Get screen size, multiplied by factor
	 * @param ratioX factor width (value 1 for screen width)
	 * @param ratioY factor height (value 1 for screen height)
	 * @return rounded scaled screen size
	 */
	public static Dimension getScreenSize( float ratioX, float ratioY )
	{
		final Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return new Dimension( JDMath.nearInt( ssize.getWidth() * ratioX ), 
							  JDMath.nearInt( ssize.getHeight() * ratioY ) );
	}
	
	/**
	 * Get a text value in current language
	 * @param key the key in the language file
	 * @return the translated expression
	 */
	public static String getLocaleText( String key )
	{
		try
		{
			final ResourceBundle resource = ResourceBundle.getBundle( 
				"jdrafting.resources.language.language", Application.locale );
			/*if ( !locale.equals( resource.getLocale() ) )  // go to English
				throw new MissingResourceException( "fallback", "", key );*/

			return resource.getString( key );
		}
		catch ( MissingResourceException e )
		{
			// If all fails, default English
			// (not needed if "language.properties" resource exists)
			return ResourceBundle.getBundle(
					"jdrafting.resources.language.language", Locale.ENGLISH )
					.getString( key );
		}
	}
	
	/**
	 * Get mnemonic in current language
	 * @param key the key in the language file
	 * @return keycode for char
	 */
	public static int getLocaleMnemonic( String key )
	{
		return KeyEvent.getExtendedKeyCodeForChar( getLocaleText( key ).charAt( 0 ) );
	}
	
	/**
	 * Scale an image from the image resource folder to a specified size
	 * @param name name of the file (from image resource folder)
	 * @param width new width
	 * @param height new height
	 * @return the scaled image
	 */
	public static ImageIcon getScaledIco( String name, int width, int height )
	{
		return new ImageIcon( new ImageIcon( 
					Object.class
					.getResource( "/jdrafting/resources/images/" + name ) )
					.getImage()
					.getScaledInstance( width, height, Image.SCALE_SMOOTH ) );
	}

	/**
	 * Get small icons of the resource images. 
	 * See {@link #getScaledIco(String, int, int)}
	 * @see #getScaledIco(String, int, int)
	 * @param name filename
	 * @return scaled image
	 */
	public static ImageIcon getSmallIcon( String name )
	{
		return getScaledIco( name, smallIconSize, smallIconSize );
	}

	/**
	 * Get large icons of the resource images. 
	 * See {@link #getScaledIco(String, int, int)}
	 * @see #getScaledIco(String, int, int)
	 * @param name filename
	 * @return scaled image
	 */
	public static ImageIcon getLargeIcon( String name )	
	{
		return getScaledIco( name, largeIconSize, largeIconSize );
	}
	
	/**
	 * Load a custom mouse cursor by filename
	 * @param file filename
	 * @return a mouse cursor
	 */
	public static Cursor getCustomCursor( String file )
	{
		return Toolkit.getDefaultToolkit().createCustomCursor( 
						getScaledIco( "cursors/" + file, 32, 32 ).getImage(),
						new Point( 0, 0 ), "custom cursor" );
	}

	/**
	 * Converts a name to a CamelCase string only with alphanumeric characters
	 * @param text a name
	 * @return CamelCase representation
	 */
	public static String camelCase( String text )
	{
		String[] words = text.split( "[^\\w]+" );  // split into words
		words =	Stream.of( words )
					  .map( s -> Character.toUpperCase( s.charAt( 0 ) ) + s.substring( 1 ) )
					  .toArray( String[]::new );  // to versals

		return String.join( "", words );
	}
	
	/**
	 * Sets preferred, maximum, minimum size to a fixed size
	 * @param c component to fix
	 * @param w width
	 * @param h height
	 * @return the same component c
	 */
	public static Component fixSize( Component c, int w, int h )
	{
		c.setPreferredSize( new Dimension( w, h ) );
		c.setMaximumSize( c.getPreferredSize() );
		c.setMinimumSize( c.getPreferredSize() );
		
		return c;
	}
	
	/**
	 * Improve quality render hints
	 * @param g2 graphics
	 */
	public static void setHighQualityRender( Graphics2D g2 )
	{
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
			 		 		 RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
					 		 RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, 
		 			 	 	 RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, 
		 			 	 	 RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, 
		 			 	 	 RenderingHints.VALUE_COLOR_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_DITHERING, 
		 			 	 	 RenderingHints.VALUE_DITHER_ENABLE );
		g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION,
		   			 		 RenderingHints.VALUE_INTERPOLATION_BILINEAR);		
	}
	
}
