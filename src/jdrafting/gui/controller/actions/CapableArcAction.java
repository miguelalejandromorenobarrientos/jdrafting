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
import jdrafting.gui.controller.mouse.CapableArcListener;

@SuppressWarnings("serial")
public class CapableArcAction extends AbstractAction 
{
	private Application app;
	
	public CapableArcAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "capable_arc" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "capable_arc_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_cap_arc" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_5, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "capable_arc.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "capable_arc.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new CapableArcListener( app.getCanvas() ) );
	}
}
