package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jdrafting.Exercise;
import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class NewAction extends AbstractAction
{
	private Application app;
	
	public NewAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "new" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "new_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_N );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "new.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "new.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// check for not saved file
		if ( app.undoManager.canUndo() )
		{
			int option = JOptionPane.showConfirmDialog( app,
													getLocaleText( "exit_msg" ), 
													getLocaleText( "new" ), 
													JOptionPane.YES_NO_OPTION );
			if ( option != JOptionPane.YES_OPTION )  return;  // cancel new file
		}
		
		// new exercise
		app.setExercise( new Exercise() );
	}
}
