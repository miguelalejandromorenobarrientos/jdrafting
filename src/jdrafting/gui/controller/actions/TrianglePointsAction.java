package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;

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
		ImageIcon large = null;
		switch( type )
		{
			case TrianglePointsListener.INCENTER:
				large = getLargeIcon( "incenter.png" );
				break;
			case TrianglePointsListener.BARICENTER:
				large = getLargeIcon( "baricenter.png" );
				break;
			case TrianglePointsListener.CIRCUMCENTER:
				large = getLargeIcon( "circumcenter.png" );
				break;
			case TrianglePointsListener.ORTOCENTER:
				large = getLargeIcon( "ortocenter.png" );
				break;
		}
		putValue( SMALL_ICON, large ); 
		putValue( LARGE_ICON_KEY, large );
	}
		
	@Override
	public void actionPerformed( ActionEvent e )
	{
		app.getCanvas().setCanvasListener( 
						new TrianglePointsListener( app.getCanvas(), type ) );
	}
}
