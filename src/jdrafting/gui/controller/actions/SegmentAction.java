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
import jdrafting.gui.controller.mouse.SegmentListener;

@SuppressWarnings("serial")
public class SegmentAction extends AbstractAction
{
	private Application app;
	
	public SegmentAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "segment" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "segment_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_segment" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_2, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "segment.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "segment.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.getCanvas().setCanvasListener( new SegmentListener( app.getCanvas() ) );
	}
}
