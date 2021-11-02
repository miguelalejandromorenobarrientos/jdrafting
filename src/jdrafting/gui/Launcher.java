package jdrafting.gui;

import java.io.File;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;

import cla.ParsedParameterMap;

public class Launcher 
{
	/**
	 * Launch GUI
	 * @param args %1 filename to load
	 */
	public static void main( String[] args )
	{
		SwingUtilities.invokeLater( () -> {
			try
			{
				// parse and execute parameters before app instantiation
				// (some parameters like lang, lookfeel need to be executed 
				// before app intantiation)
				final JDraftingArgs argsParser = new JDraftingArgs( null );
				final ParsedParameterMap parsedMap = argsParser.parseAndExecute( args );
				
				// application instance
				final Application app = new Application();
	
				// parse and execute parameters after app instantiation
				argsParser.setApp( app );
				argsParser.execute( parsedMap );

				// launch app
				app.setVisible( true );
			
				// load file from console
				// (file load must be executed with a visible app)
				if ( parsedMap.containsParam( "file" ) )
					app.openFile( new File( parsedMap.getValues( "file" )[0] ) );
			}
			catch ( NoSuchElementException e )
			{
				System.out.printf( 
						"Launch error (%s: %s)\n-help for parameter info", 
						e.getClass().getSimpleName(), e.getMessage() );
				System.exit( -1 );
			}
		} );
	}
}
