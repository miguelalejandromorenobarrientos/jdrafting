package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class VertexListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "vertex_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public VertexListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_vtx1" ) );
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
			if ( jdshape.isClosed( vertex ) )
				vertex = vertex.subList( 0, vertex.size() - 1 );

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
					return "Redo add vertex (" + edits.size() + " points)";
				}
				public String getUndoPresentationName()
				{
					return "Undo add vertex (" + edits.size() + " points)";
				}
			};
			
			// add points to every vertex
			vertex
			.stream()
			.forEach( point -> app.addShapeFromIterator(
									new JDPoint( point ).getPathIterator( null ), "",
									"> " + getLocaleText( "new_vertex" ) + " ["
									+ jdshape.getName() + "]", color, null, stroke, transaction ) );
			
			transaction.end();
			app.undoSupport.postEdit( transaction );
			
			// refresh
			app.scrollList.repaint();
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}		
	}
}
