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
import jdrafting.gui.controller.mouse.FreeHandListener;

@SuppressWarnings("serial")
public class FreeHandAction extends AbstractAction 
{
	private Application app;
	
	public FreeHandAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "free_hand" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "free_hand_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_F );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "free_hand.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "free_hand.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new FreeHandListener( app.getCanvas() ) );
	}
}
