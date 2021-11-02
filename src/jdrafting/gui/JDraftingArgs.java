package jdrafting.gui;

import static java.lang.System.out;

import java.awt.Color;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import cla.CLAParser;
import cla.ParamExecutor;
import cla.Parameter;

/**
 * Command line parser 
 * (uses CLA;  https://github.com/miguelalejandromorenobarrientos/CLA )
 * Note: Some parameter executors need an instance of Application and the 
 * others need to modify static fields before start GUI, these ones must
 * receive null as app
 */
public class JDraftingArgs extends CLAParser
{
	private Application app;
	
	public JDraftingArgs( Application a )
	{
		app = a;
		
		// parameter -help
		addDefaultHelpParameter( 
			out, 
			"\n" + Application.APPNAME + " console args description:\n", 
			"help", "-" );
		ParamExecutor defaultExecutor = getParameter( "help" ).getExecutor();
		getParameter( "help" ).setExecutor( (param,values) -> {
			defaultExecutor.execute( param, values );
			System.exit( 0 );
		});
		// parameter -lang
		addParameter( new Parameter(
			"lang",
			"-", "",
			"Select language"
			+ " (must exist 'language_??.properties' on resources folder)."
			+ " Usage: -lang language",
			false,
			1, 1,
			Stream.of( Locale.getAvailableLocales() )
				.map( Locale::toString )
				.collect( Collectors.toList() ),
			(param,values) -> {
				if ( app != null )  return;
				Application.locale = new Locale( values.get( 0 ) );
				Locale.setDefault( Application.locale );
				out.println( "lang set to " + Application.locale.getDisplayName() );
			},
			1 ) );
		// parameter -lookfeel
		addParameter( new Parameter( 
			"lookfeel",
			"-", "",
			"Select look and feel. Usage: -lookfeel installed_look&feel",
			false,
			1, 1,
			Stream.of( UIManager.getInstalledLookAndFeels() )
				.map( LookAndFeelInfo::getClassName )
				.collect( Collectors.toList() ),
			(param,values) -> {
				if ( app != null )  return;
				Application.lookAndFeelClassName = values.get( 0 );
				out.println( "L&F set to " + Application.lookAndFeelClassName );				
			},
			2 ) );
		// parameter -size
		addParameter( new Parameter( 
			"size", 
			"-", "",
			"Initial size. Usage: -size width height / size [iconified]",
			false,
			1, 2,
			null,
			(param,values) -> {
				if ( app == null )  return;
				int n = values.size();
				if ( n == 1 )
				{
					String value = values.get( 0 );
					if ( value.equals( "iconified" ) )
						app.setExtendedState( JFrame.ICONIFIED );
					else if ( value.equals( "normal" ) )
						app.setExtendedState( JFrame.NORMAL );
					else if ( value.equals( "horiz" ) )
						app.setExtendedState( JFrame.MAXIMIZED_HORIZ );
					else if ( value.equals( "vert" ) )
						app.setExtendedState( JFrame.MAXIMIZED_VERT );
				}
				else
				{
					app.setExtendedState( JFrame.NORMAL );
					int w = Integer.parseInt( values.get( 0 ) );
					int h = Integer.parseInt( values.get( 1 ) );
					app.setSize( w, h );
					out.printf( "size set to %sx%s\n", w, h );
				}
			},
			5 ) );
		// parameter -canvascolor
		addParameter( new Parameter( 
			"canvascolor", 
			"-", "",
			"Initial canvas color. "
			+ "Usage: -canvascolor html_color / -canvascolor red green blue",
			false,
			1, 3,
			null,
			(param,values) -> {
				if ( app == null )  return;
				int n = values.size();
				Color c;
				if ( n == 1 )  // html format
					c = Color.decode( values.get( 0 ) );
				else  // RGB
				{
					int r = Integer.parseInt( values.get( 0 ) );
					int g = Integer.parseInt( values.get( 1 ) );
					int b = 0;
					if ( n == 3 )
						b = Integer.parseInt( values.get( 2 ) );
					c = new Color( r, g, b );
				}
				app.setBackColor( c );
				out.println( "Canvas color set to " + c );
			},
			10 ) );
		// parameter -color
		addParameter( new Parameter( 
			"color", 
			"-", "",
			"Initial color. "
			+ "Usage: -canvascolor htmlColor / -canvascolor red green blue",
			false,
			1, 3,
			null,
			(param,values) -> {
				if ( app == null )  return;
				int n = values.size();
				Color c;
				if ( n == 1 )  // html format
					c = Color.decode( values.get( 0 ) );
				else  // RGB
				{
					int r = Integer.parseInt( values.get( 0 ) );
					int g = Integer.parseInt( values.get( 1 ) );
					int b = 0;
					if ( n == 3 )
						b = Integer.parseInt( values.get( 2 ) );
					c = new Color( r, g, b );
				}
				app.setColor( c );
				out.println( "Color set to " + c );
			},
			20 ) );
		// parameter -pointcolor
		addParameter( new Parameter( 
			"pointcolor", 
			"-", "",
			"Initial point color. "
			+ "Usage: -canvascolor htmlColor / -canvascolor red green blue",
			false,
			1, 3,
			null,
			(param,values) -> {
				if ( app == null )  return;
				int n = values.size();
				Color c;
				if ( n == 1 )  // html format
					c = Color.decode( values.get( 0 ) );
				else  // RGB
				{
					int r = Integer.parseInt( values.get( 0 ) );
					int g = Integer.parseInt( values.get( 1 ) );
					int b = 0;
					if ( n == 3 )
						b = Integer.parseInt( values.get( 2 ) );
					c = new Color( r, g, b );
				}
				app.setPointColor( c );
				out.println( "Point color set to " + c );
			},
			30 ) );
		// parameter -toolcolor
		addParameter( new Parameter( 
			"toolcolor", 
			"-", "",
			"Tool color. "
			+ "Usage: -canvascolor htmlColor / -canvascolor red green blue",
			false,
			1, 3,
			null,
			(param,values) -> {
				if ( app != null )  return;
				int n = values.size();
				Color c;
				if ( n == 1 )  // html format
					c = Color.decode( values.get( 0 ) );
				else  // RGB
				{
					int r = Integer.parseInt( values.get( 0 ) );
					int g = Integer.parseInt( values.get( 1 ) );
					int b = 0;
					if ( n == 3 )
						b = Integer.parseInt( values.get( 2 ) );
					c = new Color( r, g, b );
				}
				Application.toolMainColor = c;
				out.println( "Tool main color set to " + c );
			},
			30 ) );
		// parameter -flatness
		addParameter( new Parameter( 
			"flatness", 
			"-", "",
			"Accuracy in curves. Usage: -flatness value",
			false,
			1, 1,
			null,
			(param,values) -> {
				if ( app == null )  return;
				double flatness = Double.parseDouble( values.get( 0 ) );
				app.setFlatnessValue( flatness );
				out.println( "Flatness value set to " + flatness );
			},
			150 ) );		
		// parameter -angle
		addParameter( new Parameter( 
			"angle", 
			"-", "",
			"Tool angle start value. Usage: -angle start_angle",
			false,
			1, 1,
			null,
			(param,values) -> {
				if ( app == null )  return;
				double angle = Double.parseDouble( values.get( 0 ) );
				app.setAngle( angle );
				out.println( "Angle set to " + angle );
			},
			200 ) );
		// parameter -iconsize
		addParameter( new Parameter( 
			"iconsize", 
			"-", "",
			"Icons size (pixels by side). Usage: -iconsize smallsize largesize",
			false,
			1, 2,
			null,
			(param,values) -> {
				if ( app != null )  return;
				JDUtils.smallIconSize = Integer.parseInt( values.get( 0 ) );
				if ( values.size() == 2 )
					JDUtils.largeIconSize = Integer.parseInt( values.get( 1 ) );
			},
			300 ) );
		// parameter -file
		addParameter( new Parameter( 
			"file", 
			"-", "",
			"Load filename. Usage: -file filename",
			false,
			1, 1,
			null ) );
		// parameter -jme
		addParameter( new Parameter(
			"jme",
			":)", "",
			"enable jme",
			false,
			1, 1,
			Stream.of( new String[] { "enabled", "disabled" } )
			.collect( Collectors.toList() ),
			(param,values) -> {
				if ( app != null ) return;
				Application.jmeEnabled = values.get( 0 ).equals( "enabled" );
				out.println( "Easter egg jme loaded" );
			},
			400 ) );			
		// parameter -version
		addParameter( new Parameter( 
			"version",
			"-", "",
			"app version",
			false,
			0, 0,
			null,
			(param,values) -> {
				if ( app != null )  return;
				out.printf( "%s v%s, @%s\n", Application.APPNAME, 
							Application.VERSION, Application.COPYLEFT );
				System.exit( 0 );
			},
			Integer.MIN_VALUE + 1 ) );
	}

	public void setApp( Application app ) { this.app = app; }
}
