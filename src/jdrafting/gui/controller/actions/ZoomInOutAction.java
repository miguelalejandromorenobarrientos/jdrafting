package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;
import jdrafting.gui.Viewport;

@SuppressWarnings("serial")
public class ZoomInOutAction extends AbstractAction
{
	private final double ZOOM_FACTOR;
	private CanvasPanel canvas;
	
	public ZoomInOutAction( Application app, boolean zoomIn )
	{
		canvas = app.getCanvas();
		ZOOM_FACTOR = zoomIn ? 1 / Math.sqrt( 2 ) : Math.sqrt( 2 );
		
		putValue( NAME, getLocaleText( zoomIn ? "zoom_in" : "zoom_out" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( zoomIn ? "zoom_in_des" : "zoom_out_des" ) );
		putValue( MNEMONIC_KEY, zoomIn
								? JDUtils.getLocaleMnemonic( "mne_zoom_in" )
								: JDUtils.getLocaleMnemonic( "mne_zoom_out" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke(	zoomIn ? "typed +" : "typed -" ) );
		putValue( SMALL_ICON, getSmallIcon( zoomIn ? "zoom_in.png" : "zoom_out.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( zoomIn ? "zoom_in.png" : "zoom_out.png" ) );
	}	
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		final Viewport oldView = canvas.getViewport();

		// avoid very small zooms for accuracy bugs
		if ( ZOOM_FACTOR < 1. )
			if ( oldView.getWidth() < 1. || oldView.getHeight() < 1. )
				return;
		
		final double w = oldView.getWidth() * ZOOM_FACTOR,
					 h = oldView.getHeight() * ZOOM_FACTOR;				
		canvas.setViewport( new Viewport( oldView.getCenterX() - w / 2,
										  oldView.getCenterX() + w / 2,
										  oldView.getCenterY() - h / 2,
										  oldView.getCenterY() + h / 2 ) );			
		canvas.repaint();
	}
}
