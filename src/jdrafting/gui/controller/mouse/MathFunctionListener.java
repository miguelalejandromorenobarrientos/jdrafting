package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Map;

import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;
import jme.Expresion;
import jme.abstractas.Numero;
import jme.abstractas.Terminal;
import jme.excepciones.ExpresionException;
import jme.terminales.Vector;
import jme.terminales.VectorEvaluado;

/**
 * Create jme function from bounds by mouse control 
 */
public class MathFunctionListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "jme_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private Point2D start;
	private Point2D[] values;
	private Map<String,Object> jmeParams;
	
	public MathFunctionListener( CanvasPanel canvas, Map<String,Object> jmeParams )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		this.jmeParams = jmeParams;
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_jme1" ) );		
		
		// calculate graph
		Expresion exp = (Expresion) jmeParams.get( "expression" );		
		int n = (Integer) jmeParams.get( "intervals" ) + 1;  // number of points
		double xa = (Double) jmeParams.get( "xmin" );
		double xb = (Double) jmeParams.get( "xmax" );
		
		values = new Point2D[n];
		for ( int i = 0; i < n; i++ )
		{
			double x = xa + i * ( xb - xa ) / ( n - 1 );
			exp.setVariable( "x", x );
			exp.setVariable( "t", x );
			try
			{
				Terminal result = exp.evaluar();
				if ( result.esNumero() && !result.esComplejo() )
					values[i] = 
						new Point2D.Double( x, ( (Numero) result ).doble() );
				else if ( result.esVector() 
						  && ( (Vector) result ).dimension() == 2 )
				{
					VectorEvaluado v = (VectorEvaluado) result;
					Terminal t1 = v.getComponente( 0 );
					Terminal t2 = v.getComponente( 1 );
					if ( !t1.esNumero() || !t2.esNumero() 
						 || t1.esComplejo() || t2.esComplejo() )
						values[i] = null;
					else
						values[i] = new Point2D.Double( 
							( (Numero) t1 ).doble(), ( (Numero) t2 ).doble() );
				}
				else
					values[i] = null;				
			}
			catch ( ExpresionException e )
			{
				values[i] = null;
			}
		}		
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		if ( start != null )		
			canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );
		
		// put first point
		if ( start == null )
		{
			start = logicMouse;
			app.setStatusText( getLocaleText( "txt_jme2" ) );
			canvas.repaint();
		}
		// finish bounds
		else
		{
			// add function to exercise
			app.addShapeFromIterator( getGraph( logicMouse ).getPathIterator( null ),
									  "", jmeParams.get( "expression" ).toString(), 
									  app.getColor(), null, app.getStroke() );

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			AffineTransform transform = canvas.getTransform();

			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setStroke( new BasicStroke( 1f ) );
			g2.setColor( Application.toolMainColor );
			
			Path2D graph = getGraph( logicMouse );
			
			// draw graph
			g2.draw( transform.createTransformedShape( graph ) );
		}
	}
	
	// --- HELPERS ---
	
	/**
	 * Get the graph
	 * @param logicMouse
	 * @return the graph in the logic viewport
	 */
	private Path2D getGraph( Point2D logicMouse )
	{
		// calculate coords
		double px = app.isUsingRuler()
				    ? start.getX() > logicMouse.getX()
					  ? start.getX() - app.getDistance()
					  : start.getX() + app.getDistance()  
				    : logicMouse.getX();
		double minX = Math.min( start.getX(), px );
		double maxX = Math.max( start.getX(), px );

		// generate graph
		Path2D graph = new Path2D.Double();
		int n = values.length;
		double xa = (Double) jmeParams.get( "xmin" );
		double xb = (Double) jmeParams.get( "xmax" );
				
		for ( int i = 0; i < n; i++ )
			if ( values[i] != null )
			{
				double x = minX + ( values[i].getX() - xa ) * ( maxX - minX ) 
								/ ( xb - xa );
				double y = values[i].getY() * ( maxX - minX ) / ( xb - xa );
				boolean cond =
					// line except for first point
					i != 0  
					// and except points without previous image point
					&& values[ i - 1 ] != null
					// and except big steps in the graph
					&& Math.abs( values[ i - 1 ].getY() * ( maxX - minX ) 
										/ ( xb - xa ) - y ) < ( maxX - minX );
				if ( cond )
					graph.lineTo( x, y );
				else
					graph.moveTo( x, y );
			}

		return (Path2D) AffineTransform.getTranslateInstance( 0, start.getY() )
											.createTransformedShape( graph );
	}
}
