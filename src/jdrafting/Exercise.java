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
 */
public class Exercise implements Iterable<JDraftingShape>, Serializable
{
	private String title, description;	
	private List<JDraftingShape> shapes;
	private Color backgroundColor = Color.WHITE;
	transient private int frameIndex;
	
	
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

	
	// getters
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public List<JDraftingShape> getShapes() { return shapes; }
	public Color getBackgroundColor() { return backgroundColor; }
	public int getFrameIndex() { return frameIndex; }
	
	// setters
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
	public void setFrameIndex( int frameIndex )
	{
		this.frameIndex = frameIndex;
	}
	
	public int setFrameAtEnd()
	{
		frameIndex = shapes.size();
		return frameIndex;
	}
	
	// I/O methods
	private void readObject( ObjectInputStream ois ) throws IOException, 
														ClassNotFoundException
	{
		ois.defaultReadObject();
		frameIndex = shapes.size(); // put frame index at end
		if ( backgroundColor == null )  // (compatibility all files) // TODO
			backgroundColor = Color.WHITE;
	}
	
	// methods
	public JDraftingShape get( int index )
	{
		return getShapes().get( index );
	}
	
	public void set( int index, JDraftingShape jdshape )
	{
		getShapes().set( index, jdshape );
	}
	
	public int indexOf( JDraftingShape jdshape )
	{
		return getShapes().indexOf( jdshape );
	}
	
	public int addShape( int index, JDraftingShape jdshape )
	{
		getShapes().add( index, jdshape );
		if ( index <= getFrameIndex() )
			setFrameIndex( getFrameIndex() + 1 );
		
		return index;
	}
	
	public int addShape( JDraftingShape jdshape )
	{
		return addShape( getFrameIndex(), jdshape );
	}
	
	public void removeShape( int index )
	{
		getShapes().remove( index );
		if ( index < getFrameIndex() )
			setFrameIndex( getFrameIndex() - 1 );
	}
	
	public int removeShape( JDraftingShape jdshape )
	{
		int index = indexOf( jdshape );
		removeShape( index );
		
		return index;
	}
	
	public List<JDraftingShape> getFramesUntilIndex()
	{
		return getShapes().subList( 0, getFrameIndex() );
	}
	
	public boolean isEmpty()
	{
		return getShapes().isEmpty();
	}
	
	public int size()
	{
		return getShapes().size();
	}
	
	public boolean isIndexAtEnd() {	return getFrameIndex() == size(); }
	
	/**
	 * Get the rectangle which contains all shapes in the exercise
	 * @return exercise bounds
	 */
	public Rectangle2D getBounds()
	{
		if ( !getShapes().isEmpty() )
		{
			Rectangle2D enclosure = getShapes().get( 0 )
					.getShape().getBounds2D();
			
			for ( JDraftingShape jdshape : getShapes() )
				enclosure = enclosure.createUnion( 
											jdshape.getShape().getBounds2D() );
			
			return enclosure;
		}
		
		return null;
	}
	
	@Override
	public Iterator<JDraftingShape> iterator()
	{
		return getShapes().listIterator();
	}

	private static final long serialVersionUID = -2951147031143369819L;
}
