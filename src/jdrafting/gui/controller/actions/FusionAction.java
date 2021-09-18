package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;

/**
 * Fusion the PathIterator o several shapes on one shape
 * @author Miguel Alejandro Moreno Barrientos, (C)?-2021
 * @version 0.1.11.1
 */
@SuppressWarnings("serial")
public class FusionAction extends AbstractAction
{
	private Application app;
	
	public FusionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "fusion" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "fusion_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_fusion" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
				KeyEvent.VK_3, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "fusion.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "fusion.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		// check two or more selected shapes
		if ( app.getSelectedShapes().size() < 2 )
		{
			JOptionPane.showMessageDialog( app, 
					"Select two or more shapes",
					getLocaleText( "fusion" ),
					JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		// shape description
		String desc = String.join( "-", app.getSelectedShapes()
	  			.stream()
	  			.map( jdshape -> jdshape.getName().length() > 0
	  							 ? jdshape.getName()
	  							 : "?" )
	  			.collect( Collectors.toList() ) );

		// create undo/redo transaction
		final CompoundEdit transaction = new CompoundEdit() {
			@Override
			public boolean canRedo() { return true; };
			@Override
			public boolean canUndo() { return true; };
			@Override
			public String getRedoPresentationName()
			{
				return "Redo Fusion " + desc 
					   + "(" + ( edits.size() - 1 ) + " shapes)";
			}
			@Override
			public String getUndoPresentationName()
			{
				return "Redo Fusion " + desc 
					   + "(" + ( edits.size() - 1 ) + " shapes)";
			}
		};

		// remove shapes from exercise and create merged path
		final Path2D path = new Path2D.Double();
		final boolean connect = ( e.getModifiers() & ActionEvent.SHIFT_MASK )
								== ActionEvent.SHIFT_MASK;
		app.getSelectedShapes()
		   .stream()
		   .sorted( (jds1,jds2) -> Integer.compare( app.getExercise().getShapes().indexOf(jds1),
			   									    app.getExercise().getShapes().indexOf(jds2) ) )  // improve filling
		   .forEach( jdshape -> {
			   app.removeShape( jdshape, transaction );
			   path.append( jdshape.getShape().getPathIterator( null ), connect );
			}
		);
		
		// add new shape to exercise
		app.addShapeFromIterator( path.getPathIterator( null ),	"", 
					getLocaleText( "new_fusion" ) + " " + desc, app.getColor(), 
					app.getStroke(), transaction );
		
		transaction.end();
		app.undoSupport.postEdit( transaction );
		app.setSelectedShapes( new HashSet<>() );

		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}

}
