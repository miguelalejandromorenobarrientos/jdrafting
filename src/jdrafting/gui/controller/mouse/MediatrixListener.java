package jdrafting.gui.controller.mouse;

import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.geom.JDMath.linesIntersection;
import static jdrafting.geom.JDMath.midpoint;
import static jdrafting.geom.JDMath.normal;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

/**
 * Creates a mediatrix segment using mouse control 
 */
public class MediatrixListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
						CanvasPanel.getCustomCursor( "mediatrix_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private JDraftingShape segment;
	private Point2D start;
	
	public MediatrixListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_mediatrix1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		if ( segment == null )
		{
			JDraftingShape jdshape =
								canvas.getShapeAtCanvasPoint( e.getPoint() );			
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
				canvas.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
			else
				canvas.setCursor( CURSOR );
		}
		else
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// set segment
		if ( segment == null )
		{
			JDraftingShape jdshape =
								canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
			{
				segment = jdshape;
				canvas.setCursor( CURSOR );
				app.setStatusText( getLocaleText( "txt_mediatrix2" ) );
			}
		}
		else 
		{
			// get mouse logic position
			Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

			// set start point
			if ( start == null )
			{
				start = getMediatrix( logicMouse ).getP1();
				app.setStatusText( getLocaleText( "txt_mediatrix3" ) );
			}
			// set perpendicular segment
			else
			{
				Line2D perp = getMediatrix( logicMouse );
				app.addShapeFromIterator( perp.getPathIterator( null ), "", 
										"> " + getLocaleText( "new_mediatrix" )
										+ " [" + segment.getName() + "]", 
										app.getColor(), app.getStroke() );
				
				// back to select mode
				canvas.setCanvasListener( new HandListener( canvas ) );
			}
		}
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( segment != null )
		{
			// get logic mouse position
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// draw perpendicular
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.TOOL_MAIN_COLOR );
			g2.draw( canvas.getTransform().createTransformedShape( 
												getMediatrix( logicMouse ) ) );
		}
	}
	
	// --- HELPERS ---
	
	/**
	 * Gets a mediatrix segment to another in the logic viewport
	 * @param logicMouse
	 * @return the mediatrix
	 */
	private Line2D getMediatrix( Point2D logicMouse )
	{
		List<Point2D> vertex = segment.getVertex();
		Point2D vertex1 = vertex.get( 0 );
		Point2D vertex2 = vertex.get( 1 );
		Point2D midpoint = midpoint( vertex1, vertex2 );
		Point2D vector = vector( vertex1, vertex2 ); 
		Point2D normal = normal( vector );
		
		Point2D p1 = start == null
					 ? linesIntersection( midpoint,
							 			  sumVectors( midpoint, normal ),
							 			  logicMouse,
							 			  sumVectors( logicMouse, vector ) )							 
					 : start;
		Point2D p2 = sumVectors( p1, normal );
		Point2D q1 = start == null ? vertex1 : logicMouse;
		Point2D q2 = sumVectors( q1, vector );
		
		return new Line2D.Double( p1, linesIntersection( p1, p2, q1, q2 ) );
	}
}
