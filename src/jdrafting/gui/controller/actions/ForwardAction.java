package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.Exercise;
import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class ForwardAction extends AbstractAction
{
	private Application app;
	
	public ForwardAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "forward" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "forward_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_F );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK )  );
		putValue( SMALL_ICON, getSmallIcon( "forward.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "forward.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		Exercise exercise = app.getExercise();
		if ( !exercise.isIndexAtEnd() )
		{
			exercise.setFrameIndex( exercise.getFrameIndex() + 1 );
			app.getCanvas().repaint();
			app.scrollList.repaint();
		}
	}
}
