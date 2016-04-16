package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import jdrafting.gui.Application;

/**
 * Save exercise to media in JDrafting format 
 */
@SuppressWarnings("serial")
public class SaveAction extends AbstractAction
{
	private Application app;
	private boolean as;
	private static JFileChooser fileChooser;
	private int counter = 1;
	
	public SaveAction( Application app, boolean as )
	{
		this.app = app;
		this.as = as;
		
		putValue( NAME, getLocaleText( as ? "save_as" : "save" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "save_des" ) );
		if ( !as )
		{
			putValue( MNEMONIC_KEY, KeyEvent.VK_S );
			putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK ) );
		}
		putValue( SMALL_ICON, getSmallIcon( "save.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "save.png" ) );
		
		// create static file chooser dialog
		fileChooser = new JFileChooser();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// update file chooser L&F
		SwingUtilities.updateComponentTreeUI( fileChooser );

		// set png format as the unique filter
		Arrays.stream( fileChooser.getChoosableFileFilters() )
			  .forEach( f -> fileChooser.removeChoosableFileFilter( f ) );
		fileChooser.addChoosableFileFilter( new FileNameExtensionFilter(
										"JDrafting exercise (.jd)", "jd" ) );
		
		// update file name
		if ( app.getExercise().getTitle().trim().isEmpty() )
			fileChooser.setSelectedFile( 
									new File( "jd_" + ( counter++ ) + ".jd" ) );
		else
			fileChooser.setSelectedFile( 
				new File( camelCase( app.getExercise().getTitle() ) +".jd" ) );
			

		String saveFilename = app.getSaveFilename();

		// save dialog
		if ( as || saveFilename == null || saveFilename.length() == 0 )
		{
			if ( fileChooser.showSaveDialog( app )
				 == JFileChooser.APPROVE_OPTION )
			{
				saveFilename = fileChooser.getSelectedFile().getAbsolutePath();
			}
			else
				return;
		}
		
		// save exercise
		try ( FileOutputStream os = new FileOutputStream( saveFilename ) )
		{
			ObjectOutputStream oos = new ObjectOutputStream( os );
			oos.writeObject( app.getExercise() );
			app.setSaveFilename( saveFilename );
			app.initializeUndoRedoSystem();
		}
		catch ( IOException ex )
		{
			JOptionPane.showMessageDialog( 
					app, ex, "Error while saving", JOptionPane.ERROR_MESSAGE );
		}
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
