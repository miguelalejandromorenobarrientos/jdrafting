package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;
import jdrafting.gui.controller.mouse.RegularPolygonListener;

@SuppressWarnings("serial")
public class RegularPolygonAction extends AbstractAction
{
	private Application app;
	private int vertex = 5;
	
	public RegularPolygonAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "reg_poly" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "reg_poly_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_reg_pol" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
									KeyEvent.VK_2, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "reg_poly.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "reg_poly.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		CanvasPanel canvas = app.getCanvas();
		
		// dialog for polygon vertex
		JSpinner spinFactor = new JSpinner( new SpinnerNumberModel( 
										vertex, 3, Integer.MAX_VALUE, 1 ) );
		spinFactor.addChangeListener( evt -> 
				vertex = (int) ( (JSpinner) evt.getSource() ).getValue() );
		
		int option = JOptionPane.showOptionDialog( app, spinFactor, 
					getLocaleText( "reg_poly_dlg" ),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
					getLargeIcon( "reg_poly.png" ), null, null );
		if ( option != JOptionPane.OK_OPTION )  return;

		canvas.setCanvasListener( new RegularPolygonListener( canvas, vertex ) );
	}
}
