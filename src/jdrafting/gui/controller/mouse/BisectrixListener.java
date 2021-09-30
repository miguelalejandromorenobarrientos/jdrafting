package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.linesIntersection;
import static jdrafting.geom.JDMath.normal;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.geom.JDMath.vectorArg;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JOptionPane;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create bisectrix segment using mouse control 
 */
public class BisectrixListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "bisectrix_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private JDraftingShape seg1, seg2;
	private Point2D bis1;
	
	public BisectrixListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_bisectrix1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( seg2 == null )
		{
			JDraftingShape jdshape =
								canvas.getShapeAtCanvasPoint( e.getPoint() );			
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
				canvas.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
			else
				canvas.setCursor( CURSOR );
		}
		if ( seg1 != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// select first segment
		if ( seg1 == null )
		{
			JDraftingShape jdshape =
								canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
			{
				seg1 = jdshape;
				canvas.setCursor( CURSOR );
				app.setStatusText( getLocaleText( "txt_bisectrix2" ) );
			}
			else
				return;
		}
		// select second segment
		else if ( seg2 == null )
		{
			JDraftingShape jdshape =
								canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
			{
				seg2 = jdshape;
				Line2D bisectrix = getBisectrix( logicMouse ); 
				if ( bisectrix == null )  // parallel or coincident
				{
					// error message
					JOptionPane.showMessageDialog( app, 
											getLocaleText( "bisectrix_dlg" ),
											getLocaleText( "bisectrix_title" ),
											JOptionPane.ERROR_MESSAGE );
					// back to select mode
					canvas.setCanvasListener( new HandListener( canvas ) );
					return;
				}
				
				canvas.setCursor( CURSOR );
				app.setStatusText( getLocaleText( "txt_bisectrix3" ) );
			}
			else
				return;
		}
		else if ( bis1 == null )
		{
			Line2D bisectrix = getBisectrix( logicMouse ); 
			bis1 = bisectrix.getP2();
			app.setStatusText( getLocaleText( "txt_bisectrix4" ) );
		}
		else
		{
			Line2D bisectrix = getBisectrix( logicMouse );
			app.addShapeFromIterator( bisectrix.getPathIterator( null ), "", 
					"> " + getLocaleText( "new_bisectrix" )
					+ " [" + seg1.getName() + "," + seg2.getName() + "]", 
					app.getColor(), null, app.getStroke() );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
		canvas.repaint();
	}

	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( seg1 == null )  return;
		
		AffineTransform transform = canvas.getTransform();
		
		g2.setColor( Application.toolMainColor );
		g2.setStroke( new BasicStroke( seg1.getStroke().getLineWidth() ) );
		// mark first segment
		g2.draw( transform.createTransformedShape( seg1.getShape() ) );
			
		if ( seg2 != null )
		{
			// mark second segmen
			g2.draw( transform.createTransformedShape( seg2.getShape() ) );
			
			// get logic mouse position
			Point2D logicMouse = 
				canvas.adjustToPoint( mouse().getPoint() );		
			
			// get bisectrix
			Line2D bisectrix = getBisectrix( logicMouse );
			if ( bisectrix == null )  return;
			// draw bisectrix
			g2.setStroke( new BasicStroke( 1f ) );
			g2.draw( 
					canvas.getTransform().createTransformedShape( bisectrix ) );
		}
	}	
	
	// --- HELPERS ---
	
	private Line2D getBisectrix( Point2D logicMouse )
	{
		// get segment vertex and director vectors
		List<Point2D> vertexList = seg1.getVertex();
		Point2D vertex1 = vertexList.get( 0 );
		Point2D vertex2 = vertexList.get( 1 );
		Point2D v1 = vector( vertex1, vertex2 );
		vertexList = seg2.getVertex();
		Point2D vertex3 = vertexList.get( 0 );
		Point2D vertex4 = vertexList.get( 1 );
		Point2D v2 = vector( vertex3, vertex4 );
		
		// get intersection point
		Point2D intersection =
						linesIntersection( vertex1, vertex2, vertex3, vertex4 );
		if ( intersection == null )  // parallel or coincident
			return null;
		
		// calculate bisectrix angle
		double ang = ( vectorArg( v1 ) + vectorArg( v2 ) ) / 2.;
		int region = new Line2D.Double( vertex1, vertex2 )
						.relativeCCW( logicMouse ) 
				   * new Line2D.Double( vertex3, vertex4 )
				   		.relativeCCW( logicMouse );
		if ( region > 0 )  // determinate bisectrix 1 or 2 (ortogonal)
			ang += Math.PI / 2.;		

		// get bisectrix extremes
		Point2D p1 = bis1 == null ? intersection : bis1;
		Point2D p2 = bis1 == null || bis1.distance( intersection ) < 0.000001
				? pointRelativeToCenter( p1, ang, 1. ) 
				: intersection;
		Point2D normal = normal( vector( p1, p2 ) );
		Point2D q1 = logicMouse;
		Point2D q2 = sumVectors( q1, normal );
		
		Point2D bis2 = linesIntersection( p1, p2, q1, q2 );
		if ( bis2 == null )  return null;  // parallel or coincident
		
		return new Line2D.Double( p1, bis2 );
	}
}
