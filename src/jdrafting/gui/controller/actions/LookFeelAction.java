package jdrafting.gui.controller.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class LookFeelAction extends AbstractAction
{
	private JMenu menu;
	private Application app;
	
	public LookFeelAction( Application app, JMenu menu )
	{
		this.app = app;
		this.menu = menu;
		actionPerformed( null );
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			LookAndFeelInfo[] list = UIManager.getInstalledLookAndFeels();
			
			int i;
			for ( i = 0; i < menu.getItemCount(); i++ )
				if ( menu.getItem( i ).isSelected() )
					break;
			
			UIManager.setLookAndFeel( list[i].getClassName() );
			SwingUtilities.updateComponentTreeUI( app );
		}
		catch ( Exception ex ) {}		
	}
}
