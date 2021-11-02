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
import jdrafting.gui.controller.mouse.MidpointListener;

@SuppressWarnings("serial")
public class MidpointAction extends AbstractAction
{
	private Application app;
	
	public MidpointAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "midpoint" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "midpoint_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_midpoint" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_7, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "midpoint.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "midpoint.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( new MidpointListener( app.getCanvas() ) );
	}

}
