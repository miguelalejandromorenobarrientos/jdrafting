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
public class UndoAction extends AbstractAction
{
	private Application app;
	
	public UndoAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "undo" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_U );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "undo.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "undo.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.undoManager.undo();
		app.getCanvas().repaint();
		app.scrollList.repaint();
		app.setSelectedShapes( new HashSet<>() );
		app.refreshUndoRedo();
	}
}
