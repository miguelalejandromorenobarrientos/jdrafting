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
import jdrafting.gui.controller.mouse.ParallelListener;

@SuppressWarnings("serial")
public class ParallelAction extends AbstractAction 
{
	private Application app;
	
	public ParallelAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "para" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "para_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_A );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_2, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "parallel.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "parallel.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
									new ParallelListener( app.getCanvas() ) );
	}
}
