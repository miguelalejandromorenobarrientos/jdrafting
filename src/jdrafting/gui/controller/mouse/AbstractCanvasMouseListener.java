package jdrafting.gui.controller.mouse;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import jdrafting.gui.CanvasPanel;
import jdrafting.gui.Viewport;

public abstract class AbstractCanvasMouseListener extends MouseInputAdapter
												implements MouseWheelListener
{
	// last mouse event
	private MouseEvent lastMouseEvent;		
	
	public AbstractCanvasMouseListener( Component mouseEventSource )
	{
		// create initial MouseEvent
		Point mousePos = mouseEventSource.getMousePosition();
		if ( mousePos == null )
			mousePos = new Point( 0, 0 );
		lastMouseEvent = new MouseEvent( mouseEventSource, 
			MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0,
			mousePos.x, mousePos.y, 0, false, MouseEvent.NOBUTTON );
	}
	
	/**
	 * Get last mouse event
	 * @return the last mouse event
	 */
	final public MouseEvent mouse() { return lastMouseEvent; };
	
	/**
	 * Draw tool graphics over a given Graphics2D (tipically canvas graphics)
	 * @param g2 graphics to draw tool
	 */
	public void paintTool( Graphics2D g2 ) {}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		// zoom over canvas
		if ( e.getSource() instanceof CanvasPanel )
		{
			final CanvasPanel canvas = (CanvasPanel) e.getSource();
			
			// mouse logic viewport position
			final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
			
			// zoom in mouse position
			final double factor = Math.pow( 2, 1. / 4 );  // zoom factor
			final Viewport oldView = canvas.getViewport();
			// (avoid very small viewports due to accuracy bugs)
			if ( e.getPreciseWheelRotation() < 0 )
				if ( oldView.getWidth() < 1. || oldView.getHeight() < 1. )
					return;
			canvas.getViewport().zoom( logicMouse.getX(), logicMouse.getY(), 
						e.getPreciseWheelRotation() < 0 ? 1 / factor : factor );

			// update canvas
			canvas.repaint();
		}
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		mouseUserInfo( e );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		mouseUserInfo( e );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		mouseUserInfo( e );
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		mouseUserInfo( e );
	}
	
	protected void mouseUserInfo( MouseEvent e )
	{
		lastMouseEvent = e;
	}
}
