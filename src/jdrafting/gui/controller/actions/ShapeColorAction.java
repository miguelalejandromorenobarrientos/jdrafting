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

@SuppressWarnings("serial")
public class ShapeColorAction extends AbstractAction
{
	private Application app;
	private JDialog colorChooser;
	private JColorChooser jcc;
	
	public ShapeColorAction( Application app )
	{
		this.app = app;

		putValue( NAME, getLocaleText( "color" ) );
		putValue( SHORT_DESCRIPTION, getLocaleText( "color_des" ) );
		putValue( MNEMONIC_KEY, JDUtils.getLocaleMnemonic( "mne_shape_col" ) );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_J, InputEvent.CTRL_MASK ) );
		putValue( SMALL_ICON, getSmallIcon( "color.png" ) );
		putValue( LARGE_ICON_KEY, getLargeIcon( "color.png" ) );

		jcc = new JColorChooser();
		colorChooser = JColorChooser.createDialog( 
								app, getLocaleText( "color_des" ), true, jcc, 
								evt -> app.setColor( jcc.getColor() ), // ok
								null ); // cancel 

		// "transparent" button and panel
		final Box box = new Box( SwingConstants.VERTICAL );
		box.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
		box.add( Box.createVerticalGlue() );
		final JButton btnTransparent = new JButton( "Transparent" );
		btnTransparent.addActionListener( e -> {
			app.setColor( new Color( 0, 0, 0, 0 ) );
			colorChooser.setVisible( false );
		});
		btnTransparent.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( btnTransparent );
		// choose fill color button
		final JButton btnFillColor = new JButton() {
			protected void paintComponent( Graphics g ) {
				super.paintComponent(g);
				g.setColor( app.getFill() != null ? app.getFill() : new Color( 0, 0, 0, 0 ) );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}
		};
		JDUtils.fixSize( btnFillColor, 40, 40 );
		btnFillColor.addActionListener( e -> { 
			app.setColor( app.getFill() != null ? app.getFill() : new Color( 0, 0, 0, 0 ) ); 
			colorChooser.setVisible( false ); 
		} );
		btnFillColor.setAlignmentX( Component.CENTER_ALIGNMENT );
		box.add( Box.createVerticalStrut( 16 ) );
		box.add( btnFillColor );
		JLabel label = new JLabel( getLocaleText( "lbl_fill_color" ), SwingConstants.CENTER );
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
			app.setColor( app.getPointColor() ); 
			colorChooser.setVisible( false ); 
		});
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
			app.setPointColor( app.getColor() );
			app.setColor( point );
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
		jcc.setColor( app.getColor() );
		jcc.requestFocus();
		colorChooser.setSize( new Dimension( 750, 380 ) );
		colorChooser.setVisible( true );
	}
}
