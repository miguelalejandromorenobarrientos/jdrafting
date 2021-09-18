package jdrafting.gui.controller.mouse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;

import jdrafting.geom.JDStrokes;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Capture line/point/screen color and styles
 * @version 0.1.11.1
 */
public class EyedropperListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "eyedropper_cursor.png" );
	private Application app;
	private CanvasPanel canvas;
	
	public EyedropperListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();

		app.setStatusText( JDUtils.getLocaleText( "txt_eyedropper" ) );
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

		final Color color;
		JDraftingShape jdshape = null;
		
		// capture screen color
		if ( e.isControlDown() )  // > color in screen (pure eyedropper)
		{
			final BufferedImage img = new BufferedImage( canvas.getWidth(), canvas.getHeight(), 
														 BufferedImage.TYPE_INT_ARGB );
			final Graphics2D g2 = (Graphics2D) img.getGraphics();
			g2.setColor( canvas.getBackground() );
			g2.fillRect( 0, 0, img.getWidth(), img.getHeight() );
			
			CanvasPanel.drawExercise( g2, canvas.getTransform(), app.getExercise(), new HashSet<>(), 
									  true );
			color = new Color( img.getRGB( e.getX(), e.getY() ), true );			
		}
		// capure shape color
		else
		{
			// get shape
			jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );		
			if ( jdshape == null )  return;
			color = jdshape.getColor();
		}		
		
		// capture shape style
		if ( e.isShiftDown() )  // > point style
		{
			app.setPointColor( color );
			if ( jdshape != null )
				app.setPointStroke( JDStrokes.cloneStrokeStyle( 
									   jdshape.getStroke().getLineWidth(), app.getPointStroke() ) );
		}
		else  // > line style
		{
			// set captured color
			app.setColor( color );
			if ( jdshape != null )
			{
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
		}
		
		// back to select mode
		canvas.setCanvasListener( new HandListener( canvas ) );
	}
}
