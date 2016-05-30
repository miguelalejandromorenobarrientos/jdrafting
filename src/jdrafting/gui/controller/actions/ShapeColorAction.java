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
import jdrafting.gui.JDUtils;

@SuppressWarnings("serial")
public class ShapeColorAction extends AbstractAction
{
	private Application app;
	private JDialog colorChooser;
	private JColorChooser jcc;
	
	public ShapeColorAction( Application app )
	{
		this.app = app;

		putValue( NAME, getLocaleText( "color" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "color_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_shape_col" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "color.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "color.png" ) );

		jcc = new JColorChooser();
		colorChooser = JColorChooser.createDialog( 
								app, getLocaleText( "color_des" ), true, jcc, 
								(evt) -> app.setColor( jcc.getColor() ), // ok
								null ); // cancel 
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		jcc.setColor( app.getColor() );
		colorChooser.setVisible( true );
	}
}
