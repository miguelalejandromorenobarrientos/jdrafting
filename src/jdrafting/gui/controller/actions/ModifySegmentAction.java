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
import jdrafting.gui.controller.mouse.ModifySegmentListener;

@SuppressWarnings("serial")
public class ModifySegmentAction extends AbstractAction 
{
	private Application app;
	
	public ModifySegmentAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "modify" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "modify_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_mod_seg" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_6, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "modify_segment.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "modify_segment.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( new ModifySegmentListener( app.getCanvas() ) );
	}
}
