package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class MoveZBufferAction extends AbstractAction 
{
	private Application app;
	private boolean up;
	
	public MoveZBufferAction( Application app, boolean up )
	{
		this.app = app;
		this.up = up;
		
		putValue( NAME, getLocaleText( up ? "move_up" : "move_down" ) );
		putValue( SHORT_DESCRIPTION, 
						getLocaleText( up ? "move_up_des" : "move_down_des" ) );
		putValue( MNEMONIC_KEY, up ? KeyEvent.VK_U : KeyEvent.VK_D );
		putValue( ACCELERATOR_KEY, up ?
			KeyStroke.getKeyStroke( KeyEvent.VK_UP, InputEvent.CTRL_MASK ) :
			KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( up ? "up.png" : "down.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( up ? "up.png" : "down.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( app.getSelectedShapes().isEmpty() )  return;
		
		Set<JDraftingShape> selected = app.getSelectedShapes();

		// order selected shapes by index
		List<JDraftingShape> ordered = selected
			.stream()
			.sorted( (s1,s2) -> 
					 Integer.compare( app.getExercise().indexOf( s1 ), 
								 	  app.getExercise().indexOf( s2 ) ) )
			.collect( Collectors.toList() );
		
		// move selected shapes up or down
		if ( up ? moveUp( ordered ) : moveDown( ordered ) )
			app.undoSupport.postEdit( new EditMoveZBuffer( ordered ) );		

		// recover selection (modified in shapelist listener)
		app.setSelectedShapes( selected );
		
		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}
	
	private boolean moveUp( List<JDraftingShape> ordered )
	{
		int maxIndex = 
				app.getExercise().indexOf( ordered.get( ordered.size() - 1 ) );
		
		if ( maxIndex == app.getExercise().size() - 1 )
			return false;
		
		List<JDraftingShape> reverse = new ArrayList<JDraftingShape>( ordered );  
		Collections.reverse( reverse );
		
		for ( JDraftingShape jdshape : reverse )
		{
			int index = app.getExercise().indexOf( jdshape );
			app.getExercise().removeShape( index );
			app.shapeList.getModel().remove( index );
			app.getExercise().addShape( index + 1, jdshape );
			app.shapeList.getModel().add( index + 1, jdshape );
		}
		app.shapeList.ensureIndexIsVisible( maxIndex + 1 );
		
		return true;
	}

	private boolean moveDown( List<JDraftingShape> ordered )
	{
		int minIndex = app.getExercise().indexOf( ordered.get( 0 ) );
		
		if ( minIndex == 0 )
			return false;
		
		for ( JDraftingShape jdshape : ordered )
		{
			int index = app.getExercise().indexOf( jdshape );
			app.getExercise().removeShape( index );
			app.shapeList.getModel().remove( index );
			app.getExercise().addShape( index - 1, jdshape );
			app.shapeList.getModel().add( index - 1, jdshape );
		}
		app.shapeList.ensureIndexIsVisible( minIndex - 1 );
		
		return true;
	}
	
	/**
	 * UndoableEdit to move shapes zbuffer 
	 */
	private class EditMoveZBuffer extends AbstractUndoableEdit
	{
		private List<JDraftingShape> ordered;
		
		private EditMoveZBuffer( List<JDraftingShape> ordered )
		{
			this.ordered = ordered;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			if ( up )
				moveDown( ordered );
			else
				moveUp( ordered );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			if ( up )
				moveUp( ordered );
			else
				moveDown( ordered );
		}
		
		@Override
		public boolean canUndo() { return true; }
		@Override
		public boolean canRedo() { return true; }
		
		@Override
		public String getUndoPresentationName()
		{
			return "Undo move selection zbuffer " + ( up ? "up" : "down" )  
				   + " (" + ordered.size() + " shapes)";
		}
		@Override
		public String getRedoPresentationName()
		{
			return "Redo move selection zbuffer " + ( up ? "up" : "down" )  
				   + " (" + ordered.size() + " shapes)";
		}
	}
}
