package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.adjustVectorToSize;
import static jdrafting.geom.JDMath.length;
import static jdrafting.geom.JDMath.sumVectors;
import static jdrafting.geom.JDMath.vector;
import static jdrafting.gui.JDUtils.elvis;
import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdrafting.geom.JDPoint;
import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDCompoundEdit;
import jdrafting.gui.JDUtils;

public class DivisionPointsListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "divisions_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private int divisions = 2;

	public DivisionPointsListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_div1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		JDraftingShape jdshape;
		canvas.setCursor( ( jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() ) ) == null
				 		  || jdshape.isPoint( jdshape.getVertex() )
				 		  ? CURSOR
				 		  : new Cursor( Cursor.HAND_CURSOR ) );
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		final JDraftingShape jdshape = canvas.getShapeAtCanvasPoint( e.getPoint() );
		
		if ( jdshape != null && !jdshape.isPoint( jdshape.getVertex() ) )
		{
			if ( !app.isUsingRuler() )
			{
				// dialog for division number
				final JSpinner spinDivisions = new JSpinner( 
											 new SpinnerNumberModel( 2, 1, Integer.MAX_VALUE, 1 ) );
				spinDivisions.addChangeListener( new ChangeListener() {
					@Override
					public void stateChanged( ChangeEvent e )
					{
						divisions = (int) ((JSpinner) e.getSource()).getValue();
					}
				});
				
				int option = JOptionPane.showOptionDialog( app, spinDivisions, 
					getLocaleText( "div_dlg" ),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
					getLargeIcon( "divisions.png" ), null, null );
				if ( option == JOptionPane.CANCEL_OPTION 
					 || option == JOptionPane.CLOSED_OPTION )
				{
					// back to select mode
					canvas.setCanvasListener( new HandListener( canvas ) );
					return;
				}
			}
			
			// point style
			final Color color = isPointStyle()
								? app.getColor()
								: app.getPointColor();
			final BasicStroke stroke = isPointStyle()
								 	   ? app.getStroke()
								 	   : app.getPointStroke();
			
			// shape length and interval length
			final double length = length( jdshape.getShape(), null ),			
						 interval = app.isUsingRuler()
						 			? app.getDistance()
						 			: length / Math.nextUp( divisions );
			
			// shape iterator vars
			final PathIterator path = jdshape.getShape().getPathIterator( null );
			final double[] current = new double[6];
			path.currentSegment( current );
			double x = current[0], 
				   y = current[1];

			// point list
			final List<Point2D> pointList = new ArrayList<>();
			// add start point to open shapes
			if ( !jdshape.isClosed( jdshape.getVertex() ) )
				pointList.add( new Point2D.Double( x, y ) );

			// iterate over shape
			double dist;
			double currentInterval = interval;
			while ( !path.isDone() )
			{
				int type = path.currentSegment( current );
				if ( type != PathIterator.SEG_MOVETO )
				{
					dist = Point2D.distance( x, y, current[0], current[1] );
					if ( dist >= currentInterval )
					{
						final Point2D xy = new Point2D.Double( x, y ),
									  v = vector( xy, new Point2D.Double( current[0], current[1] ) ),
									  point = sumVectors( xy, 
											  			  adjustVectorToSize( v, currentInterval ) );
						pointList.add( point );
						x = point.getX();
						y = point.getY();
						currentInterval = interval;
						continue;
					}
					else
						currentInterval -= dist;
				}
				
				x = current[0];
				y = current[1];				
				path.next();
			}
			
			final String descHtml = String.format( 
								"<font color=%s>[%s]</font>",
								Application.HTML_SHAPE_NAMES_COL, elvis( jdshape.getName(), "?" ) );					
			
			//////////////////////////// TRANSACTION ////////////////////////////			
			@SuppressWarnings("serial")
			final JDCompoundEdit transaction = new JDCompoundEdit() {
				public String getPresentationName()
				{
					return getLocaleText( "divisions" ) + " " + descHtml 
						   + " (" + edits.size() + " points)";
				}
			};
			
			// add points to exercise
			pointList
			.stream()
			.forEach( point -> app.addShapeFromIterator(
												new JDPoint( point ).getPathIterator( null ), "",
												getLocaleText( "new_div" ) + " " + descHtml, 
												color, null, stroke, transaction ) );
			
			transaction.end();
			app.undoRedoSupport.postEdit( transaction );
			/////////////////////////////////////////////////////////////////////
			
			// refresh
			app.scrollList.repaint();
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}		
	}
	
	private boolean isPointStyle() { return mouse().isShiftDown(); }
}
