package jdrafting.gui.controller.mouse;

import static jdrafting.gui.Application.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

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
			JDPoint center;
			// TODO
//			// special case, arc
//			if ( jdshape.getShape() instanceof Arc2D )   
//			{
//				Point2D pcenter =
//							ShapeUtils.arcCenter( (Arc2D) jdshape.getShape() );
//				center = new JDPoint( pcenter );
//			}
//			// using shape bounds
//			else
			{
				Rectangle2D bounds = jdshape.getShape().getBounds2D();
				center = 
					new JDPoint( bounds.getCenterX(), bounds.getCenterY() );
			}
			// add point to exercise
			Color color =
						e.isShiftDown() ? app.getColor() : app.getPointColor();
			BasicStroke stroke = 
					e.isShiftDown() ? app.getStroke() : app.getPointStroke();
			app.addShapeFromIterator( center.getPathIterator( null ), "",
				"> " + getLocaleText( "new_midpoint" )
				+ " [" + jdshape.getName() + "]", color, stroke );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
}
