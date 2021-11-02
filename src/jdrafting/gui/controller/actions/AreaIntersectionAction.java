package jdrafting.gui.controller.actions;

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

import static jdrafting.gui.JDUtils.elvis;

/**
 * Shape from area intersections 
 * @author Miguel Alejandro Moreno Barrientos, (C)2021
 * @since 0.1.11.3
 */
@SuppressWarnings("serial")
public class AreaIntersectionAction extends AbstractAction
{
	private Application app;
	
	public AreaIntersectionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "area_intersection" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "area_intersection_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_fusion" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									 KeyEvent.VK_4, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "area_intersection.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "area_intersection.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		// check two or more selected shapes
		if ( app.getSelectedShapes().size() < 2 )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "sel_2_error" ),
										   getLocaleText( "area_intersection" ),
										   JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		// shape enum for description
		final String descHtml = "{" + String.join( ",", app.getSelectedShapes()
								  						.parallelStream()
								  						.map( jdshape -> "<font color="
							  								+ Application.HTML_SHAPE_NAMES_COL
							  								+ ">[" + elvis( jdshape.getName(), "?" )
							  								+ "]</font>" )
								  						.collect( Collectors.toList() ) ) + "}";

		//////////////////////////// TRANSACTION ////////////////////////////				
		final JDCompoundEdit transaction = new JDCompoundEdit( 
											getLocaleText( "area_intersection" ) + " " + descHtml );

		// create intersection area
		final Area intersectionArea = app.getSelectedShapes().parallelStream()
										 .map( jds -> new Area( jds.getShape() ) )
										 .reduce( (area1,area2) -> { area1.intersect( area2 );
										 							 return area1; } )
										 .get();
							   
		
		// check for empty intersection
		if ( intersectionArea.isEmpty() )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "empty_intersection_error" ),
					   					   getLocaleText( "area_intersection" ),
					   					   JOptionPane.ERROR_MESSAGE );
			return;
		}

		// final shape
		final Shape result = JDUtils.removeUnnecessarySegments( intersectionArea.isSingular()
							 ? JDUtils.closeShapeWithLine( intersectionArea, true )
							 : intersectionArea );

		// add new shape to exercise
		app.addShapeFromIterator( result.getPathIterator( null ), "", 
							   getLocaleText( "new_intersection" ) + " " + descHtml, app.getColor(), 
							   null, app.getStroke(), transaction );
		
		// post transaction
		transaction.end();
		app.undoRedoSupport.postEdit( transaction );
		/////////////////////////////////////////////////////////////////////
		
		app.setSelectedShapes( new HashSet<>() );

		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}
}
