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
import jdrafting.gui.controller.mouse.SplineListener;

@SuppressWarnings("serial")
public class SplineAction extends AbstractAction
{
	private Application app;
	
	public SplineAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "spline" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "spline_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_spline" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_8, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "spline.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "spline.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
										new SplineListener( app.getCanvas() ) );
	}
}
