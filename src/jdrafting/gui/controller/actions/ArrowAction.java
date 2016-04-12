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
import jdrafting.gui.controller.mouse.ArrowListener;

@SuppressWarnings("serial")
public class ArrowAction extends AbstractAction
{
	private Application app;
	
	public ArrowAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "arrow" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "arrow_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_A );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_3, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "arrow.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "arrow.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( 
									new ArrowListener( app.getCanvas() ) );
	}
}
