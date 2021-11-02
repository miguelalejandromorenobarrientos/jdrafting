package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.List;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

public class FragmentListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "hammer_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public FragmentListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_fragment1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		canvas.setCursor( canvas.getShapeAtCanvasPoint( e.getPoint() ) == null
						  ? CURSOR
						  : new Cursor( Cursor.HAND_CURSOR ) );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape != null )
		{
			// get shape vertex
			final List<Line2D> segments = jdshape.getSegments();
			
			// fragment except points or segments
			if ( !segments.isEmpty() )
			{
				final String descHtml = String.format( "<font color=%s>[%s]</font>",
													   Application.HTML_SHAPE_NAMES_COL, 
													   elvis( jdshape.getName(), "?" ) );
				
				//////////////////////////// TRANSACTION ////////////////////////////				
				@SuppressWarnings("serial")
				final JDCompoundEdit transaction = new JDCompoundEdit() {
					public String getPresentationName()
					{
						return getLocaleText( "fragment" ) + " " + descHtml 
							   + " (" + (edits.size() - 1) + " shapes)";
					}
				};

				// delete shape from exercise
				int index = app.getExercise().removeShape( jdshape );
				app.shapeList.getModel().remove( index );
				transaction.addEdit( app.new EditRemoveShapeFromExercise( jdshape, index ) );

				// create segments from sides
				final BasicStroke stroke = e.isShiftDown() ? app.getStroke() : jdshape.getStroke();
				final Color color = e.isShiftDown() ? app.getColor() : jdshape.getColor();
				for ( final Line2D segment : segments )
				{
					// create side and add it from [index] position
					final JDraftingShape side = JDraftingShape.createFromIterator(
												   segment.getPathIterator( null ), "",
												   getLocaleText( "new_fragment" ) + " " + descHtml,
												   color, null, stroke );
					app.getExercise().addShape( index, side );
					app.shapeList.getModel().add( index, side );
					transaction.addEdit( app.new EditAddShapeToExercise( side, index++ ) );
				}

				transaction.end();
				app.undoRedoSupport.postEdit( transaction );
				/////////////////////////////////////////////////////////////////////
				
				app.scrollList.repaint();				
				
				// back to select mode
				canvas.setCanvasListener( new HandListener( canvas ) );
			}
		}		
	}
}
