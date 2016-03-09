package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import jdrafting.gui.Application;

@SuppressWarnings("serial")
public class AboutAction extends AbstractAction
{
	private Application app;
	
	public AboutAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "about" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_A );
		putValue( SMALL_ICON, getSmallIcon( "jdrafting.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "jdrafting.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		String msg = "<html>"
			+ "<p><font color=green size=6><b>"
				+ Application.APPNAME 
			+ "</b></font></p>"
			+ "<p><font color=green>v<i>"
				+ Application.VERSION
			+ "</i></font></p>"
			+ "<p>Author <i>"
				+ Application.AUTHOR
			+ "</i></p><br/>"
			+ "<p><font size=4><i>"
				+ Application.APPNAME + "</i></font> "
				+ Application.getLocaleText( "app_des" )
			+ "</p>"
			+ "<p>"
				+ "<br/>  @" + Application.COPYLEFT
			+ "</p>"
		+ "</html>";
		
		JOptionPane.showMessageDialog( app, msg, "About " + Application.APPNAME, 
					JOptionPane.PLAIN_MESSAGE, 
					Application.getScaledIco( "jdrafting.png", 100, 100 ) );
	}
}
