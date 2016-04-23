package jdrafting.gui;

import static jdrafting.gui.JDUtils.getLocaleText;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

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
		
		JLabel ltitle = new JLabel( getLocaleText( "title" ) );
		ltitle.setAlignmentX( LEFT_ALIGNMENT );
		box.add( ltitle );
		
		final JTextField textTitle =
							new JTextField( parent.getExercise().getTitle() );
		textTitle.setAlignmentX( LEFT_ALIGNMENT );
		textTitle.setMaximumSize( new Dimension( 
					Integer.MAX_VALUE, textTitle.getPreferredSize().height ) );
		box.add( textTitle );
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		
		JLabel lDesc = new JLabel( getLocaleText( "exer_desc" ) );
		lDesc.setAlignmentX( LEFT_ALIGNMENT );
		box.add( lDesc );
		
		final JTextArea textArea = 
				new JTextArea( parent.getExercise().getDescription(), 8, 50 );
		JScrollPane scrollArea = new JScrollPane( textArea );
		scrollArea.setAlignmentX( LEFT_ALIGNMENT );
		box.add( scrollArea );
		
		box.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
	    
		JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );	    
		btnPanel.setAlignmentX( LEFT_ALIGNMENT );
		btnPanel.setMaximumSize( new Dimension( 
				Integer.MAX_VALUE, textTitle.getPreferredSize().height ) );
		
	    box.add( btnPanel );
	    
	    JButton btnOk = new JButton( getLocaleText( "save_close" ) );
	    btnPanel.add( btnOk );
		btnOk.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				parent.getExercise().setTitle( textTitle.getText() );
				parent.getExercise().setDescription( textArea.getText() );
				setVisible( false );
				dispose();
			}
		} );
	    JButton btnCancel =new JButton( getLocaleText( "cancel" ) );
	    btnPanel.add( btnCancel );
		btnCancel.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
				dispose();
			}
		} );
		
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		pack();
		setLocationRelativeTo( parent );
	}
		
	@Override
	protected JRootPane createRootPane()
	{
		JRootPane rootPanel = new JRootPane();
		
		// ESCAPE key
		KeyStroke stroke = KeyStroke.getKeyStroke( "ESCAPE" );
		Action action = new AbstractAction() {
		    public void actionPerformed(ActionEvent e)
		    {
		    	setVisible( false );
		    	dispose();
            }
        };
        InputMap inputMap = rootPanel.getInputMap(
        							JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put( stroke, "ESCAPE" );
        rootPanel.getActionMap().put( "ESCAPE", action );

        return rootPanel;
	}
}
