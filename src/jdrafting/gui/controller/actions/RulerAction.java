package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.controller.mouse.RulerListener;

@SuppressWarnings("serial")
public class RulerAction extends AbstractAction
{
	private Application app;
	
	public RulerAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "ruler" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "ruler_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_R );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
							KeyEvent.VK_BACK_SPACE, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "ruler.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "ruler.png" ) );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
										new RulerListener( app.getCanvas() ) );
		
		app.setUseDistance( true );
		app.checkRuler.setSelected( true );
	}
}
