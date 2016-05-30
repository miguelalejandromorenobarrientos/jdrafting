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
import jdrafting.gui.controller.mouse.BisectrixListener;

@SuppressWarnings("serial")
public class BisectrixAction extends AbstractAction 
{
	private Application app;
	
	public BisectrixAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "bisectrix" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "bisectrix_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_bisectrix" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_4, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "bisectrix.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "bisectrix.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
				new BisectrixListener( app.getCanvas() ) );
	}
}
