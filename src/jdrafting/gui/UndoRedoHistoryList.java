package jdrafting.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.undo.UndoableEdit;

import jdrafting.gui.Application.ExtendedUndoManager;

public class UndoRedoHistoryList extends JList<UndoableEdit> 
{
	private static final long serialVersionUID = 1L;
	
	final Application app;
	
	public UndoRedoHistoryList( final Application app ) 
	{
		super( new DefaultListModel<UndoableEdit>() );
		
		this.app = app;
	
		setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		update();
		
		addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) 
			{
				if ( e.getKeyCode() == KeyEvent.VK_ENTER )
					action();
			}
		} );

		addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent e) 
			{
				if ( e.getClickCount() == 2 )
					action();
			};
		});
	}
	
	private void action()
	{
		final UndoableEdit edit = getSelectedValue();
		if ( edit == null )
			return;
		
		final ExtendedUndoManager um = (ExtendedUndoManager) app.undoManager;
		
		final int idxSelected = um.getEdits().indexOf( edit ),
				  idxRedone = um.editToBeRedone() == null 
				  			  ? um.size() 
				  			  : um.getEdits().indexOf( um.editToBeRedone() );
		
		if ( idxSelected < idxRedone )
			for ( int i = idxSelected; i < idxRedone; i++, um.undo() );
		else
			for ( int i = idxRedone; i <= idxSelected; i++, um.redo() );
		
		if ( app.undoRedoWindow != null )
			app.undoRedoWindow.dispose();
		
		app.setSelectedShapes( new HashSet<>() );
		
		app.canvas.repaint();
	}
	
	@Override
	public DefaultListModel<UndoableEdit> getModel() 
	{ 
		return (DefaultListModel<UndoableEdit>) super.getModel(); 
	}
	
	private void update()
	{
		getModel().removeAllElements();
		
		final ExtendedUndoManager undoManager = (ExtendedUndoManager) app.undoManager;
		
		for ( final UndoableEdit edit : undoManager.getEdits() )
			getModel().addElement( edit );
	}
	
	@Override
	public ListCellRenderer<UndoableEdit> getCellRenderer() 
	{
		return new ListCellRenderer<UndoableEdit>() {
			@Override
			public JLabel getListCellRendererComponent( JList<? extends UndoableEdit> list, 
														UndoableEdit value, 
														int index, 
														boolean isSelected, 
														boolean cellHasFocus ) 
			{
				final ExtendedUndoManager um = (ExtendedUndoManager) app.undoManager;
				final int idxRedone = um.editToBeRedone() == null 
						  			  ? um.size() 
						  			  : um.getEdits().indexOf( um.editToBeRedone() );
				
				final JLabel lbl = new JLabel( String.format( "<html>%s</html>", 
															  index < idxRedone 
															  ? value.getUndoPresentationName()
															  : value.getRedoPresentationName() ) );
				lbl.setForeground( index < idxRedone 
								   ? new Color( 127, 0, 0 )  // red for undo
								   : new Color( 0, 127, 0 ) );  // green for redo
				lbl.setBorder( BorderFactory.createEmptyBorder( 2, 3, 2, 3 ) );
				
				if ( um.editToBeRedone() == value )
					lbl.setBackground( Color.LIGHT_GRAY );
				
				lbl.setOpaque( true );
				if ( isSelected )
					lbl.setBackground( Color.CYAN );
				
				return lbl;
			}
		};
	}
	
}
