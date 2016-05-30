package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.nearInt;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.sun.istack.internal.NotNull;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Central symmety on selected shapes using mouse control 
 */
public class CentralSymmetryListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
					JDUtils.getCustomCursor( "central_symmetry_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public CentralSymmetryListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_central_sym1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		canvas.repaint();
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// modify shapes by central symmetry
		Shape[] symmetricShapes = 
							getSymmetric( app.getSelectedShapes(), logicMouse );
		int index = 0;
		for ( JDraftingShape jdshape : app.getSelectedShapes() )
			jdshape.setShape( symmetricShapes[index++] );
		
		app.undoSupport.postEdit( new EditSymmetric( 
					new HashSet<>( app.getSelectedShapes() ), logicMouse ) );
		
		app.scrollList.repaint();
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// get transforms
		AffineTransform transform = canvas.getTransform();

		Point2D center = canvas.adjustToPoint( mouse().getPoint() );
		Point2D canCenter = transform.transform( center, null );
		
		// draw symmetric shapes and center
		g2.setStroke( new BasicStroke( 1f ) );
		g2.setColor( Color.RED );
		g2.fillOval( nearInt( canCenter.getX() - 4 ), 
					 nearInt( canCenter.getY() - 4 ), 8, 8 );
		g2.setColor( Application.toolMainColor );			
		for ( Shape sym : getSymmetric( app.getSelectedShapes(), center ) )
			g2.draw( transform.createTransformedShape( sym ) );			
	}
	
	
	// --- HELPERS ---
	
	/**
	 * Get symmetric shapes
	 * @param logicMouse mouse logic position
	 * @return a list of new shapes
	 */
	private Shape[] getSymmetric(@NotNull Set<JDraftingShape> selected,
								 Point2D center )
	{
		AffineTransform symmetry = new AffineTransform();
		symmetry.translate( center.getX(), center.getY() );
		symmetry.scale( -1., -1. );
		symmetry.translate( -center.getX(), -center.getY() );
		
		return selected
			   .stream()
			   .map( jdshape ->
			   			symmetry.createTransformedShape( jdshape.getShape() ) )
			   .toArray( Shape[]::new );
	}
	
	/**
	 * UndoableEdit for undo/redo rotations 
	 */
	@SuppressWarnings("serial")
	private class EditSymmetric extends AbstractUndoableEdit
	{
		private Set<JDraftingShape> selected;
		private Point2D center; 
		
		private EditSymmetric( Set<JDraftingShape> selected, Point2D center )
		{
			this.selected = selected;
			this.center = center;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			Shape[] symmetricShapes = getSymmetric( selected, center );
			int index = 0;
			for ( JDraftingShape jdshape : selected )
				jdshape.setShape( symmetricShapes[index++] );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			undo();
		}
		
		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getUndoPresentationName()
		{
			return "Undo central symmetry of selected ("
				   + selected.size() + " shapes)";
		}
		@Override
		public String getRedoPresentationName()
		{
			return "Redo central symmetry of selected ("
				   + selected.size() + " shapes)";
		}		
	}
}
