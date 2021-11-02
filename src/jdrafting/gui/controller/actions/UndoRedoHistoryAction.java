package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.UndoRedoHistoryList;

@SuppressWarnings("serial")
public class UndoRedoHistoryAction extends AbstractAction
{
	private Application app;
	
	public UndoRedoHistoryAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, "\u25BC" );
		putValue( SHORT_DESCRIPTION, getLocaleText( "tip_undo_redo" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( "F2" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.undoRedoWindow != null )
			app.undoRedoWindow.dispose();
		
		// create window, list and scroll
		final JWindow win = app.undoRedoWindow = new JWindow( app );
		final UndoRedoHistoryList lst = new UndoRedoHistoryList( app );
		win.add( new JScrollPane( lst ) );
		
		// config
		lst.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY, 2, true ) );
		win.addWindowFocusListener( new WindowFocusListener() {
			@Override
			public void windowLostFocus( WindowEvent e ) 
			{ 
				win.dispose();
				app.refreshUndoRedo();
			}				
			@Override
			public void windowGainedFocus( WindowEvent e ) {}
		});
		win.pack();
		
		// window position
		final Point loc = app.btnHistory.getLocationOnScreen();		
		win.setLocation( loc.x, 
						 loc.y+win.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().height
						 ? loc.y - win.getHeight()
						 : loc.y + app.btnHistory.getHeight() );			
		
		// show window
		win.setVisible( true );
		
		// gain focus
		lst.requestFocus();
		lst.requestFocusInWindow();
	}
	
}
