package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;
import jdrafting.gui.controller.mouse.MathFunctionListener;
import jme.Expresion;
import jme.excepciones.ExpresionException;
import jme.terminales.RealDoble;
import static jdrafting.gui.JDUtils.getLocaleText;

@SuppressWarnings("serial")
public class MathFunctionAction extends AbstractAction 
{
	private Application app;
	private Map<String,Object> jmeParams;
	
	public MathFunctionAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "func" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "func_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_jme" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_J, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "jme.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "jme.png" ) );
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( !Application.jmeEnabled )
			return;
		
		JMEDialog dialog = new JMEDialog();
		jmeParams = null;
		dialog.setVisible( true );

		if ( jmeParams != null )
			app.getCanvas().setCanvasListener(
					new MathFunctionListener( app.getCanvas(), jmeParams ) );
	}
		
	private class JMEDialog extends JDialog
	{
		private JMEDialog()
		{
			super( app, getLocaleText( "jme_dlg" ), true );
			setLayout( new GridLayout( 6, 2, 4, 4 ) );
			add( new JLabel() );
			JEditorPane ep = new JEditorPane( "text/html", 
					"<html><a href='https://miguelalejandromorenobarrientos.github.io/JmeDoc/'>"
					+ getLocaleText( "jme_doc" )
					+ "</a></html>" );
			ep.setEditable( false );
			ep.getCaret().deinstall( ep );  // non-selectable
			ep.addHyperlinkListener( ev -> {
				if ( ev.getEventType().equals( HyperlinkEvent.EventType.ACTIVATED )
					 && Desktop.isDesktopSupported() 
					 && Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) )
				{
					try { Desktop.getDesktop().browse( ev.getURL().toURI() ); }
					catch ( URISyntaxException | IOException ex ) {}
				}
			});			
			add( ep );
			add( new JLabel( "<html>JME "
					+ getLocaleText( "jme_examples" )
					+ "  ->   f(x):=<font color=blue>cos(x)</font> "
					+ "|| f(t):=<font color=red>[cos(t),3*sin(t)]</font></html>", 
							 JLabel.RIGHT ) );
			JTextField tfExpression = new JTextField(
										jmeParams != null
										? ( (Expresion) jmeParams.get( "expression" ) ).entrada()
										: "" );
			add( tfExpression );
			add( new JLabel( getLocaleText( "jme_min" ), JLabel.RIGHT ) );
			JTextField tfminX = new JTextField( jmeParams != null
												? jmeParams.get( "xmin" ).toString()
												: "-10" );
			add( tfminX );
			add( new JLabel( getLocaleText( "jme_max" ), JLabel.RIGHT ) );
			JTextField tfmaxX = new JTextField( jmeParams != null
												? jmeParams.get( "xmax" ).toString()
												: "10" );
			add( tfmaxX );
			add( new JLabel( getLocaleText( "jme_intervals" ), JLabel.RIGHT ) );
			JTextField tfIntervals = new JTextField( 
						jmeParams != null
						? jmeParams.get( "intervals" ).toString()
						: String.valueOf( (int) app.getFlatnessValue() ) );
			add( tfIntervals );
			JButton btnOk = new JButton( "Ok" );
			add( btnOk );
			Action okAction = new AbstractAction() {
				@Override
				public void actionPerformed( ActionEvent e )
				{
					Map<String,Object> params = new HashMap<>();
					try
					{
						params.put( "expression", 
								new Expresion( tfExpression.getText() ) );
						params.put( "xmin", ( (RealDoble) Expresion.evaluar( 
												tfminX.getText() ) ).doble() );
						params.put( "xmax", ( (RealDoble) Expresion.evaluar( 
												tfmaxX.getText() ) ).doble() );
						params.put( "intervals", 
								(int) ( (RealDoble) Expresion.evaluar( 
										tfIntervals.getText() ) ).longint() );
						jmeParams = params;
					}
					catch ( ClassCastException | ExpresionException ex )
					{
						JOptionPane.showMessageDialog( JMEDialog.this, ex, 
								"jme error", JOptionPane.ERROR_MESSAGE );						
						return;
					}
					dispose();
				}
			};			
			btnOk.addActionListener( okAction );
			JButton btnCancel = new JButton( "Cancel" );
			add( btnCancel );
			Action cancelAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			};
			btnCancel.addActionListener( cancelAction );
			
			// ENTER
			getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
	        .put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "INTRO_KEY" );
			getRootPane().getActionMap().put( "INTRO_KEY", okAction );			
			// ESC
			getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
	        .put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), 
	        	  "ESCAPE_KEY" );
			getRootPane().getActionMap().put( "ESCAPE_KEY", cancelAction );
			
			// window
			setPreferredSize( new Dimension( 600, 200 ) );
			pack();
			setLocationRelativeTo( app );
			
			setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		}
	}
}
