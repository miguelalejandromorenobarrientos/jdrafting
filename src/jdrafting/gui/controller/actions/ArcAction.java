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
import jdrafting.gui.controller.mouse.ArcListener;

@SuppressWarnings("serial")
public class ArcAction extends AbstractAction 
{
	private Application app;
	
	public ArcAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "arc" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "arc_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_arc" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_3, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "arc.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "arc.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( new ArcListener( app.getCanvas() ) );
	}
}
