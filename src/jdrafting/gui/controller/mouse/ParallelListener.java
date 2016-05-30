package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.linesIntersection;
import static jdrafting.geom.JDMath.midpoint;
import static jdrafting.geom.JDMath.normal;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.gui.JDUtils.getLocaleText;

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
import jdrafting.gui.JDUtils;

public class ParallelListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "parallel_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private JDraftingShape segment;
	private Point2D start;
	
	public ParallelListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_para1" ) );
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
		
		if ( segment == null )
		{
			JDraftingShape jdshape =
								canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
			{
				segment = jdshape;
				canvas.setCursor( CURSOR );
				app.setStatusText( getLocaleText( "txt_para2" ) );
				canvas.repaint();
			}
		}
		else 
		{
			// mouse logic position
			Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

			// set start
			if ( start == null )
			{
				start = getParallel( logicMouse ).getP1();			
				app.setStatusText( getLocaleText( "txt_para3" ) );
				canvas.repaint();
			}
			// set parallel
			else
			{
				// add parallel to exercise
				Line2D parallel = getParallel( logicMouse );
				app.addShapeFromIterator( parallel.getPathIterator( null ), "",
						"> " + getLocaleText( "new_parallel" )
						+ " [" + segment.getName() + "]", 
						app.getColor(), app.getStroke() );
				
				// back to select mode
				canvas.setCanvasListener( new HandListener( canvas ) );
			}
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( segment != null )
		{
			// mouse logic position
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// draw parallel
			g2.setStroke( new BasicStroke( 1f, BasicStroke.CAP_ROUND, 
													BasicStroke.JOIN_ROUND ) );
			g2.setColor( Application.toolMainColor );
			g2.draw( canvas.getTransform().createTransformedShape( 
												getParallel( logicMouse ) ) );
		}
	}	
	
	// --- HELPERS ---
	
	private Line2D getParallel( Point2D logicMouse )
	{
		List<Point2D> vertex = segment.getVertex();
		Point2D vertex1 = vertex.get( 0 );
		Point2D vertex2 = vertex.get( 1 );
		Point2D vector = vector( vertex1, vertex2 );
		Point2D normal = normal( vector );

		Point2D point = app.isUsingRuler() 
					? getFixedDistanceStart( logicMouse , app.getDistance() )
					: logicMouse;
		Point2D p1 = start == null ? point : start;
		Point2D p2 = sumVectors( p1, vector );
		Point2D q1 = start == null ? midpoint( vertex1, vertex2 ) : logicMouse;
		Point2D q2 = sumVectors( q1, normal );
		
		return new Line2D.Double( p1, linesIntersection( p1, p2, q1, q2 ) );
	}
	
	private Point2D getFixedDistanceStart( Point2D logicMouse, double distance )
	{
		List<Point2D> vertex = segment.getVertex();
		Point2D vertex1 = vertex.get( 0 );
		Point2D vertex2 = vertex.get( 1 );
		Point2D normal = normal( vector( vertex1, vertex2 ) );
		
		Point2D foot = linesIntersection( vertex1, vertex2, logicMouse, 
										  sumVectors( logicMouse, normal ) );
		return sumVectors( foot, adjustVectorToSize( 
									vector( foot, logicMouse ), distance ) );
	}
}
