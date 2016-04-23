package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class RulerListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
								JDUtils.getCustomCursor( "ruler_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D start;
	
	public RulerListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_ruler1" ) );
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved( e );
		
		if ( start != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_ruler2" ) );
		}
		else
		{
			// capture distance with ruler
			app.setDistance( start.distance( logicMouse ) );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );			
		}

		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// draw line
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.TOOL_MAIN_COLOR );	
			g2.draw( canvas.getTransform().createTransformedShape( 
									new Line2D.Double( start, logicMouse ) ) );
		}
	}
}
