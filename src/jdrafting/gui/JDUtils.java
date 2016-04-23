package jdrafting.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import jdrafting.geom.JDMath;

/**
 * Helper methods for JDrafting
 */
public class JDUtils
{
	/**
	 * Avoid instantiation
	 */
	private JDUtils() {}

	/**
	 * Get screen size, multiplied by factor
	 * @param ratioX factor width (value 1 for screen width)
	 * @param ratioY factor height (value 1 for screen height)
	 * @return
	 */
	public static Dimension getScreenSize( float ratioX, float ratioY )
	{
		Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
		
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
			ResourceBundle resource = ResourceBundle.getBundle( 
				"jdrafting.resources.language.language", Application.locale );
			/*if ( !locale.equals( resource.getLocale() ) )  // go to English
				throw new MissingResourceException( "fallback", "", key );*/

			return resource.getString( key );
		}
		catch ( MissingResourceException e )  // Default English
		{
			return ResourceBundle.getBundle(
					"jdrafting.resources.language.language", Locale.ENGLISH )
					.getString( key );
		}
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
		return getScaledIco( name, 16, 16 );
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
		return getScaledIco( name, 32, 32 );
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
	 * @param name a name
	 * @return CamelCase representation
	 */
	public static String camelCase( String name )
	{
		String[] array = name.split( "[^a-zA-Z0-9]" );
		array =	Stream.of( array )
			.filter( s -> !s.trim().isEmpty() )
			.map( 
				s -> Character.toUpperCase( s.charAt( 0 ) ) + s.substring( 1 ) )
			.toArray( String[]::new );
		name = String.join( "", array );
		
		return name;
	}
}
