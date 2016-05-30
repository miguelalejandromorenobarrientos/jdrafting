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
import jdrafting.gui.controller.mouse.TranslationListener;

@SuppressWarnings("serial")
public class TranslationAction extends AbstractAction
{
	private Application app;
	
	public TranslationAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "translation" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "translation_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_trans" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_T, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "translation.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "translation.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getSelectedShapes().size() > 0 )
			app.getCanvas().setCanvasListener( 
								new TranslationListener( app.getCanvas() ) );
		else
			JOptionPane.showMessageDialog( app, 
									getLocaleText( "selected_shapes_msg" ), 
									getLocaleText( "translation" ) + " error", 
									JOptionPane.ERROR_MESSAGE );
	}
}
