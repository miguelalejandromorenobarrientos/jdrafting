package jdrafting.gui;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import jdrafting.geom.JDraftingShape;

@SuppressWarnings("serial")
public class EditShapeDialog extends JDialog
{
	public EditShapeDialog( Application parent, JDraftingShape jdshape )
	{
		super( parent, getLocaleText( "shape_prop" ), true );
		
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
		getContentPane().add( panel );
		
		Box box = Box.createVerticalBox();
		panel.add( box );
		
		JLabel labelName = new JLabel( getLocaleText( "shape_name" ) );
		labelName.setAlignmentX( LEFT_ALIGNMENT );
		box.add( labelName );
		
		final JTextField textName =	new JTextField( jdshape.getName() );
		textName.setAlignmentX( LEFT_ALIGNMENT );
		textName.setMaximumSize( new Dimension( 
					Integer.MAX_VALUE, textName.getPreferredSize().height ) );
		box.add( textName );
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		
		JLabel labelDesc = new JLabel( getLocaleText( "shape_desc" ) );
		labelDesc.setAlignmentX( LEFT_ALIGNMENT );
		box.add( labelDesc );
		
		final JTextArea taDesc = 
							new JTextArea( jdshape.getDescription(), 6, 50 );
		JScrollPane scrollArea = new JScrollPane( taDesc );
		scrollArea.setAlignmentX( LEFT_ALIGNMENT );
		box.add( scrollArea );
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		
		JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );	    
		btnPanel.setAlignmentX( LEFT_ALIGNMENT );
		btnPanel.setMaximumSize( new Dimension( 
				Integer.MAX_VALUE, textName.getPreferredSize().height ) );
		
	    box.add( btnPanel );

	    // ok button
	    JButton btnOk = new JButton( getLocaleText( "save_close" ) );
	    btnPanel.add( btnOk );
	    Action okAction = new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				// modify shape properties
				jdshape.setName( textName.getText() );
				jdshape.setDescription( taDesc.getText() );
				
				// refresh GUI			
				parent.getCanvas().repaint();
				parent.refreshUndoRedo();
				parent.scrollList.repaint();

				dispose();
			}
		};
		btnOk.addActionListener( okAction ); 
		// details button
	    JButton btnDetails = new JButton( getLocaleText( "details" ) );
	    btnPanel.add( btnDetails );
	    btnDetails.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				String message = 
						String.join( "\n{", jdshape.toString().split( "\\{" ) );
				JTextArea txtarea = new JTextArea( message );
				txtarea.setEditable( false );
				JScrollPane scroll = new JScrollPane( txtarea );
				scroll.setPreferredSize( new Dimension(
					(int) txtarea.getPreferredSize().getWidth() + 20, 500 ) );
				JOptionPane.showMessageDialog( EditShapeDialog.this, scroll,
						getLocaleText( "details" ) + ": " + jdshape.toString(),
						JOptionPane.PLAIN_MESSAGE );
			}
		});
	    // cancel button
	    JButton btnCancel =	new JButton( getLocaleText( "cancel" ) );
	    btnPanel.add( btnCancel );
	    Action cancelAction = new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
				dispose();
			}
		};
		btnCancel.addActionListener( cancelAction );

		// ESCAPE 
        getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        	.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "ESCAPE" );
        getRootPane().getActionMap().put( "ESCAPE", cancelAction );
        // ENTER
        getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW )
        	.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "ENTER" );
        getRootPane().getActionMap().put( "ENTER", okAction );
		
        // window
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		pack();
		setLocationRelativeTo( parent );
	}
}
