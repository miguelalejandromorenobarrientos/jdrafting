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

@SuppressWarnings("serial")
public class ExitAction extends AbstractAction
{
	private Application app;
	
	public ExitAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "exit" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "exit_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_E );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_Q, InputEvent.CTRL_MASK )  );
		putValue( SMALL_ICON, getSmallIcon( "exit.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "exit.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.exit();
	}
}
