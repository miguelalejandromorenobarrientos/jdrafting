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
import jdrafting.gui.controller.mouse.PerpendicularListener;

@SuppressWarnings("serial")
public class PerpendicularAction extends AbstractAction 
{
	private Application app;
	
	public PerpendicularAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "perp" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "perp_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_P );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_1, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "perpendicular.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "perpendicular.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
								new PerpendicularListener( app.getCanvas() ) );
	}
}
