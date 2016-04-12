package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class EndAction extends AbstractAction
{
	private Application app;
	
	public EndAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "end" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "end_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_E );
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