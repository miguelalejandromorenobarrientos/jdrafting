package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.JDCompoundEdit;
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
		//////////////////////////// TRANSACTION ////////////////////////////
		final JDCompoundEdit transaction = new JDCompoundEdit() {
			@Override
			public String getPresentationName() 
			{
				return getLocaleText( "delete" ) + " (" + edits.size() + " shapes)";
			}
		};

		app.getSelectedShapes()
		   .stream()
		   .forEach( jdshape -> {
			   if ( app.shapeList.getModel().contains( jdshape ) )
				   app.removeShape( jdshape, transaction );
		   });

		transaction.end();
		app.undoRedoSupport.postEdit( transaction );
		/////////////////////////////////////////////////////////////////////

		app.setSelectedShapes( new HashSet<>() );
		
		app.getCanvas().repaint();
		app.scrollList.repaint();		
	}	
}
