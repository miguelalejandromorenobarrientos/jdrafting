package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Get intersections between selected shapes  
 */
@SuppressWarnings("serial")
public class IntersectionsAction extends AbstractAction
{
	private Application app;
	
	public IntersectionsAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "inter" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "inter_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_intersection" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									 KeyEvent.VK_0, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "intersection.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "intersection.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		// error if less than two shapes
		if ( app.getSelectedShapes().size() < 2 )
		{
			JOptionPane.showMessageDialog( app,	
					   					   getLocaleText( "inter_msg" ),
					   					   getLocaleText( "inter_dlg" ), 
					   					   JOptionPane.ERROR_MESSAGE );
			return;
		}

		// set joins style
		final Color color = ( e.getModifiers() & ActionEvent.SHIFT_MASK ) == ActionEvent.SHIFT_MASK
							? app.getColor()
							: app.getPointColor();
		final BasicStroke stroke = ( e.getModifiers() & ActionEvent.SHIFT_MASK ) 
								   == ActionEvent.SHIFT_MASK
								   ? app.getStroke()
								   : app.getPointStroke();
		
		// get pair to pair intersections
		final JDraftingShape[] arrayShapes = app.getSelectedShapes()
												.stream().toArray( JDraftingShape[]::new );
		final List<POJOJoin> joins = new LinkedList<>();

		for ( int i = 0; i < arrayShapes.length - 1; i++ )
			for ( int j = i + 1; j < arrayShapes.length; j++ )
				for ( final Point2D join : CanvasPanel.intersectionPoints( arrayShapes[i], 
																		   arrayShapes[j] ) )
				{
					final POJOJoin pojojoin = new POJOJoin();
					pojojoin.join = join;
					pojojoin.shape1 = arrayShapes[i];
					pojojoin.shape2 = arrayShapes[j];
					joins.add( pojojoin );
				}
		
		// no joins message
		if ( joins.isEmpty() )
		{
			JOptionPane.showMessageDialog( app, 
										   getLocaleText( "inter_error" ), 
										   getLocaleText( "inter" ), 
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
		final JDCompoundEdit transaction = new JDCompoundEdit() {
			@Override
			public String getPresentationName() 
			{
				return getLocaleText( "inter" ) + " " + descHtml 
					   + " (" + edits.size() + " points)";
			}
		};
		
		// add instersections to exercise
		for ( final POJOJoin pojojoin : joins )
		{
			app.addShapeFromIterator( new JDPoint( pojojoin.join ).getPathIterator( null ), "",
					  String.format( "%s {<font color=%s>[%s]</font>,<font color=%2$s>[%s]</font>}",
									 getLocaleText( "new_join" ),
									 Application.HTML_SHAPE_NAMES_COL,
									 elvis( pojojoin.shape1.getName(), "?" ),
									 elvis( pojojoin.shape2.getName(), "?" ) ),
				  color, null, stroke, transaction );
		}
	
		transaction.end();
		app.undoRedoSupport.postEdit( transaction );
		/////////////////////////////////////////////////////////////////////
		
		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}	
	
	private class POJOJoin
	{
		JDraftingShape shape1, shape2;
		Point2D join;
	}
}
