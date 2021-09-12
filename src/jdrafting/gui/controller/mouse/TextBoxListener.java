package jdrafting.gui.controller.mouse;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import jdrafting.geom.JDraftingShape;
import jdrafting.gui.Application;
import jdrafting.gui.CanvasPanel;
import jdrafting.gui.JDUtils;

/**
 * Create a text box for comments
 */
public class TextBoxListener extends AbstractCanvasMouseListener
{
	private static final Cursor CURSOR = JDUtils.getCustomCursor( "text_cursor.png" );
	private CanvasPanel canvas;
	private Application app;
	
	private Point2D start,  // upper-left corner
					end;  // lower-right corner
	
	public TextBoxListener( CanvasPanel canvas )
	{
		super( canvas );
		
		this.canvas = canvas;
		app = canvas.getApplication();
		
		canvas.setCursor( CURSOR );
		
		app.setStatusText( getLocaleText( "txt_comment1" ) );
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		super.mouseMoved( e );

		if ( start == null )  return;
		
		// dynamic cursor
		final Point2D pos = canvas.getInverseTransform().transform( e.getPoint(), null );
		canvas.setCursor( new Cursor( 
								   ( pos.getX() - start.getX() ) * ( pos.getY() - start.getY() ) > 0
							   	   ? Cursor.NE_RESIZE_CURSOR
							   	   : Cursor.NW_RESIZE_CURSOR ) );		
		canvas.repaint();
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		super.mouseReleased( e );

		// mouse position in logic viewport
		final Point2D logicMouse = canvas.adjustToPoint( e.getPoint() );

		// put first corner
		if ( start == null )
		{
			start = logicMouse;
			//app.setStatusText( getLocaleText( "txt_rect2" ) );
		}
		// finish rectangle
		else
		{
			end = logicMouse;
			showTxtDialog();

			// back to select mode
			canvas.setCanvasListener( new HandListener( canvas ) );
		}

		canvas.repaint();
	}
	
	@Override
	public void paintTool( Graphics2D g2 )
	{
		if ( start != null )
		{
			// mouse position in logic viewport
			Point2D logicMouse = canvas.adjustToPoint( mouse().getPoint() );
			
			// set style
			g2.setStroke( new BasicStroke( 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL ) );
			g2.setColor( Application.toolMainColor );
			
			// draw rectangle
			g2.draw( canvas.getTransform().createTransformedShape( getRectangle( logicMouse ) ) );
		}
	}
	
	// --- HELPERS ---

	// check modifiers
	//private boolean isSquare() { return mouse().isShiftDown(); }
	
	@SuppressWarnings("serial")
	private void showTxtDialog()
	{
		// dialog for comment and font family-style
		final JDialog dialog = new JDialog( app, true );
		dialog.setTitle( getLocaleText( "comment_des" ) );
		
		// content panel
		final JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
		dialog.getContentPane().add( panel );
		
		final Box box = Box.createVerticalBox();
		panel.add( box );
		
		// text area for comment	
		final JTextArea textArea = new JTextArea( app.getExercise().getDescription(), 8, 50 );
		JScrollPane scrollArea = new JScrollPane( textArea );
		scrollArea.setAlignmentX( JLabel.LEFT_ALIGNMENT );
		box.add( scrollArea );

		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		
		// family-style panel
		final JPanel formatPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		formatPanel.setAlignmentX( JLabel.LEFT_ALIGNMENT );
		formatPanel.setMaximumSize( 
						new Dimension( Integer.MAX_VALUE, scrollArea.getPreferredSize().height ) );
		box.add( formatPanel );
		
		// get font family names
		final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String[] fontNames = genv.getAvailableFontFamilyNames( app.getLocale() );
		
		// combo box for family names
		final JComboBox<String> comboFont = new JComboBox<>();
		for ( final String fontName : fontNames )
			comboFont.addItem( fontName );
		comboFont.setSelectedItem( "Monospaced" );
		formatPanel.add( comboFont );
		
		// checkboxes for bold & italic
		final JCheckBox checkBold = new JCheckBox( "Bold" ),
						checkItalic = new JCheckBox( "Italic" );
		formatPanel.add( checkBold );
		formatPanel.add( checkItalic );
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
	    
		// panel for ok/cancel buttons
		final JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );	    
		btnPanel.setAlignmentX( JLabel.LEFT_ALIGNMENT );
		btnPanel.setMaximumSize( 
						new Dimension( Integer.MAX_VALUE, scrollArea.getPreferredSize().height ) );
		
	    box.add( btnPanel );
	    
	    // ok button
	    JButton btnOk = new JButton( getLocaleText( "save_close" ) );
	    btnPanel.add( btnOk );
	    Action okAction = new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				// create new comment
				final JDraftingShape shape = app.addShapeFromIterator( 
													getRectangle(end).getPathIterator( null ), 
													"", 
													textArea.getText(), 
													app.getColor(), 
													app.getStroke() );
				shape.setAsText( true );
				// get style
				int fontStyle = Font.PLAIN;
				if ( checkBold.isSelected() )
				{
					if ( checkItalic.isSelected() )
						fontStyle = Font.BOLD | Font.ITALIC;
					else
						fontStyle = Font.BOLD;
				}
				else if ( checkItalic.isSelected() )
					fontStyle = Font.ITALIC;
				// set font from dialog
				shape.setFont( new Font(comboFont.getSelectedItem().toString(), fontStyle, 1000 ));
				// close dialog
				dialog.setVisible( false );
				dialog.dispose();
			}
		};
		btnOk.addActionListener( okAction ); 
	    // cancel button
		JButton btnCancel = new JButton( getLocaleText( "cancel" ) );
	    btnPanel.add( btnCancel );
	    final Action cancelAction = new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				dialog.setVisible( false );
				dialog.dispose();
			}
		};
		btnCancel.addActionListener( cancelAction );
		
		// ESCAPE 
        dialog.getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        	.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "ESCAPE" );
        dialog.getRootPane().getActionMap().put( "ESCAPE", cancelAction );
        // ENTER
        dialog.getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        	.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "ENTER" );
        dialog.getRootPane().getActionMap().put( "ENTER", okAction );
		
        // window
        dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.pack();
        dialog.setVisible( true );
		dialog.setLocationRelativeTo( app );
	}
	
	/**
	 * Gets the rectangle 
	 * @param logicMouse
	 * @return the rectangle in the logic viewport
	 */
	private Rectangle2D getRectangle( Point2D logicMouse )
	{
		// calculate coords
		double x = app.isUsingRuler()
				   ? start.getX() > logicMouse.getX()
					 ? start.getX() - app.getDistance()
					 : start.getX() + app.getDistance()  
				   : logicMouse.getX(),
			   minX = Math.min( start.getX(), x ),
			   maxX = Math.max( start.getX(), x ),
			   minY = Math.min( start.getY(), logicMouse.getY() ),
			   maxY = Math.max( start.getY(), logicMouse.getY() );
		
		// create rectangle
		final Rectangle2D rect = new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
		
		return rect;
	}
}
