package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Shape from symmetric difference 
 * @author Miguel Alejandro Moreno Barrientos, (C)2021
 * @since 0.1.12
 */
@SuppressWarnings("serial")
public class AreaSymmetricDifferenceAction extends AbstractAction
{
	private Application app;
	
	public AreaSymmetricDifferenceAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "area_sym_diff" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "area_sym_diff_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_sym_diff" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									 KeyEvent.VK_7, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "area_symmetric_substract.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "area_symmetric_substract.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		// check two or more selected shapes
		if ( app.getSelectedShapes().size() < 2 )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "sel_2_error" ),
										   getLocaleText( "area_sym_diff" ),
										   JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		// shape enum for description
		final String descHtml = "{" + String.join( ",", app.getSelectedShapes()
								  			.parallelStream()
								  			.map( jdshape -> "<font color=" 
								  							 + Application.HTML_SHAPE_NAMES_COL 
								  							 + ">[" 
								  							 + elvis( jdshape.getName(), "?" )
								  					  		 + "]</font>" )
								  			.collect( Collectors.toList() ) ) + "}";

		// create intersection area
		final Area symdiffArea = app.getSelectedShapes().parallelStream()
										 .map( jds -> new Area( jds.getShape() ) )
										 .reduce( (area1,area2) -> { area1.exclusiveOr( area2 );
										 							 return area1; } )
										 .get();

		// check for empty area
		if ( symdiffArea.isEmpty() )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "empty_sym_diff_error" ),
					   					   getLocaleText( "area_sym_diff" ),
					   					   JOptionPane.ERROR_MESSAGE );
			return;
		}

		// final shape
		final Shape result = JDUtils.removeUnnecessarySegments( symdiffArea.isSingular()
							 ? JDUtils.closeShapeWithLine( symdiffArea, true )
							 : symdiffArea );		

		//////////////////////////// TRANSACTION ////////////////////////////				
		final JDCompoundEdit transaction = new JDCompoundEdit( 
												getLocaleText( "area_sym_diff" ) + " " + descHtml );
		
		// add new shape to exercise
		app.addShapeFromIterator( result.getPathIterator( null ), "", 
							   getLocaleText( "new_sym_diff" ) + " " + descHtml, app.getColor(), 
							   null, app.getStroke(), transaction );
		
		transaction.end();
		app.undoRedoSupport.postEdit( transaction );
		/////////////////////////////////////////////////////////////////////
		
		app.setSelectedShapes( new HashSet<>() );

		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}
}
