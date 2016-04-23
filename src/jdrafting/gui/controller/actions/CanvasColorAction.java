package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class CanvasColorAction extends AbstractAction
{
	private Application app;
	private JDialog colorChooser;
	private JColorChooser jcc;
	
	public CanvasColorAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "background_color" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "background_color_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_C );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_B, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "backcolor.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "backcolor.png" ) );

		jcc = new JColorChooser();
		colorChooser = JColorChooser.createDialog( 
					app, getLocaleText( "background_color_des" ), true, jcc, 
					(evt) -> app.setBackColor( jcc.getColor() ), // ok
					null ); // cancel 
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		jcc.setColor( app.getBackColor() );
		colorChooser.setVisible( true );
	}
}
