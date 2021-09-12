package jdrafting.gui.controller.mouse;

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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Apply homothety to selected shapes using mouse control 
 */
public class HomothetyListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
							JDUtils.getCustomCursor( "homothety_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private double factor;
	
	public HomothetyListener( CanvasPanel canvas, double factor )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		this.factor = factor;
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_homothety1" ) );
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

		Shape[] transformedShapes = 
			getTransformed( app.getSelectedShapes(), logicMouse, getFactor() );
		int index = 0;
		for ( JDraftingShape jdshape : app.getSelectedShapes() )
			jdshape.setShape( transformedShapes[index++] );
		
		app.undoSupport.postEdit( new EditHomothety( new HashSet<>( 
						app.getSelectedShapes() ), logicMouse, getFactor() ) );
		
		app.scrollList.repaint();
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

		// get transform
		AffineTransform transform = canvas.getTransform();

		// draw shapes transformed and center
		g2.setStroke( new BasicStroke( 1f ) );  // center
		g2.setColor( Color.RED );			
		Point2D canCenter = transform.transform( logicMouse, null );
		g2.fillOval( nearInt( canCenter.getX() - 4 ), 
					 nearInt( canCenter.getY() - 4 ), 8, 8 );
		
		g2.setStroke( new BasicStroke( 1f ) );  // aux
		g2.setColor( new Color( 32, 128, 64, 64 ) );
		double factor = getFactor();
		Shape[] transformed = getTransformed( app.getSelectedShapes(), 
		  		  							  logicMouse, factor );
		int index = 0;
		for ( JDraftingShape jdshape : app.getSelectedShapes() )
		{
			Shape transformShape = transformed[index++];
			JDraftingShape temp = 
							new JDraftingShape( transformShape, null, null );
			List<Point2D> oriVertex = jdshape.getVertex();
			List<Point2D> transVertex = temp.getVertex();
			Point2D p1, p2;			
			for ( int i = 0; i < oriVertex.size(); i++ )
			{
				p1 = factor > 1 ? transVertex.get( i ) : oriVertex.get( i );
				p2 = factor < 0 ? transVertex.get( i ) : logicMouse;
				g2.draw( transform.createTransformedShape( 
												new Line2D.Double( p1, p2 ) ) );
			}
		}
		
		g2.setStroke( new BasicStroke( 2f ) );  // transformed shapes
		g2.setColor( Application.toolMainColor );			
		for ( Shape tshape : transformed )
			g2.draw( transform.createTransformedShape( tshape ) );
			
		// factor info
		int mouseX = mouse().getX(), mouseY = mouse().getY();
		String factorInfo = 
				"x" + new DecimalFormat( "#.########" ).format( getFactor() );
		g2.setFont( new Font( Font.SERIF, Font.BOLD, 16 ) );
		g2.setColor( new Color( 40, 40, 180 ) );
		g2.drawString( factorInfo, mouseX + 21, mouseY - 9 );
		g2.setColor( Color.LIGHT_GRAY );			
		g2.drawString( factorInfo, mouseX + 20, mouseY - 10 );
	}
	
	
	// --- HELPERS ---
	
	// check modifiers
	private boolean isNegateFactor() { return mouse().isShiftDown(); }
	private boolean isInverseFactor() { return mouse().isControlDown(); }
	
	private double getFactor()
	{
		double factor = this.factor;
		if ( isNegateFactor() )
			factor = -factor;
		if ( isInverseFactor() )
			factor = 1. / factor;
		
		return factor;
	}
	
	/**
	 * Get transformed shapes
	 * @param logicMouse mouse logic position
	 * @return a list of new shapes
	 */
	private Shape[] getTransformed( Set<JDraftingShape> selected, Point2D center, double factor )
	{
		AffineTransform homothecy = new AffineTransform();
		homothecy.translate( center.getX(), center.getY() );
		homothecy.scale( factor, factor );
		homothecy.translate( -center.getX(), -center.getY() );
		
		return selected
			   .stream()
			   .map( jdshape ->
			   			homothecy.createTransformedShape( jdshape.getShape() ) )
			   .toArray( Shape[]::new );
	}
	
	/**
	 * UndoableEdit for undo/redo homothety 
	 */
	@SuppressWarnings("serial")
	private class EditHomothety extends AbstractUndoableEdit
	{
		private Set<JDraftingShape> selected;
		private double factor;
		private Point2D center;
		
		private EditHomothety( Set<JDraftingShape> selected, Point2D center,
							   double factor )
		{
			this.selected = selected;
			this.center = center;
			this.factor = factor;
		}
		
		@Override
		public void undo() throws CannotUndoException
		{
			Shape[] transformedShapes = getTransformed( selected, center, 
														1. / factor );
			int index = 0;
			for ( JDraftingShape jdshape : selected )
				jdshape.setShape( transformedShapes[index++] );
		}
		
		@Override
		public void redo() throws CannotRedoException
		{
			Shape[] transformedShapes = getTransformed( selected, center, 
														factor );
			int index = 0;
			for ( JDraftingShape jdshape : selected )
				jdshape.setShape( transformedShapes[index++] );
		}
		
		@Override
		public boolean canRedo() { return true; }
		@Override
		public boolean canUndo() { return true; }
		
		@Override
		public String getUndoPresentationName()
		{
			return "Undo apply homothety selected (" + selected.size() 
				   + " shapes)";
		}
		@Override
		public String getRedoPresentationName()
		{
			return "Redo apply homothety selected (" + selected.size() 
				   + " shapes)";
		}		
	}
}
