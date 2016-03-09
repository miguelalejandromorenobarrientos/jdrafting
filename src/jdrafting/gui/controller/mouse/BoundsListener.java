package jdrafting.gui.controller.mouse;

import static jdrafting.gui.Application.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

/**
 * Create a rectangle bounds for a shape by mouse control 
 */
public class BoundsListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
						CanvasPanel.getCustomCursor( "bounds_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public BoundsListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_bounds1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( canvas.getShapeAtCanvasPoint( e.getPoint() ) == null )
			canvas.setCursor( CURSOR );
		else
			canvas.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
		canvas.repaint();
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// get shape
		JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape != null )
		{
			// add rectangle bounds to exercise
			app.addShapeFromIterator( 
				jdshape.getShape().getBounds2D().getPathIterator( null ), "",
				"> " + getLocaleText( "new_bounds" )
				+ " [" + jdshape.getName() + "]",
				app.getColor(), app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// get shape
		JDraftingShape jdshape = 
							canvas.getShapeAtCanvasPoint( mouse().getPoint() );
		if ( jdshape == null )  return;
		
		// set tool style
		g2.setStroke( new BasicStroke( 1f ) );
		g2.setColor( Application.TOOL_MAIN_COLOR );

		// draw bounds
		g2.draw( canvas.getTransform().createTransformedShape( 
										jdshape.getShape().getBounds2D() ) );
	}
}
