package jdrafting.gui;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class FileInfoDialog extends JDialog
{
	public FileInfoDialog( Application parent )
	{
		super( parent, getLocaleText( "exer_prop" ), true );
		
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
		getContentPane().add( panel );
		
		Box box = Box.createVerticalBox();
		panel.add( box );
		
		// title textfield
		JLabel ltitle = new JLabel( getLocaleText( "title" ) );
		ltitle.setAlignmentX( LEFT_ALIGNMENT );
		box.add( ltitle );
		
		final JTextField textTitle = new JTextField( parent.getExercise().getTitle() );
		textTitle.setAlignmentX( LEFT_ALIGNMENT );
		textTitle.setMaximumSize( new Dimension( 
										Integer.MAX_VALUE, textTitle.getPreferredSize().height ) );
		box.add( textTitle );
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		
		// description area		
		JLabel lDesc = new JLabel( getLocaleText( "exer_desc" ) );
		lDesc.setAlignmentX( LEFT_ALIGNMENT );
		box.add( lDesc );
		
		final JTextArea textArea = new JTextArea( parent.getExercise().getDescription(), 8, 50 );
		JScrollPane scrollArea = new JScrollPane( textArea );
		scrollArea.setAlignmentX( LEFT_ALIGNMENT );
		box.add( scrollArea );

		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

		// start frame spinner
		final JLabel lStartIndex = new JLabel( getLocaleText( "start_frame" ) );
		lStartIndex.setAlignmentX( LEFT_ALIGNMENT );
		box.add( lStartIndex );
		
		final int minValue = 1, 
				  maxValue = Math.max( 1, parent.getExercise().size() ),
				  value = Math.max( minValue, parent.getExercise().getStartIndex() );
		final JSpinner spinFrame = 
							new JSpinner( new SpinnerNumberModel( value, minValue, maxValue, 1 ) );
		spinFrame.getModel().addChangeListener( 
							e -> parent.getExercise().setStartIndex( (int) spinFrame.getValue() ) );
		spinFrame.setAlignmentX( LEFT_ALIGNMENT );
		spinFrame.setFont( new Font( Font.MONOSPACED, Font.BOLD, 20 ) );
		spinFrame.setMaximumSize( new Dimension( 100, spinFrame.getPreferredSize().height ) );
		box.add( spinFrame );
	
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
	    
		JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );	    
		btnPanel.setAlignmentX( LEFT_ALIGNMENT );
		btnPanel.setMaximumSize( new Dimension( 
										Integer.MAX_VALUE, textTitle.getPreferredSize().height ) );
		
	    box.add( btnPanel );
	    
	    // ok button
	    JButton btnOk = new JButton( getLocaleText( "save_close" ) );
	    btnPanel.add( btnOk );
	    Action okAction = new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				parent.getExercise().setTitle( textTitle.getText() );
				parent.getExercise().setDescription( textArea.getText() );
				setVisible( false );
				dispose();
			}
		};
		btnOk.addActionListener( okAction ); 
	    // cancel button
		JButton btnCancel =new JButton( getLocaleText( "cancel" ) );
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
