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
import jdrafting.gui.controller.mouse.DivisionPointsListener;

@SuppressWarnings("serial")
public class DivisionPointsAction extends AbstractAction
{
	private Application app;
	
	public DivisionPointsAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "divisions" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "divisions_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_divisions" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_0, InputEvent.ALT_MASK )  );
		putValue( SMALL_ICON, getSmallIcon( "divisions.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "divisions.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( new DivisionPointsListener( app.getCanvas() ) );
	}

}
