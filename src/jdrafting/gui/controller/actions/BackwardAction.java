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
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;
import jdrafting.gui.ToastCanvasStep;

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
		final Exercise exercise = app.getExercise();
		if ( exercise.getFrameIndex() > 1 )
		{
			// update frame
			exercise.setFrameIndex( exercise.getFrameIndex() - 1 );
			
			// create step description toast 
			final JDraftingShape shape = app.shapeList.getModel().get(exercise.getFrameIndex() - 1);
			if ( app.currentToast != null )
			{
				if ( app.currentToast.getClosingTimer() != null )
					app.currentToast.getClosingTimer().stop();
				app.currentToast.dispose();
			}
			app.currentToast = new ToastCanvasStep( shape, exercise.getFrameIndex(), 
													app.canvas.getLocationOnScreen() )
					   		   .showToast();
			
			// refresh
			app.getCanvas().repaint();
			app.scrollList.repaint();
		}
	}
}
