package jdrafting.gui;

import javax.swing.UIManager;
import javax.swing.undo.CompoundEdit;

/**
 * @author Miguel Alejandro Moreno Barrientos, (C)2021
 * @since 0.1.12
 */
public class JDCompoundEdit extends CompoundEdit 
{
	private static final long serialVersionUID = 1L;
	
	private String presentation = "";
	
	public JDCompoundEdit() {}	
	public JDCompoundEdit( String presentation ) { this.presentation = presentation; }
	
	@Override
	public boolean canRedo() { return true; }
	@Override
	public boolean canUndo() { return true; }
	
	@Override
	public String getPresentationName() { return presentation; }	
	@Override
	public String getRedoPresentationName() 
	{
		return new StringBuilder( UIManager.getString("AbstractUndoableEdit.redoText") )
			   .append( " " ).append( getPresentationName() ).toString();
	}
	@Override
	public String getUndoPresentationName() 
	{
		return new StringBuilder( UIManager.getString("AbstractUndoableEdit.undoText") )
			   .append( " " ).append( getPresentationName() ).toString();
	}

}
