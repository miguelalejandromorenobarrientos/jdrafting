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
import jdrafting.gui.controller.mouse.CircumferenceListener;

@SuppressWarnings("serial")
public class CircumferenceAction extends AbstractAction 
{
	private Application app;
	
	public CircumferenceAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "circumference" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "circumference_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_circ" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_4, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "circumference.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "circumference.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
								new CircumferenceListener( app.getCanvas() ) );
	}
}
