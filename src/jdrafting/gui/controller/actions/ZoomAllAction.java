package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.Viewport;

@SuppressWarnings("serial")
public class ZoomAllAction extends AbstractAction
{
	private Application app;
	private CanvasPanel canvas;
	
	public ZoomAllAction( Application app )
	{
		this.app = app;
		canvas = app.getCanvas();
		
		putValue( NAME, getLocaleText( "zoom_all" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "zoom_all_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_A );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( "typed *" ) );
		putValue( SMALL_ICON, getSmallIcon( "zoom_all.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "zoom_all.png" ) );
	}	
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( !app.getExercise().getShapes().isEmpty() )
		{
			Rectangle2D enclosure = CanvasPanel.getExerciseBounds(
								app.getExercise(), 
								new Viewport( app.getExercise().getBounds() ),
								canvas.getCanvasViewport() );
			if ( Double.isNaN( enclosure.getWidth() ) 
				 || Double.isNaN( enclosure.getHeight() ) )
			{
				enclosure = new Rectangle2D.Double( 0., 0., 1., 1. );
			}

			canvas.setViewport( new Viewport( enclosure ) );
			/*double maxsize = Math.max( enclosure.getWidth(), enclosure.getHeight() );
			canvas.setViewport( new Viewport( 
									enclosure.getCenterX() - maxsize / 2,
									enclosure.getCenterX() + maxsize / 2,
									enclosure.getCenterY() - maxsize / 2,
									enclosure.getCenterY() + maxsize / 2 ) );*/
			
			canvas.repaint();
		}
	}
}
