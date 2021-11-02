package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.centroid;
import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

public class MidpointListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "midpoint_cursor.png" );
	private Application app;
	private CanvasPanel canvas;
	
	public MidpointListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );

		app.setStatusText( getLocaleText( "txt_midpoint" ) );
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
			Point2D center;
			if ( isCentroid() )  // centroid
			{
				final List<Point2D> vertex = jdshape.getVertex();
				center = jdshape.isClosed( vertex )
						 ? centroid( vertex.subList( 0, vertex.size() - 1 ) )
						 : centroid( vertex );
			}
			else  // bounds center
			{
				final Rectangle2D bounds = jdshape.getShape().getBounds2D();
				center = new Point2D.Double( bounds.getCenterX(), bounds.getCenterY() );
			}

			// add point to exercise
			final Color color = isPointStyle() ? app.getColor() : app.getPointColor();
			final BasicStroke stroke = isPointStyle() ? app.getStroke() : app.getPointStroke();
			
			final String descHtml = String.format( "<font color=%s>[%s]</font>",
										 		   Application.HTML_SHAPE_NAMES_COL,
												   elvis( jdshape.getName(), "?" ) );

			//////////////////////////// TRANSACTION ////////////////////////////
			final JDCompoundEdit transaction = new JDCompoundEdit( 
					 getLocaleText( isCentroid() ? "centroid" : "midpoint" ) + " " + descHtml );
			
			app.addShapeFromIterator( new JDPoint( center ).getPathIterator( null ), "",
				   getLocaleText( isCentroid() ? "new_centroid" : "new_midpoint" ) + " " + descHtml, 
				   color, null, stroke, transaction );
			
			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	
	// --- HELPERS
	
	// check modifiers
	private boolean isCentroid() { return mouse().isControlDown(); }
	private boolean isPointStyle() { return mouse().isShiftDown(); }
}
