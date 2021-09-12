package jdrafting.gui.controller.mouse;

import static java.lang.Math.atan2;
import static java.lang.Math.rint;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static jdrafting.geom.JDMath.nearInt;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
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

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Rotate selected shapes using mouse control 
 */
public class RotationListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "rotation_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D center;
	
	public RotationListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_rotation1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( center != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// put center
		if ( center == null )
		{
			center = logicMouse;
			app.setStatusText( getLocaleText( "txt_rotation2" ) );
		}
		// modify shapes by rotation
		else
		{
			double ang = getAngle( logicMouse );
			Shape[] rotatedShapes = getRotated( app.getSelectedShapes(), ang );
			int index = 0;
			for ( JDraftingShape jdshape : app.getSelectedShapes() )
				jdshape.setShape( rotatedShapes[index++] );
			
			app.undoSupport.postEdit( new EditRotation( 
							new HashSet<>( app.getSelectedShapes() ), ang ) );
			
			app.scrollList.repaint();
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( center != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// get transform
			AffineTransform transform = canvas.getTransform();

			// set tool style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );			

			// draw shapes rotated and center
			Point2D canCenter = transform.transform( center, null );
			g2.fillOval( nearInt( canCenter.getX() - 4 ), 
						 nearInt( canCenter.getY() - 4 ), 8, 8 );
			for ( Shape rotated : getRotated( app.getSelectedShapes(), 
											  getAngle( logicMouse ) ) )
				g2.draw( transform.createTransformedShape( rotated ) );			
				
			// draw angle info
			double ang = toDegrees( getAngle( logicMouse ) );
			if ( !Double.isNaN( ang ) )
			{
				int mouseX = mouse().getX(), mouseY = mouse().getY();
				String angInfo = String.format( "%.2f", ang ) + "º";
				g2.setFont( new Font( Font.SERIF, Font.BOLD, 16 ) );
				g2.setColor( new Color( 40, 40, 180 ) );
				g2.drawString( angInfo, mouseX + 21, mouseY - 9 );
				g2.setColor( Color.LIGHT_GRAY );			
				g2.drawString( angInfo, mouseX + 20, mouseY - 10 );
			}
		}
	}
	
	
	// --- HELPERS ---
	
	// check modifiers
	private boolean isFixedAngle() { return mouse().isShiftDown(); }
	private boolean isIntegerAngle() { return mouse().isControlDown(); }
	
	/**
	 * Get rotation angle
	 * @param logicMouse
	 * @return the angle in radians (-pi to pi)
	 */
	private double getAngle( Point2D logicMouse )
	{
		double ang = isFixedAngle()
				 	 ? toRadians( app.getAngle() )
				 	 : atan2( logicMouse.getY() - center.getY(), 
				 			  logicMouse.getX() - center.getX() );
		if ( isIntegerAngle() )
			ang = toRadians( rint( toDegrees( ang ) ) );
		
		return ang;
	}
	
	/**
	 * Get rotated shapes
	 * @param logicMouse mouse logic position
	 * @return a list of new shapes
	 */
	private Shape[] getRotated( Set<JDraftingShape> selected, double ang )
	{
		AffineTransform rotation = AffineTransform.getRotateInstance( 
											ang, center.getX(), center.getY() );
		return selected
			   .stream()
			   .map( jdshape ->
			   			rotation.createTransformedShape( jdshape.getShape() ) )
			   .toArray( Shape[]::new );
	}
	
	/**
	 * UndoableEdit for undo/redo rotations 
	 */
	@SuppressWarnings("serial")
	private class EditRotation extends AbstractUndoableEdit
	{
		private Set<JDraftingShape> selected;
		private double ang;
		
		private EditRotation( Set<JDraftingShape> selected, double ang )
		{
			this.selected = selected;
			this.ang = ang;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			Shape[] rotatedShapes = getRotated( selected, -ang );
			int index = 0;
			for ( JDraftingShape jdshape : selected )
				jdshape.setShape( rotatedShapes[index++] );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			Shape[] rotatedShapes = getRotated( selected, ang );
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
			return "Undo rotate selected (" + selected.size() + " shapes)";
		}
		@Override
		public String getRedoPresentationName()
		{
			return "Redo rotate selected (" + selected.size() + " shapes)";
		}		
	}
}
