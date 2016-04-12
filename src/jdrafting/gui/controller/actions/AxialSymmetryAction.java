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
import jdrafting.gui.controller.mouse.AxialSymmetryListener;

@SuppressWarnings("serial")
public class AxialSymmetryAction extends AbstractAction
{
	private Application app;
	
	public AxialSymmetryAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "axial_sym" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "axial_sym_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_A );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_A, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "axial_symmetry.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "axial_symmetry.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getSelectedShapes().size() > 0 )
			app.getCanvas().setCanvasListener( 
								new AxialSymmetryListener( app.getCanvas() ) );
		else
			JOptionPane.showMessageDialog( app, 
									getLocaleText( "selected_shapes_msg" ), 
									getLocaleText( "axial_sym" ) + " error", 
									JOptionPane.ERROR_MESSAGE );
	}
}
