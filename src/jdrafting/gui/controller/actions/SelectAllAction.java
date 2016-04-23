package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class SelectAllAction extends AbstractAction 
{
	private Application app;
	
	public SelectAllAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "select_all" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "select_all_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_A );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_A, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "select_all.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "select_all.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.setSelectedShapes( new HashSet<>( app.getExercise().getShapes() ) );

		app.scrollList.repaint();
		app.getCanvas().repaint();
	}
}
