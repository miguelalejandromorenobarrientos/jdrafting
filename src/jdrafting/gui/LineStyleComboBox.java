package jdrafting.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import jdrafting.geom.JDStrokes;

@SuppressWarnings("serial")
public class LineStyleComboBox extends JComboBox<BasicStroke>
{
	private Application app;

	public LineStyleComboBox( Application app )
	{
		super( Arrays.stream( JDStrokes.values() )
					 .map( v -> v.getStroke() )
					 .toArray( BasicStroke[]::new ) );		
		this.app = app;
		
		setBackground( new Color( 200, 200, 255 ) );
		
		addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if ( e.getSource() instanceof JComboBox<?> )
				{
					JComboBox<?> combo = (JComboBox<?>) e.getSource();
					app.setStroke( JDStrokes.getStroke(
									(BasicStroke) combo.getSelectedItem(),
									app.getStroke().getLineWidth() ) ); 
				}
			}
		} );
	}
	
	@Override
	public ListCellRenderer<? super BasicStroke> getRenderer()
	{
		return new LineRenderer();
	}
	
	
	private class LineRenderer extends JPanel implements 
												ListCellRenderer<BasicStroke>
	{
		private BasicStroke stroke;

		LineRenderer()
		{
			setBackground( new Color( 222, 222, 255 ) );
		}
		
		@Override
		public Component getListCellRendererComponent(
			JList<? extends BasicStroke> list, BasicStroke value, int index,
			boolean isSelected, boolean cellHasFocus )
		{
			stroke = value;
			
			return this;  // render
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor( app.getColor() );
			g2.setStroke( JDStrokes.getStroke( stroke, 
							Math.min( app.getStroke().getLineWidth(), 12 ) ) );		
			g2.drawLine( 8, getHeight() / 2, 
					(int) getPreferredSize().getWidth() - 9, getHeight() / 2 );
		}
		
		@Override
		public Dimension getPreferredSize()
		{
			return LineStyleComboBox.this.getSize();
		}
	}
}
