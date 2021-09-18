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
import jdrafting.gui.controller.mouse.TextBoxListener;

@SuppressWarnings("serial")
public class TextBoxAction extends AbstractAction
{
	private Application app;
	
	public TextBoxAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "comment" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "comment_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_comment" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_T, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "text.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "text.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( new TextBoxListener( app.getCanvas() ) );
	}
}
