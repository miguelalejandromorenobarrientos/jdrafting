package jdrafting.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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

	public static int smallIconSize = 16;
	public static int largeIconSize = 24;
	
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
			.map( 
				s -> Character.toUpperCase( s.charAt( 0 ) ) + s.substring( 1 ) )
			.toArray( String[]::new );  // to versals

		return String.join( "", words );
	}
	
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
