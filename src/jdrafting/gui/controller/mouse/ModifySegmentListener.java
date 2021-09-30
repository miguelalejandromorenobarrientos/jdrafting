package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.linesIntersection;
import static jdrafting.geom.JDMath.normal;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Modifies a segment using mouse control 
 */
public class ModifySegmentListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
						JDUtils.getCustomCursor( "modify_segment_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private JDraftingShape segment;
	private Point2D start;	
	
	public ModifySegmentListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		app.setStatusText( getLocaleText( "txt_modify1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		// adjust cursor over segments
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
				app.setStatusText( getLocaleText( "txt_modify2" ) );
				canvas.repaint();
			}
		}
		else
		{
			// get mouse logic position
			Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

			if ( start == null )
			{
				start = getSegment( logicMouse ).getP2();
				app.setStatusText( getLocaleText( "txt_modify3" ) );
				canvas.repaint();
			}
			else
			{
				// point style
				Color color = e.isShiftDown()
						      ? app.getColor()
						      : segment.getColor();
				BasicStroke stroke = e.isShiftDown()
									 ? app.getStroke()
									 : segment.getStroke();
				
				// new segment 
				Line2D newSegment = getSegment( logicMouse );
				JDraftingShape jdshape = JDraftingShape.createFromIterator(
						newSegment.getPathIterator( null ), segment.getName(), 
						segment.getDescription(), color, null, stroke );
				
				// replace segment
				int index = app.getExercise().indexOf( segment );
				app.getExercise().set( index, jdshape );
				app.shapeList.getModel().remove( index );
				app.shapeList.getModel().add( index, jdshape );
				
				// create undo/redo transaction
				@SuppressWarnings("serial")
				CompoundEdit transaction = new CompoundEdit() {
					@Override
					public boolean canRedo() { return true; };
					@Override
					public boolean canUndo() { return true; };
					@Override
					public String getRedoPresentationName()
					{
						return "Redo modify segment " + jdshape.toShortString();
					}
					@Override
					public String getUndoPresentationName()
					{
						return "Undo modify segment " + jdshape.toShortString();
					}
				};
				
				// remove old segment and add new segment to exercise
				transaction.addEdit( 
						app.new EditRemoveShapeFromExercise( segment, index ) );
				transaction.addEdit(
						app.new EditAddShapeToExercise( jdshape, index ) );
				
				transaction.end();
				app.undoSupport.postEdit( transaction );
				
				// refresh
				app.scrollList.repaint();

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
			// get logic mouse position
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// draw segment
			g2.setStroke( new BasicStroke( 3f ) );
			g2.setColor( Application.toolMainColor );
			g2.draw( canvas.getTransform().createTransformedShape( 
												getSegment( logicMouse ) ) );
		}
	}

	// --- HELPERS ---
	
	/**
	 * Get new segment in logic viewport
	 * @param logicMouse
	 * @return the new segment
	 */
	private Line2D getSegment( Point2D logicMouse )
	{
		List<Point2D> vertex = segment.getVertex();
		Point2D vertex1 = vertex.get( 0 );
		Point2D vertex2 = vertex.get( 1 );
		Point2D vector = vector( vertex1, vertex2 );
		Point2D normal = normal( vector );	
		
		Point2D p1 = start == null ? vertex1 : start;
		Point2D p2 = sumVectors( p1, vector );
		Point2D q1 = logicMouse;
		Point2D q2 = sumVectors( q1, normal );
		
		return new Line2D.Double( p1, linesIntersection( p1, p2, q1, q2 ) );
	}
}
