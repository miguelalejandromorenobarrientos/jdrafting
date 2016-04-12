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
public class TextVisibleAction extends AbstractAction 
{
	private Application app;
	
	public TextVisibleAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "text" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "text_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_T );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_T, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "names.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "names.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.setVisibleNames( !app.isVisibleNames() );
		app.getCanvas().repaint();
	}
}
