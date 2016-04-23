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
import jdrafting.gui.controller.mouse.MediatrixListener;

@SuppressWarnings("serial")
public class MediatrixAction extends AbstractAction 
{
	private Application app;
	
	public MediatrixAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "mediatrix" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "mediatrix_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_P );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_3, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "mediatrix.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "mediatrix.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new MediatrixListener( app.getCanvas() ) );
	}
}
