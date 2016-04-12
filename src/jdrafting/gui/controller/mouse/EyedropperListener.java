package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import jdrafting.geom.JDStrokes;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

public class EyedropperListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR =
						CanvasPanel.getCustomCursor( "eyedropper_cursor.png" );
	private Application app;
	private CanvasPanel canvas;
	
	public EyedropperListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		app.setStatusText( Application.getLocaleText( "txt_eyedropper" ) );
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
		JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );		
		if ( jdshape == null )  return;
		
		// capture shape style
		if ( e.isShiftDown() )  // > point style
		{
			app.setPointColor( jdshape.getColor() );
			app.setPointStroke( JDStrokes.cloneStrokeStyle( 
				jdshape.getStroke().getLineWidth(), app.getPointStroke() ) );
		}
		else  // > line style
		{
			// set captured color
			app.setColor( jdshape.getColor() );
			// set captured stroke
			app.setStroke( jdshape.getStroke() );
			// set captured style line into combobox
			BasicStroke stroke = jdshape.isPoint( jdshape.getVertex() ) 
								 ? JDStrokes.PLAIN_ROUND.getStroke()
								 : jdshape.getStroke();
			int size = app.comboLineStyle.getModel().getSize();
			for ( int index = 0; index < size; index++ )
			{
				BasicStroke cStroke = app.comboLineStyle.getItemAt( index );
				cStroke = JDStrokes.getStroke( 
								cStroke, jdshape.getStroke().getLineWidth() );
				if ( stroke.getEndCap() == cStroke.getEndCap()
					 && stroke.getMiterLimit() == cStroke.getMiterLimit()
					 && stroke.getDashPhase() == cStroke.getDashPhase() 
					 && stroke.getLineJoin() == cStroke.getLineJoin()
					 && Arrays.equals( stroke.getDashArray(), 
							 		   cStroke.getDashArray() ) )
				{
					app.comboLineStyle.setSelectedIndex( index );
					break;
				}
			}
		}
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
}
