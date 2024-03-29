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
import jdrafting.gui.controller.mouse.VertexListener;

@SuppressWarnings("serial")
public class VertexAction extends AbstractAction
{
	private Application app;
	
	public VertexAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "vertex" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "vertex_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_vertex" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_8, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "vertex.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "vertex.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( new VertexListener( app.getCanvas() ) );
	}
}
