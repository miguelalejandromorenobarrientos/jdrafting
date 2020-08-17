// This file is part of Toast.
//
// Toast is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.
//
//     Toast is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.
//
//     You should have received a copy of the GNU General Public License
//     along with Toast.  If not, see <https://www.gnu.org/licenses/>.
package jdrafting.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Customizable Toast for Swing.<br>
 * <br>
 * Toast doesn't block EDT (Event Dispatch Thread).
 * 
 * @author Miguel Alejandro Moreno Barrientos, (C) 2019-2020
 * @version 0.1.2 (modified for JDrafting)
 */
public class Toast extends JWindow 
{
	private static final long serialVersionUID = 1L;

	public static final int HALF_SECOND = 500, 
							ONE_SECOND = 1000, 
							TWO_SECONDS = 2000, 
							FOUR_SECONDS = 4000; 
	
	private int time;  // time until start to disappear
	
	private Timer closingTimer;

	/** label to show */
	protected JLabel toastLabel;
	/** message */
	protected String msg;
	/** toast location (when developer gives the coordinates) */
	protected Point point = null;
	/** max message length shown */
	protected int maxLength = 80;

	/**
	 * Create toast x-centered and y-south
	 * 
	 * @param msg message to show
	 * @param time time for hiding (milliseconds)
	 */
	public Toast( @NotNull String msg, int time )
	{
		this.msg = msg;
		this.time = time;		

		setAlwaysOnTop( true );
		
		// initialize JLabel toast
		toastLabel = new JLabel( cutString( msg, maxLength, "\u2026" ) );
		toastLabel.setOpaque(true);
		toastLabel.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10) );
		toastLabel.setFont( new Font( Font.SANS_SERIF, Font.BOLD, 18 ) );
		toastLabel.setForeground( Color.WHITE );
		toastLabel.setBackground( Color.BLACK );
		add( toastLabel );	
	}

	/**
	 * Create toast in point (left-upper corner)
	 * 
	 * @param msg message to show
	 * @param point screen position (left-upper corner)
	 * @param time time for hiding (milliseconds)
	 */
	public Toast( @NotNull String msg, @Nullable Point point, int time ) 
	{
		this( msg, time );
		
		this.point = point;
	}
	
	/**
	 * Returns inner label.<br>
	 * Color, Font, Background can be modified
	 * (other changes could cause malfunction)
	 *   
	 * @return inner label
	 */
	public JLabel getToastLabel() { return toastLabel; }
	
	/**
	 * Max msg length
	 * 
	 * @return max string length
	 */
	public int getMaxLength() { return maxLength; }
	/**
	 * Sets max msg length <i>(too large string can overflow screen)</i>
	 * 
	 * @param maxLength {@code >=0}
	 * @return the toast
	 */
	public Toast setMaxLength( int maxLength ) 
	{ 
		this.maxLength = maxLength;		
		toastLabel.setText( cutString( msg, maxLength, "\u2026" ) );
		return this;
	}
	
	/**
	 * @return toast message
	 */
	public String getMsg() { return msg; }
	
	/**
	 * @return time for hiding
	 */
	public int getTime() { return time; }
	
	public @Nullable Timer getClosingTimer() { return closingTimer; }
	
	/**
	 * Show toast. Can be called several times
	 * 
	 * @return the toast
	 */
	public Toast showToast()
	{
		pack();
		
		// initial opacity
		setOpacity(0.8f);
		
		// show toast
		setVisible(true);

		// location
		if ( point == null )  // location x-centered and y-south
		{
			setLocationRelativeTo( null );
			setLocation( getLocation().x, 
						 getLocation().y + Math.round( 
							 						getToolkit().getScreenSize().height * 0.33f ) );
		}
		else
			setLocation( point );
		
		// closing toast
		closingTimer = new Timer( time, e -> {
			final Timer disappearTimer = new Timer( 20, evt -> {
				// hide transition
				if ( getOpacity() > 0.05f )
					setOpacity( getOpacity() - 0.02f );
				// stop timer and close toast
				else
                {
                	dispose();
                	setVisible( false );
                	((Timer) evt.getSource()).stop();
                }
			});  // disappearTimer
			disappearTimer.start();			
		});  // closingTimer
		closingTimer.setRepeats(false);
		closingTimer.start();	

		return this;
	}

	/**
	 * Cut string from limit
	 * 
	 * @param s message to cut
	 * @param limit max length of the original string (must be >= 0)
	 * @param sufix string at the end (as \u2026)
	 * @return cut string
	 */
	protected static @Nullable String cutString( @Nullable String s, 
										   int limit, @NotNull String sufix )
	{
		return s == null || limit >= s.length()
			   ? s
			   : s.substring( 0, limit ) + sufix;
	}
}
