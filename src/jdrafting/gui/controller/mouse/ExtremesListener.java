package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

public class ExtremesListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "extremes_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	public ExtremesListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_ext1" ) );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		canvas.setCursor( canvas.getShapeAtCanvasPoint( e.getPoint() ) == null
						  ? CURSOR 
						  : new Cursor( Cursor.HAND_CURSOR ) );
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );		
		
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape != null )
		{
			// point style
			final Color color = isPointStyle(e)
								? app.getColor()
								: app.getPointColor();
			final BasicStroke stroke = isPointStyle(e)
									   ? app.getStroke()
									   : app.getPointStroke();
			
			// get shape vertex
			List<Point2D> vertex = jdshape.getVertex();
			
			// ERROR; closed shapes doesn't have extreme vertex
			if ( jdshape.isClosed( vertex ) )
				JOptionPane.showMessageDialog( app, getLocaleText( "ext_dlg" ), 
											   getLocaleText( "ext_title" ), 
											   JOptionPane.ERROR_MESSAGE );
			else
			{
				// get first and last vertex
				vertex = new ArrayList<Point2D>( Arrays.asList( 
						new Point2D[] { vertex.get( 0 ), 
										vertex.get( vertex.size() - 1 ) } ) );

				final String descHtml = String.format( "<font color=%s>[%s]</font>", 
													   Application.HTML_SHAPE_NAMES_COL, 
													   elvis( jdshape.getName(), "?" ) );
				
				//////////////////////////// TRANSACTION ////////////////////////////			
				final JDCompoundEdit transaction = new JDCompoundEdit(
									 getLocaleText( "extremes" ) + " " + descHtml + " (2 points)" );
				
				// add extreme points to exercise
				vertex
				.stream()
				.forEach( point -> app.addShapeFromIterator(
												   new JDPoint( point ).getPathIterator( null ), "",
												   getLocaleText( "new_extreme" ) + " " + descHtml, 
												   color, null, stroke, transaction ) );

				transaction.end();
				app.undoRedoSupport.postEdit( transaction );
				/////////////////////////////////////////////////////////////////////
				
				// refresh
				app.scrollList.repaint();
				
				// back to select mode
				canvas.setCanvasListener( new HandListener( canvas ) );
			}
		}
	}
	
	private boolean isPointStyle( MouseEvent e ) { return e.isShiftDown(); }
}
