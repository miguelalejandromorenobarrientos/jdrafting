package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;
import jdrafting.gui.Viewport;

@SuppressWarnings("serial")
public class PrintAction extends AbstractAction
{
	private Application app;
	
	public PrintAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "print" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "print_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_print" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_L, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "print.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "print.png" ) );
	}
		
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getExercise().isEmpty() )
			return;
		
		// set job and get a printer
		final PrinterJob job = PrinterJob.getPrinterJob();
		job.setJobName( Application.APPNAME + ": " + app.getExercise().getTitle() );
		if ( !job.printDialog() )
			return;
		// set printable image
		job.setPrintable( (g, pageFormat, pageIndex) -> {
			
	    	final double pageWidth = pageFormat.getImageableWidth(),
	    				 pageHeight = pageFormat.getImageableHeight();	    

	    	Rectangle2D bounds = app.getExercise().getBounds();
			final double marginX = bounds.getWidth() / 16., marginY = bounds.getHeight() / 16.;
			bounds = new Rectangle2D.Double( bounds.getX() - marginX, 
											 bounds.getY() - marginY, 
											 bounds.getWidth() + 2*marginX, 
											 bounds.getHeight() + 2*marginY );
			
			final double scaleX = pageWidth / bounds.getWidth(),
						 scaleY = pageHeight / bounds.getHeight(),
						 scale = Math.min( scaleX, scaleY );			
			
			final BufferedImage img = new BufferedImage( (int) (bounds.getWidth() * scale), 
														 (int) (bounds.getHeight() * scale), 
														 BufferedImage.TYPE_INT_ARGB );			
			final Graphics2D g2 = (Graphics2D) img.getGraphics();
			
			// High quality render
			JDUtils.setHighQualityRender( g2 );
			
			// draw exercise in image context
			CanvasPanel.drawExercise( g2, CanvasPanel.getTransform(
									new Viewport( bounds ),
									new Viewport( 0, img.getWidth() - 1, 0, img.getHeight() - 1 ) ),
									app.getExercise(), 
									new HashSet<>(), true );			
			
			g.translate( (int) (pageFormat.getImageableX()), (int) (pageFormat.getImageableY()) );
		    if ( pageIndex == 0 ) 
		    {
		    	g.drawImage( img, 0, 0, null );
		    	return Printable.PAGE_EXISTS;
		    }
		    return Printable.NO_SUCH_PAGE;		    
		} );
		// print job
	    try
	    {
	    	job.print();
	    }
	    catch ( PrinterException ex )
	    {
	    	JOptionPane.showMessageDialog( app, ex.getMessage(), "Printing error", 
	    								   JOptionPane.ERROR_MESSAGE );
	    }
	}
}
