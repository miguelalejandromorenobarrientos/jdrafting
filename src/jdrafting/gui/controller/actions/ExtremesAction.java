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
import jdrafting.gui.controller.mouse.ExtremesListener;

@SuppressWarnings("serial")
public class ExtremesAction extends AbstractAction
{
	private Application app;
	
	public ExtremesAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "extremes" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "extremes_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_extremes" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_9, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "extremes.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "extremes.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener(
									new ExtremesListener( app.getCanvas() ) );
	}

}
