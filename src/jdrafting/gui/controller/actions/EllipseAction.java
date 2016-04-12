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
import jdrafting.gui.controller.mouse.EllipseListener;

@SuppressWarnings("serial")
public class EllipseAction extends AbstractAction 
{
	private Application app;
	
	public EllipseAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "ellipse" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "ellipse_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_E );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_8, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "ellipse.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "ellipse.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new EllipseListener( app.getCanvas() ) );
	}
}
