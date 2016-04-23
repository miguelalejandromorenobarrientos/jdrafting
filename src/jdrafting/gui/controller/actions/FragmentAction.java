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
import jdrafting.gui.controller.mouse.FragmentListener;

@SuppressWarnings("serial")
public class FragmentAction extends AbstractAction
{
	private Application app;
	
	public FragmentAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "fragment" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "fragment_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_F );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
				KeyEvent.VK_2, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "hammer.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "hammer.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener(
									new FragmentListener( app.getCanvas() ) );
	}
}