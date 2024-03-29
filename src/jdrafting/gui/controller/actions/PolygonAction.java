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
import jdrafting.gui.controller.mouse.PolygonListener;

@SuppressWarnings("serial")
public class PolygonAction extends AbstractAction
{
	private Application app;
	
	public PolygonAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "polygon" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "polygon_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_polygon" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_3, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "polygon.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "polygon.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( new PolygonListener( app.getCanvas(), true ) );
	}
}
