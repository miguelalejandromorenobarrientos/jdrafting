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
 * Shape from area union 
 * @author Miguel Alejandro Moreno Barrientos, (C)2021
 * @since 0.1.11.3
 */
@SuppressWarnings("serial")
public class AreaUnionAction extends AbstractAction
{
	private Application app;
	
	public AreaUnionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "area_union" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "area_union_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_fusion" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									 KeyEvent.VK_5, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "area_union.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "area_union.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		// check two or more selected shapes
		if ( app.getSelectedShapes().size() < 2 )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "sel_2_error" ),
										   getLocaleText( "area_union" ),
										   JOptionPane.ERROR_MESSAGE );
			return;
		}
		
		// shape enum for description
		final String descHtml = "{" + String.join( ",", app.getSelectedShapes()
								  			.parallelStream()
								  			.map( jdshape -> "<font color=" 
								  							 + Application.HTML_SHAPE_NAMES_COL 
								  							 + ">[" + elvis(jdshape.getName(), "?")
								  					  		 + "]</font>" )
								  			.collect( Collectors.toList() ) ) + "}";

		// create intersection area
		final Area unionArea = app.getSelectedShapes().parallelStream()
													 .map( jds -> new Area( jds.getShape() ) )
													 .reduce( (area1,area2) -> { area1.add( area2 );
													 							 return area1; } )
													 .get();
							   
		// check for empty intersection
		if ( unionArea.isEmpty() )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "empty_union_error" ),
					   					   getLocaleText( "area_union" ),
					   					   JOptionPane.ERROR_MESSAGE );
			return;
		}

		// final shape
		final Shape result = JDUtils.removeUnnecessarySegments( unionArea.isSingular()
							 ? JDUtils.closeShapeWithLine( unionArea, true )
							 : unionArea );

		//////////////////////////// TRANSACTION ////////////////////////////				
		final JDCompoundEdit transaction = new JDCompoundEdit( 
												   getLocaleText( "area_union" ) + " " + descHtml );
		
		// add new shape to exercise
		app.addShapeFromIterator( result.getPathIterator( null ), "", 
							   getLocaleText( "new_union" ) + " " + descHtml, app.getColor(), 
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
