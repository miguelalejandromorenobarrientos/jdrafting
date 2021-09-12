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
import jdrafting.gui.controller.mouse.PaintListener;

@SuppressWarnings("serial")
public class PaintAction extends AbstractAction 
{
	private Application app;
	
	public PaintAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "paint" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "paint_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_paint" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "paint.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "paint.png" ) );
	}
		
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( new PaintListener( app.getCanvas() ) );
	}
}
