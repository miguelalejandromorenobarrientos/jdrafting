package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
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
			// point style
			final Color color = isPointStyle()
								? app.getPointColor()
								: app.getColor();
			final BasicStroke stroke = isPointStyle()
									   ? app.getPointStroke()
									   : app.getStroke();
			
			// get shape vertex
			List<Point2D> vertex = jdshape.getVertex();
			if ( jdshape.isClosed( vertex ) )
				vertex = vertex.subList( 0, vertex.size() - 1 );

			final String descHtml = String.format( "<font color=%s>[%s]</font>", 
												   Application.HTML_SHAPE_NAMES_COL,
												   elvis( jdshape.getName(), "?" ) );
			
			//////////////////////////// TRANSACTION ////////////////////////////
			@SuppressWarnings("serial")
			final JDCompoundEdit transaction = new JDCompoundEdit() {
				public String getPresentationName() 
				{
					return getLocaleText( "vertex" ) + " " + descHtml 
						   + " (" + edits.size() + " points)";
				}
			};
			
			// add points to every vertex
			vertex
			.stream()
			.forEach( point -> app.addShapeFromIterator(
										new JDPoint( point ).getPathIterator( null ), "",
										getLocaleText( "new_vertex" ) + " " + descHtml, color, null, 
										stroke, transaction ) );
			
			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////
			
			// refresh
			app.scrollList.repaint();
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}		
	}
	
	private boolean isPointStyle() { return !mouse().isShiftDown(); }
}
