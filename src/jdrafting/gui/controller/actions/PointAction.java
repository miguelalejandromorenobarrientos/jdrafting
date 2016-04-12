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
import jdrafting.gui.controller.mouse.PointListener;

@SuppressWarnings("serial")
public class PointAction extends AbstractAction
{
	private Application app;
	
	public PointAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "point" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "point_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_P );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_1, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "point.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "point.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( 
										new PointListener( app.getCanvas() ) );
	}
}
