package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class PaintListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "paint_cursor.png" );
	private Application app;
	private CanvasPanel canvas;
	
	public PaintListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		app.setStatusText( JDUtils.getLocaleText( "txt_paint1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape == null )
			canvas.setCursor( CURSOR );
		else
		{
			final List<Point2D> vertex = jdshape.getVertex();		
			canvas.setCursor( jdshape.isPoint( vertex ) || jdshape.isSegment( vertex )
							  ? CURSOR
							  : new Cursor( Cursor.HAND_CURSOR ) );
		}
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// get shape
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );		
		if ( jdshape == null )  return;

		// don't apply to segments or points
		final List<Point2D> vertex = jdshape.getVertex();		
		if ( jdshape.isPoint( vertex ) || jdshape.isSegment( vertex ) )
			return;

		// set fill color
		final Color newColor = isDeleteColor() 
							   ? null  // no fill
							   : isPointColor() 
							     ? app.getPointColor() 
							     : isLineColor() ? app.getColor() : app.getFill(), 
				    oldColor = jdshape.getFill();
		jdshape.setFill( newColor );
		
		// add undo/redo edit
		app.undoRedoSupport.postEdit( new EditPaint( jdshape, newColor, oldColor ) );
		
		// refresh
		app.scrollList.repaint();
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	
	
	// --- HELPERS
	private boolean isLineColor() { return mouse().isAltDown(); }
	private boolean isPointColor() { return mouse().isShiftDown(); }	
	private boolean isDeleteColor() { return mouse().isControlDown(); }
	
	
	@SuppressWarnings("serial")
	private class EditPaint extends AbstractUndoableEdit
	{
		private JDraftingShape jdshape;
		private Color newColor, oldColor;
		
		public EditPaint( JDraftingShape jdshape, Color newColor, Color oldColor )
		{
			this.jdshape = jdshape;
			this.newColor = newColor;
			this.oldColor = oldColor;
		}
		
		@Override
		public void undo()
		{
			jdshape.setFill( oldColor );
		}
		
		@Override
		public void redo()
		{
			jdshape.setFill( newColor );
		}

		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getPresentationName() 
		{
			return String.format( "%s <font color=%s>[%s]</font> ",
								  getLocaleText( "paint" ),
								  Application.HTML_SHAPE_NAMES_COL,
								  elvis( jdshape.getName(), "?" ) );
		}
	}
}
