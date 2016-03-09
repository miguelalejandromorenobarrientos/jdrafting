package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class CopySelectedAction extends AbstractAction
{
	private Application app;
	
	public CopySelectedAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "copy" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "copy_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_C );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK )  );
		putValue( SMALL_ICON, getSmallIcon( "copy.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "copy.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( !app.getSelectedShapes().isEmpty() )
		{
			// create undo/redo transaction
			CompoundEdit transaction = new CompoundEdit() {
				@Override
				public boolean canRedo() { return true; };
				@Override
				public boolean canUndo() { return true; };
				@Override
				public String getRedoPresentationName()
				{
					return "Redo copy selected (" + edits.size() + " shapes)";
				}
				@Override
				public String getUndoPresentationName()
				{
					return "Undo copy selected (" + edits.size() + " shapes)";
				}
			};
			
			// move copies relative to originals and add them to exercise
			double tx = app.getCanvas().getViewport().getWidth() / 30.;
			double ty = app.getCanvas().getViewport().getHeight() / 30.;
			AffineTransform transform =
								AffineTransform.getTranslateInstance( tx, -ty );
			
			Set<JDraftingShape> copySet = app.getSelectedShapes()
			.stream()
			.map( jdshape -> app.addShapeFromIterator( 
					jdshape.getShape().getPathIterator( transform ), 
					"copy of " + jdshape.getName(), jdshape.getDescription(), 
					jdshape.getColor(), jdshape.getStroke(), transaction ) )
			.collect( Collectors.toSet() );

			transaction.end();
			app.undoSupport.postEdit( transaction );

			// change selection to new shapes
			app.setSelectedShapes( copySet );  
			
			// refresh
			app.scrollList.repaint();
			app.getCanvas().repaint();
		}
	}
}
