package jdrafting.gui.controller.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

@SuppressWarnings("serial")
public class ShapeColorAction extends AbstractAction
{
	private Application app;
	
	public ShapeColorAction( Application app )
	{
		this.app = app;

		putValue( NAME, getLocaleText( "color" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "color_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_S );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "color.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "color.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		Color color = JColorChooser.showDialog(
							app, getLocaleText( "color_des" ), app.getColor() );
		if ( color != null )
		{
			app.setColor( color );
		}
	}
}
