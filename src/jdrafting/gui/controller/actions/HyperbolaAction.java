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
import jdrafting.gui.JDUtils;
import jdrafting.gui.controller.mouse.HyperbolaListener;

@SuppressWarnings("serial")
public class HyperbolaAction extends AbstractAction 
{
	private Application app;
	
	public HyperbolaAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "hyperbola" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "hyperbola_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_hyperbola" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_7, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "hyperbola.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "hyperbola.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new HyperbolaListener( app.getCanvas() ) );
	}
}
