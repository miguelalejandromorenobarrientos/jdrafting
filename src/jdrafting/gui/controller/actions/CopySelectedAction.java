package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;
import jdrafting.gui.Toast;

@SuppressWarnings("serial")
public class CopySelectedAction extends AbstractAction
{
	private Application app;
	
	public CopySelectedAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "copy" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "copy_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_copy" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "copy.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "copy.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( !app.getSelectedShapes().isEmpty() )
		{
			app.setInnerClipboard( app.getSelectedShapes()
							   	  .parallelStream()
							   	  .sorted( (jds1,jds2) -> Integer.compare( 
							   			  						 app.getExercise().indexOf(jds1),
							   			  						 app.getExercise().indexOf(jds2) ) )
							   	  .toArray( JDraftingShape[]::new ) );
			
			new Toast( String.format( "<html>%d %s</html>", 
									  app.getInnerClipboard().length, 
									  getLocaleText( "toast_copy" ) ), 
					   Toast.ONE_SECOND ).showToast();
		}
		else
		{
			final Toast toast = new Toast( String.format(
										"<html><font color=#FFFF00>%s</font></html>",
										getLocaleText( "toast_no_copy" ) ),
								Toast.ONE_SECOND );
			toast.getToastLabel().setBackground( new Color( 255, 75, 75 ) );
			toast.showToast();
		}
	}
}
