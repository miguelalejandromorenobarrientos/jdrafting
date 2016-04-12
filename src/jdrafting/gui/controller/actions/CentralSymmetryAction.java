package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.controller.mouse.CentralSymmetryListener;

@SuppressWarnings("serial")
public class CentralSymmetryAction extends AbstractAction
{
	private Application app;
	
	public CentralSymmetryAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "central_sym" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "central_sym_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_C );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_C, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "central_symmetry.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "central_symmetry.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getSelectedShapes().size() > 0 )
			app.getCanvas().setCanvasListener( 
							new CentralSymmetryListener( app.getCanvas() ) );
		else
			JOptionPane.showMessageDialog( app, 
									getLocaleText( "selected_shapes_msg" ), 
									getLocaleText( "central_sym" ) + " error", 
									JOptionPane.ERROR_MESSAGE );
	}
}
