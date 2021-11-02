package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

/**
 * Create a rectangle bounds for a shape by mouse control 
 */
public class BoundsListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
								JDUtils.getCustomCursor( "bounds_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public BoundsListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_bounds1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		canvas.setCursor( canvas.getShapeAtCanvasPoint( e.getPoint() ) == null
						  ? CURSOR
						  : new Cursor( Cursor.HAND_CURSOR ) );
		
		canvas.repaint();
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// get shape
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape != null )
		{
			final String descHtml = String.format( "<font color=%s>[%s]</font>",
												   Application.HTML_SHAPE_NAMES_COL,
												   elvis( jdshape.getName(), "?" ) );
			
			//////////////////////////// TRANSACTION ////////////////////////////
			final CompoundEdit transaction = new JDCompoundEdit( 
													getLocaleText( "bounds" ) + " " + descHtml );
			
			// add rectangle bounds to exercise
			app.addShapeFromIterator( jdshape.getShape().getBounds2D().getPathIterator( null ), "",
									  getLocaleText( "new_bounds" ) + " " + descHtml,
									  app.getColor(), null, app.getStroke(), transaction );

			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		// get shape
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( mouse().getPoint() );
		if ( jdshape == null )  return;
		
		// set tool style
		g2.setStroke( new BasicStroke( 1f ) );
		g2.setColor( Application.toolMainColor );

		// draw bounds
		g2.draw( canvas.getTransform().createTransformedShape( jdshape.getShape().getBounds2D() ) );
	}
}
