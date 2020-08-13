package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import jdrafting.Exercise;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;
import jdrafting.gui.Viewport;

@SuppressWarnings("serial")
public class OpenAction extends AbstractAction
{
	private Application app;
	private static JFileChooser fileChooser;
	
	public OpenAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "open" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "open_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_open" ) );
		putValue( ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "open.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "open.png" ) );
		
		// create static file chooser dialog
		fileChooser = new JFileChooser();
		// add accesory panel for preview to file chooser
		JPanel accesoryPanel = new JPanel();
		accesoryPanel.setBorder( 
								BorderFactory.createTitledBorder( "Preview" ) );
		PreviewPanel previewPanel = new PreviewPanel(); 
		accesoryPanel.add( previewPanel );
		fileChooser.setAccessory( accesoryPanel );
		fileChooser.addPropertyChangeListener( previewPanel );
		fileChooser.setPreferredSize( JDUtils.getScreenSize( 0.7f, 0.7f ) );
	}
		
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// check for not saved file
		if ( app.undoManager.canUndo() )
		{
			int option = JOptionPane.showConfirmDialog( app,
													getLocaleText( "exit_msg" ), 
													getLocaleText( "open" ), 
													JOptionPane.YES_NO_OPTION );
			if ( option != JOptionPane.YES_OPTION )  return;  // cancel open
		}
		
		// update file chooser L&F
		SwingUtilities.updateComponentTreeUI( fileChooser );

		// set png format as the unique filter
		Arrays.stream( fileChooser.getChoosableFileFilters() )
			  .forEach( f -> fileChooser.removeChoosableFileFilter( f ) );
		fileChooser.addChoosableFileFilter( new FileNameExtensionFilter(
										"JDrafting exercise (.jd)", "jd" ) );
		// reset preview
		fileChooser.setSelectedFile( null ); // ensure property change trigger
		PreviewPanel prev = 
					(PreviewPanel) fileChooser.getAccessory().getComponent( 0 ); 
		prev.exercise = null;
		
		// Open dialog
		if ( fileChooser.showOpenDialog( app ) == JFileChooser.APPROVE_OPTION )
			app.openFile( fileChooser.getSelectedFile() );
	}
	
	private class PreviewPanel extends JPanel implements PropertyChangeListener
	{
		private static final int SIZE = 200;		
		private Exercise exercise;
		
		PreviewPanel()
		{
			setPreferredSize( new Dimension( SIZE, SIZE ) );
			setOpaque( true );
		}
		
		@Override
		public void propertyChange( PropertyChangeEvent e )
		{
			String propertyName = e.getPropertyName();
			
			if ( propertyName.equals( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY ) )
			{
	            File selected = (File) e.getNewValue();
	            
	            if ( selected != null )
	            {
	            	try ( FileInputStream is = new FileInputStream( selected ) )
	            	{
	            		exercise = 
	            			(Exercise) new ObjectInputStream( is ).readObject();
	            	}
	            	catch ( IOException | ClassNotFoundException ex ) {}
	            }
	            else
	            	exercise = null;

	            repaint();
			}			
		}
		
		@Override
		public void paintComponent( Graphics g )
		{
			super.paintComponent( g );
			
			if ( exercise != null )
			{
				setBackground( exercise.getBackgroundColor() );
				
				Graphics2D g2 = (Graphics2D) g;				
				// High quality render
				JDUtils.setHighQualityRender( g2 );
				
				final Rectangle2D bounds = exercise.getBounds();
				int width = SIZE;
				int height = Math.min( getPreferredSize().height,
									   (int) ( width * bounds.getHeight() / bounds.getWidth() ) );
				CanvasPanel.drawExercise( g2, CanvasPanel.getTransform( 
															new Viewport( bounds ), 
															new Viewport( 0, width, 0, height ) ),
										  exercise, new HashSet<>(), false );
			}
			else
				setBackground( fileChooser.getBackground() );
		}
	}
}
