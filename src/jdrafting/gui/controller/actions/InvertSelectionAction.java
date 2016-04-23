package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class InvertSelectionAction extends AbstractAction 
{
	private Application app;
	
	public InvertSelectionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "invert" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "invert_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_I );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_G, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "invert.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "invert.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		app.setSelectedShapes( app.getExercise().getShapes()
		.stream()
		.filter( jdshape -> !app.getSelectedShapes().contains( jdshape ) )
		.collect( Collectors.toSet() ) );
		
		app.scrollList.repaint();
		app.getCanvas().repaint();
	}
}
