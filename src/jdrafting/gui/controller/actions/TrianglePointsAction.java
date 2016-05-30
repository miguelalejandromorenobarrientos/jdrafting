package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleMnemonic;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import jdrafting.gui.Application;
import jdrafting.gui.controller.mouse.TrianglePointsListener;

@SuppressWarnings("serial")
public class TrianglePointsAction extends AbstractAction 
{
	private Application app;
	private int type;
	
	public TrianglePointsAction( Application app, int type )
	{
		this.app = app;
		this.type = type;
		
		putValue( NAME, TrianglePointsListener.getName( type ) );
		ImageIcon small = null, large = null;
		int mnemonic = -1;
		switch( type )
		{
			case TrianglePointsListener.INCENTER:
				small = getSmallIcon( "incenter.png" );
				large = getLargeIcon( "incenter.png" );
				mnemonic = getLocaleMnemonic( "mne_incenter" );
				break;
			case TrianglePointsListener.BARICENTER:
				small = getSmallIcon( "baricenter.png" );
				large = getLargeIcon( "baricenter.png" );
				mnemonic = getLocaleMnemonic( "mne_baricenter" );
				break;
			case TrianglePointsListener.CIRCUMCENTER:
				small = getSmallIcon( "circumcenter.png" );
				large = getLargeIcon( "circumcenter.png" );
				mnemonic = getLocaleMnemonic( "mne_circumcenter" );
				break;
			case TrianglePointsListener.ORTOCENTER:
				small = getSmallIcon( "ortocenter.png" );
				large = getLargeIcon( "ortocenter.png" );
				mnemonic = getLocaleMnemonic( "mne_ortocenter" );
				break;
		}
		putValue( MNEMONIC_KEY, mnemonic );
		putValue( SMALL_ICON, small ); 
		putValue( LARGE_ICON_KEY, large );
	}
		
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( 
						new TrianglePointsListener( app.getCanvas(), type ) );
	}
}
