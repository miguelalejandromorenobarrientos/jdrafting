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
import jdrafting.gui.controller.mouse.PasteStyleListener;

@SuppressWarnings("serial")
public class PasteStyleAction extends AbstractAction 
{
	private Application app;
	
	public PasteStyleAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "paste_style" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "paste_style_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_paste" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "paste_style.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "paste_style.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener(
				new PasteStyleListener( app.getCanvas() ) );
	}
}
