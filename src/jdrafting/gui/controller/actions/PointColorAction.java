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
public class PointColorAction extends AbstractAction
{
	private Application app;
	private JDialog colorChooser;
	private JColorChooser jcc;
	
	public PointColorAction( Application app )
	{
		this.app = app;

		putValue( NAME, getLocaleText( "point_color" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "point_color_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_P );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_K, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "point_color.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "point_color.png" ) );
	
		jcc = new JColorChooser();
		colorChooser = JColorChooser.createDialog( 
					app, getLocaleText( "point_color_des" ), true, jcc, 
					(evt) -> app.setPointColor( jcc.getColor() ), // ok
					null ); // cancel 
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		jcc.setColor( app.getPointColor() );
		colorChooser.setVisible( true );
	}
}
