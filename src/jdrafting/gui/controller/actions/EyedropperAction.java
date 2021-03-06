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
import jdrafting.gui.controller.mouse.EyedropperListener;

@SuppressWarnings("serial")
public class EyedropperAction extends AbstractAction 
{
	private Application app;
	
	public EyedropperAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "eyedropper" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "eyedropper_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_eyedropper" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "eyedropper.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "eyedropper.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
				new EyedropperListener( app.getCanvas() ) );
	}
}
