package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import jdrafting.geom.JDStrokes;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

public class PasteStyleListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "paste_style_cursor.png" );
	private Application app;
	private CanvasPanel canvas;
	
	public PasteStyleListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		app.setStatusText( JDUtils.getLocaleText( "txt_paste_style1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( canvas.getShapeAtCanvasPoint( e.getPoint() ) == null )
			canvas.setCursor( CURSOR );
		else
			canvas.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// get shape
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );		
		if ( jdshape == null )  return;

		// set color and stroke
		final boolean isPoint = jdshape.isPoint( jdshape.getVertex() );
		Color newColor, oldColor = jdshape.getColor(), newFill = null, oldFill = jdshape.getFill();
		BasicStroke newStroke, oldStroke = jdshape.getStroke();
		if ( isPointStyle() )
		{
			jdshape.setColor( newColor = app.getPointColor() );
			jdshape.setStroke( newStroke = isPoint 
							   ? app.getPointStroke() 
							   : JDStrokes.getStroke( JDStrokes.PLAIN_ROUND.getStroke(),
													  app.getPointStroke().getLineWidth() ) );
		}
		else
		{
			jdshape.setColor( newColor = app.getColor() );
			jdshape.setFill( newFill = isFill() ? app.getFill() : null );
			jdshape.setStroke( newStroke = isPoint 
							   ? JDStrokes.cloneStrokeStyle( app.getStroke().getLineWidth(),
											  				 app.getPointStroke() ) 
							   : app.getStroke() );
		}
					
		// add undo/redo edit
		app.undoRedoSupport.postEdit( new EditPasteStyle( 
						jdshape, newColor, oldColor, newFill, oldFill, newStroke, oldStroke ) );
		
		// refresh
		app.scrollList.repaint();
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	
	
	// --- HELPERS
	private boolean isPointStyle() { return mouse().isShiftDown(); }	
	private boolean isFill() { return !mouse().isControlDown(); }
	
	@SuppressWarnings("serial")
	private class EditPasteStyle extends AbstractUndoableEdit
	{
		private JDraftingShape jdshape;
		private Color newColor, oldColor, newFill, oldFill;
		private BasicStroke newStroke, oldStroke;
		
		public EditPasteStyle( JDraftingShape jdshape, Color newColor,
											  Color oldColor, Color newFill, Color oldFill, 
											  BasicStroke newStroke, BasicStroke oldStroke )
		{
			this.jdshape = jdshape;
			this.newColor = newColor;
			this.oldColor = oldColor;
			this.newFill = newFill;
			this.oldFill = oldFill;
			this.newStroke = newStroke;
			this.oldStroke = oldStroke;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			jdshape.setColor( oldColor );
			jdshape.setFill( oldFill );
			jdshape.setStroke( oldStroke );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			jdshape.setColor( newColor );
			jdshape.setFill( newFill );
			jdshape.setStroke( newStroke );
		}
		
		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getUndoPresentationName()
		{
			return "Undo paste style";
		}
		@Override
		public String getRedoPresentationName()
		{
			return "Redo paste style";
		}
	}
}
