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
import jdrafting.gui.controller.mouse.ProtractorListener;

@SuppressWarnings("serial")
public class ProtractorAction extends AbstractAction
{
	private Application app;
	
	public ProtractorAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "protractor" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "protractor_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_P );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_INSERT, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "protractor.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "protractor.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new ProtractorListener( app.getCanvas() ) );
	}
}
