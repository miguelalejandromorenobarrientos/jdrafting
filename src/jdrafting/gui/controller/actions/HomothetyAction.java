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
import jdrafting.gui.controller.mouse.HomothetyListener;

@SuppressWarnings("serial")
public class HomothetyAction extends AbstractAction
{
	private Application app;
	private double factor = 2.;
	
	public HomothetyAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "homothety" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "homothety_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_homo" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "homothety.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "homothety.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		final CanvasPanel canvas = app.getCanvas();
		
		if ( app.getSelectedShapes().size() > 0 )
		{
			// dialog for homothety factor
			final JSpinner spinFactor = new JSpinner( new SpinnerNumberModel( 
													 factor, 0.1, Double.POSITIVE_INFINITY, 0.1 ) );
			spinFactor.addChangeListener( evt ->
				factor = (double) ( (JSpinner) evt.getSource() ).getValue() );
			
			final int option = JOptionPane.showOptionDialog( app, spinFactor, 
															 getLocaleText( "homo_dlg" ),
															 JOptionPane.OK_CANCEL_OPTION, 
															 JOptionPane.PLAIN_MESSAGE, 
															 getLargeIcon( "homothety.png" ), 
															 null, null );
			if ( option != JOptionPane.OK_OPTION )  return;

			canvas.setCanvasListener( new HomothetyListener( canvas, factor ) );
		}
		else
			JOptionPane.showMessageDialog( app, 
										getLocaleText( "selected_shapes_msg" ), 
										getLocaleText( "homothety" ) + " error", 
										JOptionPane.ERROR_MESSAGE );
	}
}
