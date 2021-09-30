package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Rectangular selection by mouse control 
 */
public class SelectionListener extends AbstractCanvasMouseListener
{
	private CanvasPanel canvas;
	private Application app;
	
	private Set<JDraftingShape> originalSelection = null;
	private Point2D start = null;
	private Rectangle2D recSelection = null;
	
	public SelectionListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );

		app.setStatusText( JDUtils.getLocaleText( "txt_sel1" ) );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		super.mousePressed( e );

		// set start and keep original selection or new
		start = canvas.getInverseTransform().transform( e.getPoint(), null );
		originalSelection = e.isShiftDown()  // add to previous or not
							? new HashSet<>( app.getSelectedShapes() )
							: new HashSet<>();
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		super.mouseDragged( e );
		
		// mouse position in logic viewport
		Point2D position = canvas.getInverseTransform().transform( 
														e.getPoint(), null );

		// get selection rectangle
		double minX = Math.min( start.getX(), position.getX() );
		double maxX = Math.max( start.getX(), position.getX() );
		double minY = Math.min( start.getY(), position.getY() );
		double maxY = Math.max( start.getY(), position.getY() );		
		recSelection = 
				new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );

		// get selected shapes
		app.setSelectedShapes( new HashSet<>( originalSelection ) );
		for ( JDraftingShape jdshape : app.getExercise().getFramesUntilIndex() )
		{
			// select if all area contained inside selection
			if ( e.isControlDown() )  
			{
				if ( recSelection.contains( jdshape.getShape().getBounds2D() ) )
					app.getSelectedShapes().add( jdshape );
			}
			// select if shape intersects selection
			else
				for ( Line2D segment : jdshape.getSegments() )
					if ( segment.intersects( recSelection ) )
					{
						app.getSelectedShapes().add( jdshape );
						break;
					}
		}
		
		// refresh cursor and canvas
		canvas.setCursor( ( position.getX() - start.getX() ) 
					 	  * ( position.getY() - start.getY() ) > 0
					 	  	? new Cursor( Cursor.NE_RESIZE_CURSOR )
					 	  	: new Cursor( Cursor.NW_RESIZE_CURSOR ) );		
		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		app.scrollList.repaint();

		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );		
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// draw selection rectangle
		if ( recSelection != null )
		{
			g2.setColor( RECTANGLE_COLOR );
			g2.setStroke( RECTANGLE_STROKE );
			g2.draw( 
				canvas.getTransform().createTransformedShape( recSelection ) );
		}
	}
	
	private static final Color RECTANGLE_COLOR = new Color( 10, 160, 10 );
	private static final BasicStroke RECTANGLE_STROKE = new BasicStroke( 
						1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, 
						new float[] { 7f, 5f }, 0f ); 
}
