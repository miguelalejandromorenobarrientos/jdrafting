package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JOptionPane;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;
/**
 * Select two shapes and substract the second to the first 
 */
public class AreaSubstractListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "area_substract_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private JDraftingShape shape1, shape2;
	
	public AreaSubstractListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_area_substract1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );			
		if ( jdshape != null )
		{
			final List<Point2D> vertex = jdshape.getVertex();
			if ( !jdshape.isSegment( vertex ) && !jdshape.isPoint( vertex ) )
				canvas.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
		}
		else
			canvas.setCursor( CURSOR );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// select first shape
		if ( shape1 == null )
		{
			final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null )
			{
				final List<Point2D> vertex = jdshape.getVertex();
				if ( !jdshape.isSegment( vertex ) && !jdshape.isPoint( vertex ) )
				{	
					shape1 = jdshape;
					canvas.setCursor( CURSOR );
					app.setStatusText( getLocaleText( "txt_area_substract2" ) );
				}
			}
		}
		// select second shape
		else if ( shape2 == null )
		{
			final JDraftingShape jdshape =  canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null )
			{
				final List<Point2D> vertex = jdshape.getVertex();
				if ( !jdshape.isSegment( vertex ) && !jdshape.isPoint( vertex ) )
				{
					shape2 = jdshape;
					substract();
					// back to select mode
					canvas.setCanvasListener( new HandListener( canvas ) );
					return;
				}
			}
		}

		//canvas.repaint();
	}

	// --- HELPERS ---
	
	private void substract()
	{
		// create substract area
		final Area substractArea = new Area( shape1.getShape() );
		substractArea.subtract( new Area( shape2.getShape() ) );
		
		// check for empty intersection
		if ( substractArea.isEmpty() )
		{
			JOptionPane.showMessageDialog( app, getLocaleText( "empty_substract_error" ),
					   					   getLocaleText( "area_substract" ),
					   					   JOptionPane.ERROR_MESSAGE );
			return;
		}

		// final shape
		final Shape result = JDUtils.removeUnnecessarySegments( substractArea.isSingular()
							 ? JDUtils.closeShapeWithLine( substractArea, true )
							 : substractArea );		

		// shape enum for description
		final String descHtml = String.format( 
									 "(<font color=%3$s>%s</font>,<font color=%3$s>%s</font>)",
									 elvis( shape1.getName(), "?" ), elvis( shape2.getName(), "?" ),
									 Application.HTML_SHAPE_NAMES_COL );
		
		//////////////////////////// TRANSACTION ////////////////////////////
		final JDCompoundEdit transaction = new JDCompoundEdit( 
											   getLocaleText( "area_substract" ) + " " + descHtml );
		
		// add new shape to exercise
		app.addShapeFromIterator( result.getPathIterator( null ), "", 
							   getLocaleText( "new_substract" ) + " " + descHtml, app.getColor(), 
							   null, app.getStroke(), transaction );

		transaction.end();
		app.undoRedoSupport.postEdit( transaction );
		/////////////////////////////////////////////////////////////////////
		
		// refresh
		app.getCanvas().repaint();
		app.scrollList.repaint();
	}
	
}
