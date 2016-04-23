package jdrafting.gui.controller.mouse;

import static jdrafting.geom.JDMath.lineAng;
import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Capture an angle using mouse control 
 */
public class ProtractorListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = 
						JDUtils.getCustomCursor( "protractor_cursor.png" );
	private CanvasPanel canvas;
	private Application app;

	private Point2D vertex, p1;	
	
	public ProtractorListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_prot1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );
		
		if ( vertex != null )
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );
		
		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// put vertex
		if ( vertex == null )
		{
			vertex = logicMouse;
			app.setStatusText( getLocaleText( "txt_prot2" ) );
		}
		// put first angle side
		else if ( p1 == null )
		{
			p1 = logicMouse;
			app.setStatusText( getLocaleText( "txt_prot3" ) );
		}
		// put second angle side
		else
		{
			Point2D p2 = logicMouse;

			// set captured angle
			double ang = lineAng( new Line2D.Double( vertex, p1 ), 
					 			  new Line2D.Double( vertex, p2 ) );
			app.setAngle( Math.toDegrees( ang ) );
			
			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
		
		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( vertex != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );

			// get transform
			AffineTransform transform = canvas.getTransform();

			// set tool style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.TOOL_MAIN_COLOR );			
			
			// draw first side
			Line2D line1 =
					new Line2D.Double( vertex, p1 == null ? logicMouse : p1 );			
			g2.draw( transform.createTransformedShape( line1 ) );			

			if ( p1 != null )
			{
				// draw second side
				Line2D line2 = new Line2D.Double( vertex, logicMouse );
				g2.draw( transform.createTransformedShape( line2 ) );

				// draw angle
				double ang = Math.toDegrees( lineAng( line1, line2 ) );				
				double dist = Math.min( vertex.distance( p1 ), 
										vertex.distance( logicMouse ) ) / 2.;
				double offang = Math.toDegrees( Math.atan2(
					p1.getY() - vertex.getY(), p1.getX() - vertex.getX() ) );
				
				Arc2D arc = new Arc2D.Double( 
					vertex.getX() - dist / 2, vertex.getY() - dist / 2,
					dist, dist,
					-offang, line1.relativeCCW( logicMouse ) <= 0 ? -ang : ang,
					Arc2D.PIE );
				
				g2.setColor( Color.MAGENTA );
				g2.setStroke( new BasicStroke( 2f, BasicStroke.CAP_ROUND, 
							BasicStroke.JOIN_ROUND, 0f, new float[] { 3f, 5f }, 
							(float) Math.random() * 4 ) );
				g2.draw( transform.createTransformedShape( arc ) );					
				
				// draw angle info
				int mouseX = mouse().getX(), mouseY = mouse().getY();
				String angInfo = String.format( "%.2f", ang ) + "º";
				g2.setFont( new Font( Font.SERIF, Font.BOLD, 16 ) );
				g2.setColor( new Color( 40, 40, 180 ) );
				g2.drawString( angInfo, mouseX + 21, mouseY - 9 );
				g2.setColor( Color.LIGHT_GRAY );			
				g2.drawString( angInfo, mouseX + 20, mouseY - 10 );
			}
		}
	}
}
