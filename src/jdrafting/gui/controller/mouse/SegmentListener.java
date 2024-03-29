package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.pointRelativeToCenter;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.geom.JDPoint;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Create a segment using mouse control 
 */
public class SegmentListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "segment_cursor.png" );
	private static final double ANGLE_INTERVAL = Math.PI / 4.;
	private CanvasPanel canvas;
	private Application app;

	private Point2D start;
	
	public SegmentListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_seg1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( start != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_seg2" ) );
			canvas.repaint();
		}
		else
		{
			// add shape to exercise
			final Line2D line = getSegment( logicMouse );
			if ( addExtremes() )
			{
				//////////////////////////// TRANSACTION ////////////////////////////
				final JDCompoundEdit transaction = new JDCompoundEdit( 
										  getLocaleText( "segment" ) 
										  + (addExtremes() ? " <small>(& extremes)</small>" : "") );

				// add segment
				app.addShapeFromIterator( line.getPathIterator( null ),	"", 
										  getLocaleText( "new_segment" ), 
										  app.getColor(), null, app.getStroke(), transaction );
				// add extremes
				app.addShapeFromIterator( new JDPoint( line.getP1() ).getPathIterator( null ), "", 
								 		  getLocaleText( "new_segment_extreme" ) + " 1", 
								 		  app.getPointColor(), null, app.getPointStroke(), 
								 		  transaction );
				app.addShapeFromIterator( new JDPoint( line.getP2() ).getPathIterator( null ), "", 
										  getLocaleText( "new_segment_extreme" ) + " 2", 
										  app.getPointColor(), null, app.getPointStroke(), 
										  transaction );
				
				transaction.end();
				app.undoRedoSupport.postEdit( transaction );
				/////////////////////////////////////////////////////////////////////
			}
			else  // add segment
				app.addShapeFromIterator( line.getPathIterator( null ), "", 
										  getLocaleText( "new_segment" ), 
										  app.getColor(), null, app.getStroke() );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// draw segment
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );

			g2.draw( canvas.getTransform().createTransformedShape( 
												getSegment( logicMouse ) ) );
		}
	}
	
	
	// --- HELPERS ---

	// check modifiers
	private boolean isFixedAngle() { return mouse().isShiftDown(); }
	private boolean addExtremes() { return mouse().isControlDown(); }
	
	/**
	 * Get segment in logic viewport
	 * @param logicMouse
	 * @return the segment
	 */
	private Line2D getSegment( Point2D logicMouse )
	{
		Point2D end = logicMouse;
		
		// adjust to basic main angles
		if ( isFixedAngle() )
		{
			final double ang = Math.atan2( end.getY() - start.getY(), 
									 	   end.getX() - start.getX() ),
						 newAng = ANGLE_INTERVAL * Math.round( ang / ANGLE_INTERVAL );
			end = pointRelativeToCenter( start, newAng,	start.distance( end ) );
		}
		
		// fixed distance
		if ( app.isUsingRuler() )
			return new Line2D.Double( start, 
									  sumVectors( start, adjustVectorToSize( vector( start, end ), 
																		    app.getDistance() ) ) );
		// free distance
		return new Line2D.Double( start, end );
	}
}
