package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class PointColorAction extends AbstractAction
{
	private Application app;
	
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
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		Color color = JColorChooser.showDialog(
				app, getLocaleText( "point_color_des" ), app.getPointColor() );
		if ( color != null )
		{
			app.setPointColor( color );
		}
	}
}
