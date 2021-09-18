package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;
import jdrafting.gui.ToastCanvasStep;

@SuppressWarnings("serial")
public class RewindAction extends AbstractAction
{
	private Application app;
	
	public RewindAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "rewind" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "rewind_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_rewind" ) );
		putValue( ACCELERATOR_KEY, 
				  KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, InputEvent.ALT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "rewind.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "rewind.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( !app.getExercise().isEmpty() )
		{
			// update frame
			app.getExercise().setFrameIndex( app.getExercise().getStartIndex() );
			// create step description toast 
			final JDraftingShape shape = 
							  app.shapeList.getModel().get( app.getExercise().getStartIndex() - 1 );
			if ( app.currentToast != null )
			{
				if ( app.currentToast.getClosingTimer() != null )
					app.currentToast.getClosingTimer().stop();
				app.currentToast.dispose();
			}
			app.currentToast = new ToastCanvasStep( shape, app.getExercise().getFrameIndex(), 
													app.canvas.getLocationOnScreen() )
							   .showToast();
			
			// refresh
			app.getCanvas().repaint();
			app.scrollList.repaint();
		}
	}
}
