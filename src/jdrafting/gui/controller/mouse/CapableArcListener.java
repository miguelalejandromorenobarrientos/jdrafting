package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.linesIntersection;
import static jdrafting.geom.JDMath.midpoint;
import static jdrafting.geom.JDMath.normal;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.geom.JDMath.vectorArg;
import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDMath;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Create capable arc of a segment using mouse control 
 */
public class CapableArcListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "capable_arc_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private JDraftingShape segment;
	
	public CapableArcListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_cap1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		// Mark segments when the mouse is over them
		if ( segment == null )
		{
			final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );			
			canvas.setCursor( jdshape != null && jdshape.isSegment( jdshape.getVertex() )
							  ? new Cursor( Cursor.HAND_CURSOR ) 
							  : CURSOR );
		}
		else
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// select segment
		if ( segment == null )
		{
			final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
			if ( jdshape != null && jdshape.isSegment( jdshape.getVertex() ) )
			{
				segment = jdshape;
				canvas.setCursor( CURSOR );
				app.setStatusText( getLocaleText( "txt_cap2" ) );
				canvas.repaint();
			}
		}
		// add one of the two capable arcs
		else
		{
			final String descHtml = String.format( "<font color=%s>[%s]</font> <small>(%.1f�)</small>",
												   Application.HTML_SHAPE_NAMES_COL, 
												   elvis( segment.getName(), "?" ),
												   app.getAngle() );
			// add capable arc to exercise
			final Arc2D arc = getArc( logicMouse );
			final double flatness = JDMath.length( arc, null )/*arc.getWidth()*/ / app.getFlatnessValue();
			
			//////////////////////////// TRANSACTION ////////////////////////////
			final CompoundEdit transaction = new JDCompoundEdit( 
												  getLocaleText( "capable_arc" ) + " " + descHtml );
			
			app.addShapeFromIterator( arc.getPathIterator( null, flatness ), "", 
									  getLocaleText( "new_capable_arc" ) + " " + descHtml, 
									  app.getColor(), null, app.getStroke(), transaction );
			
			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( segment == null )  return;
		
		final AffineTransform transform = canvas.getTransform();
		
		g2.setColor( Application.toolMainColor );
		g2.setStroke( new BasicStroke( 1f ) );
		
		// draw capable arc
		final Arc2D arc = getArc( CanvasPanel.getInverseTransform( transform ).transform( 
																	   mouse().getPoint(), null ) );
		g2.draw( transform.createTransformedShape( arc ) );
	}	
	
	// --- HELPERS ---

	/**
	 * Get capable arc
	 * @param logicMouse
	 * @return the capable arc
	 */
	private Arc2D getArc( Point2D logicMouse )
	{
		final List<Point2D> vertex = segment.getVertex();
		final Point2D p1 = vertex.get( 0 ), p2 = vertex.get( 1 );
		final int relative = new Line2D.Double( p1, p2 ).relativeCCW( logicMouse );
		final double ang = Math.toRadians( 90 + relative * app.getAngle() );
		final Point2D midpoint = midpoint( p1, p2 ),
					  normal = normal( vector( p1, p2 ) ),
					  aux = pointRelativeToCenter( p1, vectorArg( vector( p1, p2 ) ) + ang, 1. );
		Point2D center = linesIntersection( midpoint, sumVectors( midpoint, normal ), p1, aux );
		if ( center == null )  // 90�
			center = midpoint;
		final double radius = center.distance( p1 );
		
		final double startAng = -vectorArg( relative < 0 
											? vector( center, p2 ) 
											: vector( center, p1 ) );  
		
		return new Arc2D.Double( center.getX() - radius, center.getY() - radius,
								 2 * radius, 2 * radius,
								 Math.toDegrees( startAng ),
								 -( 360 - app.getAngle() * 2 ),
								 Arc2D.OPEN );
	}
}
