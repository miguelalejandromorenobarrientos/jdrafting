package jdrafting.gui.controller.actions;

import static jdrafting.gui.JDUtils.getLargeIcon;
import static jdrafting.gui.JDUtils.getLocaleText;
import static jdrafting.gui.JDUtils.getSmallIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jdrafting.gui.Application;
import jdrafting.gui.JDUtils;

/**
 * @since 0.1.11.2
 * @version 0.1.12
 */
@SuppressWarnings("serial")
public class ShapeFillAction extends AbstractAction
{
	private Application app;
	private JDialog colorChooser;
	private JColorChooser jcc;
	
	public ShapeFillAction( Application app )
	{
		this.app = app;

		putValue( NAME, getLocaleText( "fill" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "fill_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_shape_fill" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "fill_color.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "fill_color.png" ) );

		// JFileChooser
		jcc = new JColorChooser();
		colorChooser = JColorChooser.createDialog( app, getLocaleText( "fill_des" ), true, jcc, 
												   evt -> app.setFill( jcc.getColor() ), // ok
												   null ); // cancel
		// "remove fill" button and panel
		final Box box = new Box( SwingConstants.VERTICAL );
		box.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
		box.add( Box.createVerticalGlue() );
		final JButton btnRemove = new JButton() {
			@Override
			protected void paintComponent( Graphics g ) {
				super.paintComponent( g );
				for ( int n = 5, step = getWidth() / n, i = 0; i < n; i++ )
					for ( int j = 0; j < n; j++ )
					{
						g.setColor( ((i+j)&1) == 0 ? Color.GRAY : Color.DARK_GRAY );
						g.fillRect( i*step, j*step, step, step );
					}
			}
		};
		JDUtils.fixSize( btnRemove, 40, 40 );
		btnRemove.addActionListener( e -> {
			app.setFill(null);
			colorChooser.setVisible( false );
		});
		btnRemove.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( btnRemove );
		JLabel label = new JLabel( getLocaleText( "lbl_no_fill" ), SwingConstants.CENTER );
		label.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( label );
		// choose line color button
		final JButton btnLineColor = new JButton() {
			protected void paintComponent( Graphics g ) {
				super.paintComponent(g);
				g.setColor( app.getColor() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}
		};
		JDUtils.fixSize( btnLineColor, 40, 40 );
		btnLineColor.addActionListener( e -> { 
			app.setFill( app.getColor() ); 
			colorChooser.setVisible( false ); 
		} );
		btnLineColor.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( Box.createVerticalStrut( 16 ) );
		box.add( btnLineColor );
		label = new JLabel( getLocaleText( "lbl_line_color" ), SwingConstants.CENTER );
		label.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( label );
		final JButton btnLineSwap = new JButton( getSmallIcon( "swap_colors.png" ) );
		btnLineSwap.setToolTipText( getLocaleText( "tip_swap_color" ) );
		JDUtils.fixSize( btnLineSwap, 32, 32 );
		btnLineSwap.addActionListener( e -> {
			final Color line = app.getColor();
			app.setColor( app.getFill() != null ? app.getFill() : new Color( 0, 0, 0, 0 ) );
			app.setFill( line.getAlpha() > 0 ? line : null );
			colorChooser.setVisible( false ); 
		});
		btnLineSwap.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( btnLineSwap );
		// choose point color button
		final JButton btnPointColor = new JButton() {
			protected void paintComponent( Graphics g ) {
				super.paintComponent(g);
				g.setColor( app.getPointColor() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}
		};
		JDUtils.fixSize( btnPointColor, 40, 40 );
		btnPointColor.addActionListener( e -> { 
			app.setFill( app.getPointColor() ); 
			colorChooser.setVisible( false ); 
		} );
		btnPointColor.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( Box.createVerticalStrut( 16 ) );
		box.add( btnPointColor );
		label = new JLabel( getLocaleText( "lbl_point_color" ), SwingConstants.CENTER );
		label.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( label );
		final JButton btnPointSwap = new JButton( getSmallIcon( "swap_colors.png" ) );
		btnPointSwap.setToolTipText( getLocaleText( "tip_swap_color" ) );
		JDUtils.fixSize( btnPointSwap, 32, 32 );
		btnPointSwap.addActionListener( e -> {
			final Color point = app.getPointColor();
			app.setPointColor( app.getFill() != null ? app.getFill() : new Color( 0, 0, 0, 0 ) );
			app.setFill( point.getAlpha() > 0 ? point : null );
			colorChooser.setVisible( false ); 
		} );
		btnPointSwap.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( btnPointSwap );		
		
		box.add( Box.createVerticalGlue() );
		colorChooser.add( box, BorderLayout.WEST );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		jcc.setColor( app.getFill() );
		jcc.requestFocus();
		colorChooser.setSize( new Dimension( 750, 380 ) );
		colorChooser.setVisible( true );
	}
}
