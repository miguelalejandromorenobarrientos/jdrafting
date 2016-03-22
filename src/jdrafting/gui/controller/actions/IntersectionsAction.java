package jdrafting.gui.controller.actions;

import static jdrafting.gui.Application.getLargeIcon;
import static jdrafting.gui.Application.getLocaleText;
import static jdrafting.gui.Application.getSmallIcon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;

@SuppressWarnings("serial")
public class IntersectionsAction extends AbstractAction
{
	private Application app;
	
	public IntersectionsAction( Application app )
	{
		this.app = app;
		
		putValue( NAME, getLocaleText( "inter" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "inter_des" ) );
		putValue( MNEMONIC_KEY, KeyEvent.VK_I );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( 
				KeyEvent.VK_0, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "intersection.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "intersection.png" ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if ( app.getSelectedShapes().size() == 2 )
		{
			Color color = ( e.getModifiers() & ActionEvent.SHIFT_MASK ) 
							== ActionEvent.SHIFT_MASK
						  ? app.getColor()
						  : app.getPointColor();
			BasicStroke stroke = ( e.getModifiers() & ActionEvent.SHIFT_MASK ) 
									== ActionEvent.SHIFT_MASK
					 			 ? app.getStroke()
					 			 : app.getPointStroke();
			JDraftingShape[] shapes = 
					app.getSelectedShapes().toArray( new JDraftingShape[2] );
			List<Point2D> joins = CanvasPanel.intersectionPoints( shapes[0],
																  shapes[1] );
			if ( joins.isEmpty() )
			{
				JOptionPane.showMessageDialog( app, 
						"No joins", "Error while intersecting",
						JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			// create undo/redo transaction
			CompoundEdit transaction = new CompoundEdit() {
				@Override
				public boolean canRedo() { return true; };
				@Override
				public boolean canUndo() { return true; };
				@Override
				public String getRedoPresentationName()
				{
					return
						"Redo intersections (" + edits.size() + " points)";
				}
				@Override
				public String getUndoPresentationName()
				{
					return
						"Undo intersections (" + edits.size() + " points)";
				}
			};
			
			// add instersections to exercise
			joins
			.stream()
			.forEach( join -> app.addShapeFromIterator(
					new JDPoint( join ).getPathIterator( null ), "",
					"> " + getLocaleText( "new_join" )
					+ " [" + shapes[0].getName() + "," 
					+ shapes[1].getName() + "]", color, stroke, transaction ) );
		
			transaction.end();
			app.undoSupport.postEdit( transaction );
			
			// refresh
			app.getCanvas().repaint();
			app.scrollList.repaint();
		}
		else
			JOptionPane.showMessageDialog( app,	getLocaleText( "inter_msg" ),
					getLocaleText( "inter_dlg" ), JOptionPane.ERROR_MESSAGE );
	}
}
