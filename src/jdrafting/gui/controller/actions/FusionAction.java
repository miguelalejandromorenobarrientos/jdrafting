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

@SuppressWarnings("serial")
public class FusionAction extends AbstractAction
{
	private Application app;
	
	public FusionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "fusion" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "fusion_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_C );
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
		
		// new shape name
		String name = String.join( "-", app.getSelectedShapes()
	  			.stream()
	  			.map( jdshape -> jdshape.getName().length() > 0
	  							 ? jdshape.getName()
	  							 : "?" )
	  			.collect( Collectors.toList() ) );

		// create undo/redo transaction
		CompoundEdit transaction = new CompoundEdit() {
			@Override
			public boolean canRedo() { return true; };
			@Override
			public boolean canUndo() { return true; };
			@Override
			public String getRedoPresentationName()
			{
				return "Redo Fusion " + name 
					   + "(" + ( edits.size() - 1 ) + " shapes)";
			}
			@Override
			public String getUndoPresentationName()
			{
				return "Redo Fusion " + name 
					   + "(" + ( edits.size() - 1 ) + " shapes)";
			}
		};

		// remove shapes from exercise and create merged path
		Path2D path = new Path2D.Double();
		boolean connect = ( e.getModifiers() & ActionEvent.SHIFT_MASK )
				 == ActionEvent.SHIFT_MASK;
		app.getSelectedShapes()
		.stream()
		.forEach( jdshape -> {
			app.removeShape( jdshape, transaction );
			path.append( jdshape.getShape().getPathIterator( null ), connect );
		});
		
		// add new shape to exercise
		app.addShapeFromIterator( path.getPathIterator( null ),	name, 
					getLocaleText( "new_fusion" ) + " " + name, app.getColor(), 
					app.getStroke(), transaction );
		
		transaction.end();
		app.undoSupport.postEdit( transaction );
		app.setSelectedShapes( new HashSet<>() );

		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}

}
