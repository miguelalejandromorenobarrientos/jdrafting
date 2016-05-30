package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;
import jdrafting.gui.controller.mouse.TriangleListener;

@SuppressWarnings("serial")
public class TriangleAction extends AbstractAction 
{
	private Application app;
	
	public TriangleAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "triangle" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "triangle_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_triangle" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_1, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, JDUtils.getSmallIcon( "triangle.png" ) );
		putValue( LARGE_ICON_KEY, JDUtils.getLargeIcon( "triangle.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new TriangleListener( app.getCanvas() ) );
	}
}
