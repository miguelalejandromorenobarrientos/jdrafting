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
public class ShapeFillAction extends AbstractAction
{
	private Application app;
	private JDialog colorChooser;
	private JColorChooser jcc;
	
	public ShapeFillAction( Application app )
	{
		this.app = app;

		putValue( NAME, getLocaleText( "fill" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "fill_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_shape_fill" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "fill_color.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "fill_color.png" ) );

		jcc = new JColorChooser();
		colorChooser = JColorChooser.createDialog( 
								app, getLocaleText( "fill_des" ), true, jcc, 
								evt -> app.setFill( jcc.getColor() ), // ok
								null ); // cancel 
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		jcc.setColor( app.getFill() );
		colorChooser.setVisible( true );
	}
}
