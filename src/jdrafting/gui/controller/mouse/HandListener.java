package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.EditShapeDialog;
import jdrafting.gui.JDUtils;

public class HandListener extends AbstractCanvasMouseListener 
{
	private static final Cursor DRAG_CURSOR = JDUtils.getCustomCursor( "dragging_cursor.png" );
	private static final Cursor HAND_CURSOR = JDUtils.getCustomCursor( "hand_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private JDraftingShape textShape;
	
	// drag parameters
	private int newMouseX, newMouseY;
	public boolean moving = false;
	private int button = -1;  // (mousedragged doesn't keep pressed button)

	public HandListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		canvas.setCursor( HAND_CURSOR );

		app.setStatusText( JDUtils.getLocaleText( "txt_hand" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		// cursor over shape or over canvas background
		canvas.setCursor( canvas.getShapeAtCanvasPoint( e.getPoint() ) != null
						  ? new Cursor( Cursor.HAND_CURSOR )
						  : HAND_CURSOR );
	}

	@Override
	public void mousePressed( MouseEvent e )
	{
		super.mousePressed( e );
		
		newMouseX = e.getX();
		newMouseY = e.getY();
		button = e.getButton();
		
		// check for text label selected
		if ( app.isVisibleNames() )
		{
			JDraftingShape jdshape =
						canvas.getShapeWithNameAtCanvasPoint( e.getPoint() ); 
			if ( jdshape != null )
			{
				textShape = jdshape;
				return;
			}
		}
		
		// check for select a shape
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		if ( jdshape != null )
		{
			// edit shape dialog
			if ( e.getClickCount() == 2 )
			{
				new EditShapeDialog( app, jdshape ).setVisible( true );
				return;
			}
			
			app.shapeList.getSelectionModel().clearSelection();

			// select single shape
			if ( app.getSelectedShapes().isEmpty() )
				app.getSelectedShapes().add( jdshape );
			// select/deselect shape
			else if ( e.isShiftDown() )
			{
				// select
				if ( !app.getSelectedShapes().contains( jdshape ) )
					app.getSelectedShapes().add( jdshape );
				// deselect
				else
					app.getSelectedShapes().remove( jdshape );
			}
			// new selection with this shape
			else if ( !app.getSelectedShapes().contains( jdshape ) )
			{
				app.setSelectedShapes( new HashSet<>() );
				app.getSelectedShapes().add( jdshape );	
			}
		}
		else
			// remove selection when pressing canvas background
			app.setSelectedShapes( new HashSet<>() );
		
		canvas.repaint();
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		super.mouseDragged( e );
		
		// update mouse position
		final int mouseX = newMouseX, mouseY = newMouseY;
		newMouseX = e.getX();
		newMouseY = e.getY();
		
		// dragging cursor
		canvas.setCursor( DRAG_CURSOR );

		// canvas delta
		final Point2D canvasDelta = new Point2D.Double( newMouseX - mouseX, newMouseY - mouseY );
		
		// move text relative to shape
		if ( textShape != null )
		{
			final Rectangle2D bounds = CanvasPanel.getShapeCanvasBounds(
																 textShape, canvas.getTransform() );
			
			final Point2D namePosition = CanvasPanel.getNamePosition( bounds, textShape );
			
			final double textX = namePosition.getX() + canvasDelta.getX(),
						 textY = namePosition.getY() + canvasDelta.getY();
			
			textShape.setTextPosition( new Point2D.Double(
				( textX - bounds.getCenterX() ) / bounds.getWidth(),
				-( textY - bounds.getCenterY() ) / bounds.getHeight() ) );
			
			canvas.repaint();
			
			return;
		}
		
		// logic delta
		final Point2D delta = canvas.getInverseTransform().deltaTransform( canvasDelta, null );
			
		// drag with hand
		if ( app.getSelectedShapes().isEmpty() )
			canvas.getViewport().move( -delta.getX(), -delta.getY() );
		// move shape with second button
		else if ( button == MouseEvent.BUTTON3 )
		{
			if ( !moving )
				app.undoRedoSupport.postEdit( new EditMoveShapes( app.getSelectedShapes() ) );
			moving = true;
			
			app.getSelectedShapes()
			   .stream()
			   .forEach( jdshape -> jdshape.move( delta.getX(), delta.getY() ) );
		}
		
		// refresh canvas
		canvas.repaint();		
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		textShape = null;
		moving = false;
		canvas.setCursor( HAND_CURSOR );
		
		// refresh
		canvas.repaint();
		app.scrollList.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// draw segment from shape bounds center to name text
		if ( textShape != null )
		{
			final AffineTransform transform = canvas.getTransform();			
			final Rectangle2D bounds = CanvasPanel.getShapeCanvasBounds( textShape, transform );			
			final Point2D namePosition = CanvasPanel.getNamePosition( bounds, textShape );
			
			g2.setColor( Application.toolMainColor );
			g2.setStroke( textShape.getStroke() );
			g2.draw( transform.createTransformedShape( textShape.getShape() ) );
			g2.setStroke( new BasicStroke( 1f ) );
			g2.draw( new Line2D.Double( 
								namePosition.getX(), namePosition.getY(), 
								bounds.getCenterX(), bounds.getCenterY() ) );			
		}
	}

	/**
	 * Undo/Redo class for move shapes action
	 */
	@SuppressWarnings("serial")
	private class EditMoveShapes extends AbstractUndoableEdit
	{
		final private Map<JDraftingShape,Shape> oldValuesMap = new HashMap<>(),
					  							newValuesMap = new HashMap<>();
		
		EditMoveShapes( Iterable<JDraftingShape> shapes )
		{
			for ( JDraftingShape jdshape : shapes )
				oldValuesMap.put( jdshape, jdshape.getShape() );
		}
		
		@Override
		public void undo()
		{
			oldValuesMap.forEach( (key,value) -> newValuesMap.put( key, key.getShape() ) );
			oldValuesMap.forEach( (key,value) -> key.setShape( value ) );
		}
		
		@Override
		public void redo() 
		{
			newValuesMap.forEach( (key,value) -> key.setShape( value ) );
		}

		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getPresentationName()
		{
			return "Move (" + oldValuesMap.size() + " shapes)";
		}
	}
}
