package jdrafting.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jdrafting.geom.JDraftingShape;

@SuppressWarnings("serial")
public class ShapeList extends JList<JDraftingShape>
{
	private Application app;
	
	public ShapeList( Application app )
	{
		super( new DefaultListModel<JDraftingShape>() );
		
		this.app = app;
		
		setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		// listeners
		addMouseListener( new ShapeListMouseAdapter() );
		getSelectionModel().addListSelectionListener( 
											new ShapeListSelectionListener() );
	}
	
	@Override
	public ListCellRenderer<? super JDraftingShape> getCellRenderer()
	{
		return new ShapeListRenderer();
	}
	
	@Override
	public DefaultListModel<JDraftingShape> getModel()
	{
		return (DefaultListModel<JDraftingShape>) super.getModel();
	}
	
	/**
	 * MouseAdapter 
	 */
	class ShapeListMouseAdapter extends MouseAdapter
	{
		@Override
		public void mousePressed( MouseEvent e )
		{
			if ( e.getClickCount() > 1 )
			{
				// get selected shape
				final int index = ShapeList.this.getSelectionModel().getLeadSelectionIndex();
				final JDraftingShape jdshape = ShapeList.this.getModel().get( index );
				// edit shape
				new EditShapeDialog( app, jdshape ).setVisible( true );
				e.consume();
			}
			if ( e.getButton() == MouseEvent.BUTTON3 )
			{
				// TODO PopupMenu?
				e.consume();
			}
		}
	}
	
	/**
	 * ListSelectionListener 
	 */
	class ShapeListSelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged( ListSelectionEvent e )
		{
			final ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			final int first = lsm.getMinSelectionIndex(),
					  last = lsm.getMaxSelectionIndex();

			app.setSelectedShapes( new HashSet<>() );
			
			for ( int index = first; index <= last; index++ )
				if ( lsm.isSelectedIndex( index ) )
					app.getSelectedShapes().add( getModel().get( index ) );

			// TODO how to always force valueChanged?
			
			app.getCanvas().repaint();
			app.scrollList.repaint();
		}
	}
	
	/**
	 * ListCellRenderer 
	 */
	class ShapeListRenderer extends JPanel implements ListCellRenderer<JDraftingShape>
	{
		@Override
		public Component getListCellRendererComponent(
				JList<? extends JDraftingShape> list, JDraftingShape value, 
				int index, boolean isSelected, boolean cellHasFocus ) 
		{
			removeAll();
			setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
			setOpaque( true );
			setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
			// index
			JLabel shapeIndex = new JLabel( index + 1 + ":" );
			add( shapeIndex );
			shapeIndex.setFont( new Font( Font.SANS_SERIF, Font.ITALIC, 14 )  );
			shapeIndex.setForeground( Color.BLACK );
			// miniature
			add( Box.createHorizontalStrut( 2 ) );
			JPanel mini = new JPanel() {
				@Override
				protected void paintComponent( Graphics g )
				{
					setBackground( app.getBackColor() );
					super.paintComponent(g);
					
					Graphics2D g2 = (Graphics2D) g;
					
					// High quality render
					JDUtils.setHighQualityRender( g2 );

					Viewport viewDest = new Viewport( 0, getWidth() - 1, 
													  0, getHeight() - 1 );  
					Rectangle2D bounds = CanvasPanel.getExerciseBounds( 
							app.getExercise(), 
							new Viewport( app.getExercise().getBounds() ), 
							viewDest );
					
					double size =
							Math.max( bounds.getWidth(), bounds.getHeight() );
					bounds = new Rectangle2D.Double(
							bounds.getCenterX() - size / 2.,
							bounds.getCenterY() - size / 2.,
							size, size );
					AffineTransform transform = CanvasPanel.getTransform(
											new Viewport( bounds ), viewDest );
					HashSet<JDraftingShape> selected = new HashSet<>();
					selected.add( value );
					CanvasPanel.drawExercise( g2, transform, app.getExercise(), 
											  selected, false );
				}
			};
			add( mini );
			mini.setPreferredSize( new Dimension( 64, 64 ) );
			mini.setMaximumSize( mini.getPreferredSize() );
			mini.setMinimumSize( mini.getPreferredSize() );
			// name
			add( Box.createHorizontalStrut( 4 ) );
			JLabel shapeName = new JLabel( value.getName() != null && value.getName().length() > 0
										   ? value.getName()
										   : "", JLabel.LEFT );
			add( shapeName );
			shapeName.setFont( new Font( Font.SANS_SERIF, Font.BOLD, 20 )  );
			shapeName.setForeground( Color.BLUE );			
			// description
			add( Box.createHorizontalStrut( 4 ) );
			JLabel shapeDesc = new JLabel( "", JLabel.LEFT );
			add( shapeDesc );
			if ( value.getDescription().length() > 0 )
			{
				shapeDesc.setText( "<html>" + String.join( "<br/>", 
						value.getDescription().split( "\n" ) ) + "</html>" );
				setToolTipText( shapeDesc.getText() );
			}

			shapeDesc.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, 12 )  );
			shapeDesc.setForeground( Color.GRAY );
			
			// background color
			if ( app.getSelectedShapes().contains( value ) )  // selected
				setBackground( new Color( 255, 127, 127 ) );
			else if ( !app.getExercise().isIndexAtEnd()   // visible
					  && index >= app.getExercise().getFrameIndex() )
				setBackground( Color.LIGHT_GRAY );
			else
				setBackground( ShapeList.this.getBackground() );

			return this;
		}
	}	
}
