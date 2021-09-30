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
public class TextVisibleAction extends AbstractAction 
{
	private Application app;
	
	public TextVisibleAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "text" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "text_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_text" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_W, InputEvent.CTRL_MASK ) );
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
