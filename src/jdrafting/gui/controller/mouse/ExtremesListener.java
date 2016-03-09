package jdrafting.gui.controller.mouse;

import static jdrafting.gui.Application.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

public class ExtremesListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
						CanvasPanel.getCustomCursor( "extremes_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public ExtremesListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_ext1" ) );
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
			// point style
			Color color = e.isShiftDown()
					      ? app.getColor()
					      : app.getPointColor();
			BasicStroke stroke = e.isShiftDown()
								 ? app.getStroke()
								 : app.getPointStroke();
			
			// get shape vertex
			List<Point2D> vertex = jdshape.getVertex();
			
			// ERROR; closed shapes doesn't have extreme vertex
			if ( jdshape.isClosed( vertex ) )
				JOptionPane.showMessageDialog( app, getLocaleText( "ext_dlg" ), 
					getLocaleText( "ext_title" ), JOptionPane.ERROR_MESSAGE );
			else
			{
				// get first and last vertex
				vertex = new ArrayList<Point2D>( Arrays.asList( 
						new Point2D[] { vertex.get( 0 ), 
										vertex.get( vertex.size() - 1 ) } ) );

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
						return
							"Redo add extremes (" + edits.size() + " points)";
					}
					@Override
					public String getUndoPresentationName()
					{
						return
							"Undo add extremes (" + edits.size() + " points)";
					}
				};
				
				// add extreme points to exercise
				vertex
				.stream()
				.forEach( point -> app.addShapeFromIterator(
					new JDPoint( point ).getPathIterator( null ), "",
					"> " + getLocaleText( "new_extreme" ) + " [" 
					+ jdshape.getName() + "]", color, stroke, transaction ) );

				transaction.end();
				app.undoSupport.postEdit( transaction );
				
				// refresh
				app.scrollList.repaint();
				
				// back to select mode
				canvas.setCanvasListener( new HandListener( canvas ) );
			}
		}
	}
}
