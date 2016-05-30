package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;

@SuppressWarnings("serial")
public class RewindAction extends AbstractAction
{
	private Application app;
	
	public RewindAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "rewind" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "rewind_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_rewind" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "rewind.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "rewind.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getExercise().setFrameIndex( app.getExercise().isEmpty() ? 0 : 1 );
		
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}
}
