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
import jdrafting.gui.controller.mouse.ParabolaListener;

@SuppressWarnings("serial")
public class ParabolaAction extends AbstractAction 
{
	private Application app;
	
	public ParabolaAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "parabola" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "parabola_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_parabola" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_6, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "parabola.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "parabola.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new ParabolaListener( app.getCanvas() ) );
	}
}
