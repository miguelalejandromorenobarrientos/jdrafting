package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

public class SelectionListener extends AbstractCanvasMouseListener
{
	private CanvasPanel canvas;
	private Application app;
	
	private Set<JDraftingShape> originalSelection = null;
	private Point2D start = null;
	private Rectangle2D selection = null;
	
	public SelectionListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );

		app.setStatusText( Application.getLocaleText( "txt_sel1" ) );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		super.mousePressed( e );

		start = canvas.getInverseTransform().transform( e.getPoint(), null );
		originalSelection = e.isShiftDown()
							? new HashSet<>( app.getSelectedShapes() )
							: new HashSet<>();
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		super.mouseDragged( e );
		
		Point2D position = canvas.getInverseTransform().transform( 
														e.getPoint(), null );
		
		double minX = Math.min( start.getX(), position.getX() );
		double maxX = Math.max( start.getX(), position.getX() );
		double minY = Math.min( start.getY(), position.getY() );
		double maxY = Math.max( start.getY(), position.getY() );
		
		selection = 
			new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );

		app.setSelectedShapes( new HashSet<>( originalSelection ) );
		for ( JDraftingShape jdshape : app.getExercise().getShapes() )
			if ( e.isControlDown()
			     ? selection.contains( jdshape.getShape().getBounds2D() )
				 : jdshape.getShape().intersects( selection ) )
				app.getSelectedShapes().add( jdshape );

		if ( ( position.getX() - start.getX() ) 
			 * ( position.getY() - start.getY() ) > 0 )
			canvas.setCursor( new Cursor( Cursor.NE_RESIZE_CURSOR ) );
		else
			canvas.setCursor( new Cursor( Cursor.NW_RESIZE_CURSOR ) );
		
		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );		
		
		app.scrollList.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( selection != null )
		{
			g2.setColor( new Color( 10, 160, 10 ) );
			g2.setStroke( new BasicStroke( 1.5f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1f, new float[] { 7f, 5f }, 0f ) );
			g2.draw( 
				canvas.getTransform().createTransformedShape( selection ) );
		}
	}
}
