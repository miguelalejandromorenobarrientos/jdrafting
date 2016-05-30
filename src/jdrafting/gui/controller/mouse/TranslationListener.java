package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.sun.istack.internal.NotNull;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Translate selected shapes using mouse control 
 */
public class TranslationListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "translation_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D start;
	
	public TranslationListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_translate1" ) );
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
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// put start
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_translate2" ) );
		}
		// modify shapes by translation
		else
		{
			Point2D vt = getVector( logicMouse );
			Shape[] translatedShapes = 
				getTranslated( app.getSelectedShapes(), vt.getX(), vt.getY() );
			int index = 0;
			for ( JDraftingShape jdshape : app.getSelectedShapes() )
				jdshape.setShape( translatedShapes[index++] );
			
			app.undoSupport.postEdit( new EditTranslation( 
									new HashSet<>( app.getSelectedShapes() ), 
									vt.getX(), vt.getY() ) );
			
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
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// get transform
			AffineTransform transform = canvas.getTransform();

			// set tool style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Color.RED );			

			// draw shapes translated and direction vector
			Point2D vt = getVector( logicMouse );
			g2.draw( transform.createTransformedShape( 
						new Line2D.Double( start, sumVectors( start, vt ) ) ) );
			g2.setColor( Application.toolMainColor );			
			for ( Shape translated : getTranslated( app.getSelectedShapes(), 
													vt.getX(), vt.getY() ) )
				g2.draw( transform.createTransformedShape( translated ) );			
		}
	}
	
	
	// --- HELPERS ---
	
	/**
	 * Get translation vector
	 * @param logicMouse
	 * @return the translation vector
	 */
	private Point2D getVector( Point2D logicMouse )
	{
		return app.isUsingRuler()
			   ? adjustVectorToSize( vector( start, logicMouse ), 
					   				 app.getDistance() )
			   : vector( start, logicMouse ); 
	}
	
	/**
	 * Get rotated shapes
	 * @param logicMouse mouse logic position
	 * @return a list of new shapes
	 */
	private Shape[] getTranslated(@NotNull Set<JDraftingShape> selected, 
								  double tx, double ty )
	{
		AffineTransform translation = 
								AffineTransform.getTranslateInstance( tx, ty );
		
		return selected
			   .stream()
			   .map( jdshape ->
			   		translation.createTransformedShape( jdshape.getShape() ) )
			   .toArray( Shape[]::new );
	}
	
	/**
	 * UndoableEdit for undo/redo rotations 
	 */
	@SuppressWarnings("serial")
	private class EditTranslation extends AbstractUndoableEdit
	{
		private Set<JDraftingShape> selected;
		private double tx, ty;
		
		private EditTranslation( Set<JDraftingShape> selected, 
								 double tx, double ty )
		{
			this.selected = selected;
			this.tx = tx;
			this.ty = ty;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			Shape[] rotatedShapes = getTranslated( selected, -tx, -ty );
			int index = 0;
			for ( JDraftingShape jdshape : selected )
				jdshape.setShape( rotatedShapes[index++] );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			Shape[] rotatedShapes = getTranslated( selected, tx, ty );
			int index = 0;
			for ( JDraftingShape jdshape : selected )
				jdshape.setShape( rotatedShapes[index++] );
		}
		
		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getUndoPresentationName()
		{
			return "Undo translate selected (" + selected.size() + " shapes)";
		}
		@Override
		public String getRedoPresentationName()
		{
			return "Redo translate selected (" + selected.size() + " shapes)";
		}		
	}
}
