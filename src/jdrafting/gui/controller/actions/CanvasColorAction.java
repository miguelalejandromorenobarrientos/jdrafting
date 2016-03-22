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
public class CanvasColorAction extends AbstractAction
{
	private Application app;
	
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
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		Color backColor = JColorChooser.showDialog(
			app, getLocaleText( "background_color_des" ), app.getBackColor() );
		if ( backColor != null )
		{
			app.setBackColor( backColor );
			app.scrollList.repaint();
		}
	}
}
