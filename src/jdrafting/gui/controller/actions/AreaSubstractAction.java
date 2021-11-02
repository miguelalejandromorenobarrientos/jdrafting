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
import jdrafting.gui.controller.mouse.AreaSubstractListener;

/**
 * Shape from area substraction 
 * @author Miguel Alejandro Moreno Barrientos, (C)2021
 * @since 0.1.11.3
 */
@SuppressWarnings("serial")
public class AreaSubstractAction extends AbstractAction
{
	private Application app;
	
	public AreaSubstractAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "area_substract" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "area_substract_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_fusion" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									 KeyEvent.VK_6, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "area_substract.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "area_substract.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( new AreaSubstractListener( app.getCanvas() ) );
	}
}
