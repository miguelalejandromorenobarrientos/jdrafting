package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;

@SuppressWarnings("serial")
public class AboutAction extends AbstractAction
{
	private Application app;
	
	public AboutAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "about" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_about" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( "F1" ) );
		putValue( SMALL_ICON, getSmallIcon( "jdrafting.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "jdrafting.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		final JEditorPane ep = new JEditorPane( "text/html", "<html>"
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
				+ getLocaleText( "app_des" )
			+ "</p>"
			+ "<p>"
			+ "GPLv3 license " 
			+ "<a href='http://www.gnu.org/licenses/gpl-3.0.html'>"
			+ "http://www.gnu.org/licenses/gpl-3.0.html"
			+ "</a>"
			+ "</p>"
			+ "<p>"
				+ "<br/>SVG export: " 
				+ "<a href='https://xmlgraphics.apache.org/batik/'>" 
				+ "Apache Batik</a>"
			+ "</p>"
			+ "<p>"
				+ "Command line parser: "
				+ "<a href="
				+ "'https://github.com/miguelalejandromorenobarrientos/CLA'>"
				+ "CLA</a>"
			+ "</p>"
			+ "<p>"
				+ "Toast in canvas for step description: "
				+ "<a href="
				+ "'https://github.com/miguelalejandromorenobarrientos/Toast-Swing'>"
				+ "Toast</a>"
			+ "</p>"
			+ "<p>"
				+ "Math parser for functions: "
				+ "<a href="
				+ "'https://miguelalejandromorenobarrientos.github.io/JmeDoc/'>"
				+ "JME parser</a>"
			+ "</p>"
			+ "<p>"
				+ "<br/>  (C)" + Application.COPYLEFT
			+ "</p>"
		+ "</html>" );		
		ep.setEditable( false );
		ep.setOpaque( false );
		ep.getCaret().deinstall( ep );  // non-selectable
		ep.addHyperlinkListener( ev -> {
			if ( ev.getEventType().equals( HyperlinkEvent.EventType.ACTIVATED )
				 && Desktop.isDesktopSupported() 
				 && Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) )
			{
				try { Desktop.getDesktop().browse( ev.getURL().toURI() ); }
				catch ( URISyntaxException | IOException ignored ) {}
			}
		});
		JOptionPane.showMessageDialog( app, ep, "About " + Application.APPNAME, 
							JOptionPane.PLAIN_MESSAGE, 
							JDUtils.getScaledIco( "jdrafting.png", 100, 100 ) );
	}
}
