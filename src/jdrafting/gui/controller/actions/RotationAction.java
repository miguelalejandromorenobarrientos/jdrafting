package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;
import jdrafting.gui.controller.mouse.RotationListener;

@SuppressWarnings("serial")
public class RotationAction extends AbstractAction
{
	private Application app;
	
	public RotationAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "rotation" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "rotation_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_rotation" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "rotation.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "rotation.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getSelectedShapes().size() > 0 )
			app.getCanvas().setCanvasListener( 
									new RotationListener( app.getCanvas() ) );
		else
			JOptionPane.showMessageDialog( app, 
										   getLocaleText( "selected_shapes_msg" ), 
										   getLocaleText( "rotation" ) + " error", 
										   JOptionPane.ERROR_MESSAGE );
	}
}
