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
public class PolyLineAction extends AbstractAction
{
	private Application app;
	
	public PolyLineAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "polyline" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "polyline_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_polyline" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_4, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "polyline.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "polyline.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( new PolygonListener( app.getCanvas(), false ) );
	}
}
