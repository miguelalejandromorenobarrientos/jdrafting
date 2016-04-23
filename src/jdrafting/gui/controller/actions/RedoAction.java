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
public class RedoAction extends AbstractAction
{
	private Application app;
	
	public RedoAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "redo" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_R );
		putValue( ACCELERATOR_KEY, 
				  KeyStroke.getKeyStroke( KeyEvent.VK_Z,
							InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "redo.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "redo.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.undoManager.redo();
		app.getCanvas().repaint();
		app.scrollList.repaint();
		app.setSelectedShapes( new HashSet<>() );
		app.refreshUndoRedo();
	}
}
