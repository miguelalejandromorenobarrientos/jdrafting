package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class FragmentListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
								JDUtils.getCustomCursor( "hammer_cursor.png" );
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
		
		if ( canvas.getShapeAtCanvasPoint( e.getPoint() ) == null )
			canvas.setCursor( CURSOR );
		else
			canvas.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape != null )
		{
			// get shape vertex
			List<Line2D> segments = jdshape.getSegments();

			// fragment except points or segments
			if ( !segments.isEmpty() )
			{
				// create undo/redo transaction
				@SuppressWarnings("serial")
				CompoundEdit transaction = new CompoundEdit() {
					@Override
					public boolean canRedo() { return true; };
					@Override
					public boolean canUndo() { return true; };
					@Override
					public String getRedoPresentationName()
					{
						return "Redo Fragment (" + ( edits.size() - 1 ) 
							   + " shapes)";
					}
					@Override
					public String getUndoPresentationName()
					{
						return "Undo Fragment (" + ( edits.size() - 1 ) 
							   + " shapes)";
					}
				};

				// delete shape from exercise
				int index = app.getExercise().removeShape( jdshape );
				app.shapeList.getModel().remove( index );
				transaction.addEdit( 
						app.new EditRemoveShapeFromExercise( jdshape, index ) );

				// create segments from sides
				BasicStroke stroke = 
						e.isShiftDown() ? app.getStroke() : jdshape.getStroke();
				Color color = 
						e.isShiftDown() ? app.getColor() : jdshape.getColor();
				for ( Line2D segment : segments )
				{
					JDraftingShape side = JDraftingShape.createFromIterator(
										segment.getPathIterator( null ), "",
										"> " + getLocaleText( "new_fragment" )
										+ " [" + jdshape.getName() + "]",
										color, stroke );
					app.getExercise().addShape( index, side );
					app.shapeList.getModel().add( index, side );
					transaction.addEdit(
							app.new EditAddShapeToExercise( side, index++ ) );
				}

				transaction.end();
				app.undoSupport.postEdit( transaction );
				
				app.scrollList.repaint();				
				
				// back to select mode
				canvas.setCanvasListener( new HandListener( canvas ) );
			}
		}		
	}
}
