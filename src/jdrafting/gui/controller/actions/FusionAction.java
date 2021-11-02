package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.elvis;
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

import jdrafting.gui.Application;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Fusion the PathIterator o several shapes on one shape
 * @author Miguel Alejandro Moreno Barrientos, (C)?-2021
 * @version 0.1.12
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
			JOptionPane.showMessageDialog( app, getLocaleText( "sel_2_error" ),
										   getLocaleText( "fusion" ),
										   JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		final String descHtml = "{" + String.join( ",", app.getSelectedShapes()
	  			.parallelStream()
	  			.map( jdshape -> "<font color=" 
	  							 + Application.HTML_SHAPE_NAMES_COL 
	  							 + ">[" 
	  							 + elvis( jdshape.getName(), "?" )
	  					  		 + "]</font>" )
	  			.collect( Collectors.toList() ) ) + "}";

		//////////////////////////// TRANSACTION ////////////////////////////
		final JDCompoundEdit transaction = new JDCompoundEdit( getLocaleText( "fusion" ) 
							+ " " + descHtml + " (" + app.getSelectedShapes().size() + " shapes)" );

		// remove shapes from exercise and create merged path
		final Path2D path = new Path2D.Double();
		final boolean connect = 
							( e.getModifiers() & ActionEvent.SHIFT_MASK ) == ActionEvent.SHIFT_MASK;
		
		app.getSelectedShapes()
		   .stream()
		   .sorted( (jds1,jds2) -> Integer.compare( app.getExercise().getShapes().indexOf(jds1),
			   									    app.getExercise().getShapes().indexOf(jds2) ) )  // improve filling
		   .forEach( jdshape -> {
			   app.removeShape( jdshape, transaction );
			   path.append( jdshape.getShape().getPathIterator( null ), connect );
			});
		
		// add new shape to exercise
		app.addShapeFromIterator( path.getPathIterator( null ),	"", 
					getLocaleText( "new_fusion" ) + " " + descHtml, app.getColor(), null,
					app.getStroke(), transaction );
		
		transaction.end();
		app.undoRedoSupport.postEdit( transaction );
		/////////////////////////////////////////////////////////////////////
		
		app.setSelectedShapes( new HashSet<>() );

		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}

}
