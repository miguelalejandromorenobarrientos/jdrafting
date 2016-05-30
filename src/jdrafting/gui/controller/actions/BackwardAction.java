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
import jdrafting.gui.JDUtils;

@SuppressWarnings("serial")
public class BackwardAction extends AbstractAction
{
	private Application app;
	
	public BackwardAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "backward" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "backward_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_backward" ) );
		putValue( ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, InputEvent.CTRL_MASK )  );
		putValue( SMALL_ICON, getSmallIcon( "backward.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "backward.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		Exercise exercise = app.getExercise();
		if ( exercise.getFrameIndex() > 1 )
		{
			exercise.setFrameIndex( exercise.getFrameIndex() - 1 );
			app.getCanvas().repaint();
			app.scrollList.repaint();
		}
	}
}
