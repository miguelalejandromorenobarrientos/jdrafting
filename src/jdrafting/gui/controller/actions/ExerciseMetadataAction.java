package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.FileInfoDialog;
import jdrafting.gui.JDUtils;

@SuppressWarnings("serial")
public class ExerciseMetadataAction extends AbstractAction 
{
	private Application app;
	
	public ExerciseMetadataAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "fileinfo" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "fileinfo_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_ex_metadata" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "fileinfo.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "fileinfo.png" ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		new FileInfoDialog( app ).setVisible( true );
		app.setAppTitle();
	}
}
