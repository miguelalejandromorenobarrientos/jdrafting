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
import jdrafting.gui.controller.mouse.SelectionListener;

@SuppressWarnings("serial")
public class SelectionAction extends AbstractAction
{
	private Application app;
	
	public SelectionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "selection" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "selection_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_S );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_0, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "selection.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "selection.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new SelectionListener( app.getCanvas() ) );
	}
}