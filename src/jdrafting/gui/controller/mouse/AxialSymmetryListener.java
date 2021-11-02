package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;

import jdrafting.geom.JDMath;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Axial symmetry of selected shapes using mouse control 
 */
public class AxialSymmetryListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "axial_symmetry_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D start;
	
	public AxialSymmetryListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_axial_sym1" ) );
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

		// put start
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_axial_sym2" ) );
		}
		// modify shapes by axis symmetry
		else
		{
			// vector director of the axis
			final Point2D vector = JDMath.vector( start, logicMouse );
			
			// modify shapes by transform
			final Shape[] translatedShapes = getSymmetric( app.getSelectedShapes(), vector, start );
			int index = 0;
			for ( JDraftingShape jdshape : app.getSelectedShapes() )
				jdshape.setShape( translatedShapes[index++] );
			
			app.undoRedoSupport.postEdit( new EditSymmetry( 
											   new HashSet<>( app.getSelectedShapes() ), vector ) );
			
			app.scrollList.repaint();
			
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
			final Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// get transform
			final AffineTransform transform = canvas.getTransform();

			// draw transformed shapes and axis
			final Point2D vector = JDMath.vector( start, logicMouse );
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Color.RED ); // axis	
			g2.draw( transform.createTransformedShape( 
									new Line2D.Double( start, logicMouse ) ) );
			g2.setColor( Application.toolMainColor );  // transformed shapes			
			for ( Shape symmetric : getSymmetric( app.getSelectedShapes(), vector, start ) )
				g2.draw( transform.createTransformedShape( symmetric ) );			
		}
	}
	
	
	// --- HELPERS ---
	
	/**
	 * Get symmetryc shapes
	 * @param logicMouse mouse logic position
	 * @return a list of new shapes
	 */
	private Shape[] getSymmetric( Set<JDraftingShape> selected, Point2D vector, Point2D anchor )
	{
		/* Axial symmetry matrix
		 * [cos(2a),sin(2a),-tx(cos(2a)-1)-ty*sin(2a);
		 *  sin(2a),-cos(2a),ty(cos(2a)+1)-tx*sin(2a);
		 *  0,0,1]
		 */
		final AffineTransform symmetry = AffineTransform.getTranslateInstance( 
												anchor.getX(), anchor.getY() );
		symmetry.rotate( vector.getX(), vector.getY() );
		symmetry.scale( 1., -1. );		
		symmetry.rotate( vector.getX(), -vector.getY() );
		symmetry.translate( -anchor.getX(), -anchor.getY() );
		
		return selected
			   .stream()
			   .map( jdshape ->
			   		symmetry.createTransformedShape( jdshape.getShape() ) )
			   .toArray( Shape[]::new );
	}
	
	/**
	 * UndoableEdit for undo/redo symmetry
	 */
	@SuppressWarnings("serial")
	private class EditSymmetry extends AbstractUndoableEdit
	{
		private Set<JDraftingShape> selected;
		private Point2D vector;
		
		private EditSymmetry( Set<JDraftingShape> selected, Point2D vector ) 
		{
			this.selected = selected;
			this.vector = vector;
		}
		
		@Override
		public void undo() { redo(); }
		
		@Override
		public void redo()
		{
			final Shape[] symmetricShapes = getSymmetric( selected, vector, start );
			int index = 0;
			for ( final JDraftingShape jdshape : selected )
				jdshape.setShape( symmetricShapes[index++] );
		}
		
		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getPresentationName() 
		{
			return getLocaleText( "axial_sym" ) + " (" + selected.size() + " shapes)";
		}		
	}
}
