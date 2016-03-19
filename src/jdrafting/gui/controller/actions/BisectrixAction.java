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
import jdrafting.gui.controller.mouse.BisectrixListener;

@SuppressWarnings("serial")
public class BisectrixAction extends AbstractAction 
{
	private Application app;
	
	public BisectrixAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "bisectrix" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "bisectrix_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_P );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_4, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "bisectrix.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "bisectrix.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
				new BisectrixListener( app.getCanvas() ) );
	}
}
