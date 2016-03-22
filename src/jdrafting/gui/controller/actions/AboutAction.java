package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

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
		JEditorPane ep = new JEditorPane( "text/html", "<html>"
			+ "<p><font color=green size=7><b>"
				+ Application.APPNAME 
			+ "</b></font><br/>"
			+ "<font color=green size=4>v<i>"
				+ Application.VERSION
			+ "</i></font><br/>"
			+ "<font size=3>Author <i>"
				+ Application.AUTHOR
			+ "</i></font></p>"
			+ "<p><font size=4><i>"
				+ Application.APPNAME + "</i></font> "
				+ Application.getLocaleText( "app_des" )
			+ "</p>"
			+ "<p>"
				+ "<br/>SVG export: " 
				+ "<a href='https://xmlgraphics.apache.org/batik/'>" 
				+ "Apache Batik</a>"
			+ "</p>"
			+ "<p>"
				+ "<br/>  @" + Application.COPYLEFT
			+ "</p>"
		+ "</html>" );
		ep.setEditable( false );
		ep.setOpaque( false );
		ep.addHyperlinkListener( new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate( HyperlinkEvent e )
			{
				if ( e.getEventType().equals( 
											HyperlinkEvent.EventType.ACTIVATED )
					 && Desktop.isDesktopSupported() 
					 && Desktop.getDesktop().isSupported( 
							 						Desktop.Action.BROWSE ) )
				{
					try { Desktop.getDesktop().browse( e.getURL().toURI() ); }
					catch ( URISyntaxException | IOException ex ) {}
				}
			}
		});
		JOptionPane.showMessageDialog( app, ep, "About " + Application.APPNAME, 
					JOptionPane.PLAIN_MESSAGE, 
					Application.getScaledIco( "jdrafting.png", 100, 100 ) );
	}
}
