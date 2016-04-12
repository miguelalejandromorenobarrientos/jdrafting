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
import jdrafting.gui.controller.mouse.AngleListener;

@SuppressWarnings("serial")
public class AngleAction extends AbstractAction 
{
	private Application app;
	
	public AngleAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "angle" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "angle_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_N );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_6, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "angle.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "angle.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
										new AngleListener( app.getCanvas() ) );
	}
}
