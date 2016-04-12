package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.centroid;
import static jdrafting.gui.Application.getLocaleText;

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

public class MidpointListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
						CanvasPanel.getCustomCursor( "midpoint_cursor.png" );
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
			Point2D center;
			if ( isCentroid() )  // centroid
			{
				List<Point2D> vertex = jdshape.getVertex();
				center = jdshape.isClosed( vertex )
						 ? centroid( vertex.subList( 0, vertex.size() - 1 ) )
						 : centroid( vertex );
			}
			else  // bounds center
			{
				Rectangle2D bounds = jdshape.getShape().getBounds2D();
				center = new Point2D.Double( bounds.getCenterX(), 
											 bounds.getCenterY() );
			}

			// add point to exercise
			Color color =
						e.isShiftDown() ? app.getColor() : app.getPointColor();
			BasicStroke stroke = 
					e.isShiftDown() ? app.getStroke() : app.getPointStroke();
			app.addShapeFromIterator( 
							new JDPoint( center ).getPathIterator( null ), "",
							"> " + getLocaleText( "new_midpoint" )
							+ " [" + jdshape.getName() + "]", color, stroke );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	
	// --- HELPERS
	
	// check modifiers
	private boolean isCentroid() { return mouse().isControlDown(); }
}
