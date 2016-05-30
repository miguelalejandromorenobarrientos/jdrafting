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
import jdrafting.gui.controller.mouse.RectangleListener;

@SuppressWarnings("serial")
public class RectangleAction extends AbstractAction
{
	private Application app;
	
	public RectangleAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "rectangle" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "rectangle_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_rect" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_7, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "rectangle.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "rectangle.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new RectangleListener( app.getCanvas() ) );
	}
}
