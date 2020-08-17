package jdrafting.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import jdrafting.geom.JDraftingShape;

/**
 * Toast in canvas when move steps in exercise, showing current shape name and description
 * @since 0.1.10.1
 */
public class ToastCanvasStep extends Toast 
{
	private static final long serialVersionUID = 1L;
	
	private static final int TIME = 5000;  // ms
	protected int maxLength = 1000;
	
	public ToastCanvasStep( JDraftingShape shape, int index, Point location )
	{
		super( shape.getDescription().isEmpty()
				? "        "
				: "<html>" 
				  + String.join( "<br/>", shape.getDescription().split( "\n" ) )
				  + "</html>", 
			   new Point( location.x + 20, location.y + 15 ), 
			   TIME );
		
		// border and title
		getToastLabel().setBorder( BorderFactory.createTitledBorder( 
							String.format( "<html><font color='#7777FF'>%s:</font> "
										   + "<font color='#00FF00'><b><i>%s</i></b></font></html>", 
										   index, shape.getName() ) ) );

		// pause toast when mouse enter and restart at exit 
		addMouseListener( new MouseAdapter() {
			@Override
			public void mouseEntered( MouseEvent e ) 
			{ 
				ToastCanvasStep.this.getClosingTimer().stop(); 
			}
			@Override
			public void mouseExited( MouseEvent e ) 
			{ 
				ToastCanvasStep.this.getClosingTimer().restart();
			}
		});		
	}
}
