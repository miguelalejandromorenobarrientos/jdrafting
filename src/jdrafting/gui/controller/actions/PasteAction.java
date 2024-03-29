package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;
import jdrafting.gui.Toast;

@SuppressWarnings("serial")
public class PasteAction extends AbstractAction
{
	private Application app;
	
	public PasteAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "paste" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "paste_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_paste" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "paste.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "paste.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getInnerClipboard() != null && app.getInnerClipboard().length > 0 )
		{
			//////////////////////////// TRANSACTION ////////////////////////////			
			final JDCompoundEdit transaction = new JDCompoundEdit() {
				public String getPresentationName()
				{
					return getLocaleText( "paste" ) + " (" + edits.size() + " shapes)";
				}
			};								
	
			// move copies relative to originals and add them to exercise
			final double tx = 0. /*app.getCanvas().getViewport().getWidth() / 30.*/,
						 ty = 0. /*app.getCanvas().getViewport().getHeight() / 30.*/;
			final AffineTransform transform = AffineTransform.getTranslateInstance( tx, -ty );
			
			final Set<JDraftingShape> copySet = Arrays.stream( app.getInnerClipboard() )
								.map( jdshape -> { 
									final JDraftingShape copy = app.addShapeFromIterator( 
										jdshape.getShape().getPathIterator( transform ), 
										/*"copy of " +*/ jdshape.getName(), jdshape.getDescription(), 
										jdshape.getColor(), jdshape.getFill(), jdshape.getStroke(), 
										transaction );
									copy.setAsText( jdshape.isText() );
									copy.setFont( jdshape.getFont() );
									copy.setTextPosition( jdshape.getTextPosition() );
									return copy;
								})
								.collect( Collectors.toSet() );

			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////
	
			new Toast( String.format( "<html>%d %s</html>", 
					  				  app.getInnerClipboard().length, 
					  				  getLocaleText( "toast_paste" ) ), 
									  Toast.ONE_SECOND ).showToast();
			
			// change selection to new shapes
			app.setSelectedShapes( copySet );  
			
			// refresh
			app.scrollList.repaint();
			app.getCanvas().repaint();
		}
		else
		{
			final Toast toast = new Toast( String.format(
										"<html><font color=#FFFF00>%s</font></html>",
										getLocaleText( "toast_no_copy" ) ),
								Toast.ONE_SECOND );
			toast.getToastLabel().setBackground( new Color( 255, 75, 75 ) );
			toast.showToast();
		}
	}
}
