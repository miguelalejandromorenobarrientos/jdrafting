package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;

@SuppressWarnings("serial")
public class DeleteSelectedAction extends AbstractAction
{
	private Application app;
	
	public DeleteSelectedAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "delete" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "delete_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_delete" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( "DELETE" ) );
		putValue( SMALL_ICON, getSmallIcon( "delete_all.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "delete_all.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		CompoundEdit transaction = new CompoundEdit() {
			@Override
			public boolean canRedo() { return true; };
			@Override
			public boolean canUndo() { return true; };
			@Override
			public String getRedoPresentationName()
			{
				return "Redo remove selected (" + edits.size() + " shapes)";
			}
			public String getUndoPresentationName()
			{
				return "Undo remove selected (" + edits.size() + " shapes)";
			}
		};

		app.getSelectedShapes()
		.stream()
		.forEach( jdshape -> app.removeShape( jdshape, transaction ) );

		transaction.end();
		app.undoSupport.postEdit( transaction );

		app.setSelectedShapes( new HashSet<>() );
		app.getCanvas().repaint();
		app.scrollList.repaint();		
	}	
}
