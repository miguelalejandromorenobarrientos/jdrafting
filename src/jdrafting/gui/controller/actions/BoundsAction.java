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
import jdrafting.gui.controller.mouse.BoundsListener;

@SuppressWarnings("serial")
public class BoundsAction extends AbstractAction 
{
	private Application app;
	
	public BoundsAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "bounds" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "bounds_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_bounds" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
				KeyEvent.VK_1, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "bounds.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "bounds.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( 
										new BoundsListener( app.getCanvas() ) );
	}
}
