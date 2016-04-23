package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class FreeHandListener extends AbstractCanvasMouseListener 
{
	private static final Cursor UP_CURSOR = 
						JDUtils.getCustomCursor( "free_hand_up_cursor.png" );
	private static final Cursor DOWN_CURSOR = 
						JDUtils.getCustomCursor( "free_hand_down_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private Path2D path = new Path2D.Double();
	
	// drag parameters
	private boolean dragging = false;

	public FreeHandListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		canvas.setCursor( UP_CURSOR );

		app.setStatusText( JDUtils.getLocaleText( "txt_free1" ) );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		super.mousePressed( e );
		
		canvas.setCursor( DOWN_CURSOR );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		if ( dragging )
			path.lineTo( logicMouse.getX(), logicMouse.getY() );
		else
			path.moveTo( logicMouse.getX(), logicMouse.getY() );
		
		canvas.repaint();
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		super.mouseDragged( e );

		dragging = true;
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		path.lineTo( logicMouse.getX(), logicMouse.getY() );
		
		// refresh canvas
		canvas.repaint();		
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		if ( e.getClickCount() == 2 )
		{
			// add free polyline to exercise
			app.addShapeFromIterator( path.getPathIterator( null ),
									"", "", app.getColor(), app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
			return;
		}
		
		dragging = false;		
		canvas.setCursor( UP_CURSOR );
		
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// set tool style
		g2.setStroke( new BasicStroke( 1f ) );
		g2.setColor( Application.TOOL_MAIN_COLOR );

		// draw polyline
		g2.draw( canvas.getTransform().createTransformedShape( path ) );
	}
}
