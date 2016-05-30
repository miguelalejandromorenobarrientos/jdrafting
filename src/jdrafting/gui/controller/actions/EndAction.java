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
public class EndAction extends AbstractAction
{
	private Application app;
	
	public EndAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "end" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "end_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_end" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, InputEvent.ALT_MASK )  );
		putValue( SMALL_ICON, getSmallIcon( "end.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "end.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getExercise().setFrameAtEnd();
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}
}
