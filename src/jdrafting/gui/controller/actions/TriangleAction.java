package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import jdrafting.gui.Application;
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
		putValue( MNEMONIC_KEY, KeyEvent.VK_T );
		/*putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_5, InputEvent.CTRL_MASK ) );*/
		putValue( SMALL_ICON, getLargeIcon( "triangle.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "triangle.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new TriangleListener( app.getCanvas() ) );
	}
}
