 package jdrafting;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jdrafting.geom.JDraftingShape;

/**
 * JDrafting exercise info (shapes, index, title, ...)
 * @author Miguel Alejandro Moreno Barrientos
 * @since 0.1.0
 */
public class Exercise implements Iterable<JDraftingShape>, Serializable
{
	private String title, description;	// exercise title & description
	private List<JDraftingShape> shapes;  // list of shapes of the exercise
	private Color backgroundColor = Color.WHITE;  // current background
	private int startIndex = 1;  // frame index where starts exercise (set data in first frames)
	transient private int frameIndex;  // current frame index in presentation mode
	
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public Exercise( String title, String description )
	{
		shapes = new ArrayList<>();
		frameIndex = shapes.size();
		this.title = title;
		this.description = description;
	}

	public Exercise() { this( "", "" ); }

	
	/////////////
	// GETTERS //
	/////////////
	
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public List<JDraftingShape> getShapes() { return shapes; }
	public Color getBackgroundColor() { return backgroundColor; }
	public int getStartIndex() { return startIndex; }
	public int getFrameIndex() { return frameIndex; }
	
	
	/////////////
	// SETTERS //
	/////////////
	
	public Exercise setTitle(String title) { this.title = title; return this; }
	public Exercise setDescription( String description )
	{
		this.description = description;
		
		return this;
	}
	public void setBackgroundColor( Color backgroundColor )
	{
		this.backgroundColor = backgroundColor;
	}
	public void setStartIndex( int startIndex ) { this.startIndex = startIndex; }
	public void setFrameIndex( int frameIndex )
	{
		this.frameIndex = frameIndex;
	}
	
	public int setFrameAtEnd()
	{
		frameIndex = shapes.size();
		return frameIndex;
	}

	/**
	 * Get shape at index
	 * @param index shape index [0,n)
	 * @return shape at index
	 */
	public JDraftingShape get( int index )
	{
		return getShapes().get( index );
	}
	
	/**
	 * Set shape at index
	 * @param index shape index [0,n)
	 * @param jdshape shape to store
	 */
	public void set( int index, JDraftingShape jdshape )
	{
		getShapes().set( index, jdshape );
	}
	
	
	/////////////
	// METHODS //
	/////////////
	
	/**
	 * Recover serialized exercise from stream and set index at the end 
	 * @param ois stream
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void readObject( ObjectInputStream ois ) throws IOException, 
														ClassNotFoundException
	{
		ois.defaultReadObject();
		frameIndex = shapes.size(); // put frame index at end
		if ( backgroundColor == null )  // (compatibility all files) // TODO
			backgroundColor = Color.WHITE;
	}
		
	/**
	 * Shape index
	 * @param jdshape shape
	 * @return index [0,n) or -1 if doesn't exist.
	 */
	public int indexOf( JDraftingShape jdshape )
	{
		return getShapes().indexOf( jdshape );
	}
	
	/**
	 * New shape at index 
	 * @param index index in [0,n]
	 * @param jdshape shape
	 * @return same index
	 * @throws ArrayIndexOutOfBoundsException 
	 * 			if the index is out of range(index < 0 || index > size())
	 */
	public int addShape( int index, JDraftingShape jdshape )
	{
		getShapes().add( index, jdshape );
		if ( index <= getFrameIndex() )
			setFrameIndex( getFrameIndex() + 1 );
		
		return index;
	}
	
	/**
	 * Add shape at the end
	 * @param jdshape shape
	 * @return insertion index
	 */
	public int addShape( JDraftingShape jdshape )
	{
		return addShape( getFrameIndex(), jdshape );
	}
	
	/**
	 * Remove shape at index
	 * @param index [0,n)
	 * @throws ArrayIndexOutOfBoundsException 
	 * 		   if the index is out of range(index < 0 || index >= size()) 
	 */
	public void removeShape( int index )
	{
		getShapes().remove( index );
		if ( index < getFrameIndex() )
			setFrameIndex( getFrameIndex() - 1 );
		if ( getStartIndex() > size() )
			setStartIndex( Math.max( 1, size() ) );
	}
	
	/**
	 * Remove shape
	 * @param jdshape shape
	 * @return removed shape index
	 */
	public int removeShape( JDraftingShape jdshape )
	{
		int index = indexOf( jdshape );
		removeShape( index );
		
		return index;
	}
	
	/**
	 * Return a sublist of the shapes from zero until frame index (exclusive)
	 * @return sublist [0,idx)
	 */
	public List<JDraftingShape> getFramesUntilIndex()
	{
		return getShapes().subList( 0, getFrameIndex() );
	}
	
	/**
	 * Check exercise has not shapes
	 * @return {@code true} if empty
	 */
	public boolean isEmpty() { return getShapes().isEmpty(); }
	
	/**
	 * Number of shapes in the exercise
	 * @return n
	 */
	public int size() { return getShapes().size(); }
	
	/**
	 * Check whether frame index is at the end 
	 * @return {@code true} if so
	 */
	public boolean isIndexAtEnd() {	return getFrameIndex() == size(); }
	
	/**
	 * Get the rectangle which contains all shapes in the exercise
	 * @return exercise bounds or null if empty
	 */
	public Rectangle2D getBounds()
	{
		if ( !getShapes().isEmpty() )
		{
			Rectangle2D enclosure = getShapes().get( 0 ).getShape().getBounds2D();
			
			for ( JDraftingShape jdshape : getShapes() )
				enclosure = enclosure.createUnion( jdshape.getShape().getBounds2D() );
			
			return enclosure;
		}
		
		return null;
	}
	
	@Override
	public Iterator<JDraftingShape> iterator() { return getShapes().listIterator(); }

	private static final long serialVersionUID = -2951147031143369819L;
}
