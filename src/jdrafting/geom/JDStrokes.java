package jdrafting.geom;

import java.awt.BasicStroke;
import java.util.Arrays;

public enum JDStrokes
{
	PLAIN_ROUND {
        @Override
        public BasicStroke getStroke()
        {
            return new BasicStroke( 1f, 
            						BasicStroke.CAP_ROUND, 
            						BasicStroke.JOIN_ROUND );
        }
    },
	PLAIN_SQUARE {
        @Override
        public BasicStroke getStroke()
        {
            return new BasicStroke( 1f, 
            						BasicStroke.CAP_BUTT, 
            						BasicStroke.JOIN_MITER );
        }
    },
    DOTTED {
        @Override
        public BasicStroke getStroke()
        {
            return new BasicStroke( 1f, 
            						BasicStroke.CAP_BUTT, 
            						BasicStroke.JOIN_BEVEL, 
            						1f,
            						new float[] { 2f, 2f },
            						0f );
        }
    },
    MIXED {
        @Override
        public BasicStroke getStroke()
        {
            return new BasicStroke( 1f, 
            						BasicStroke.CAP_ROUND, 
            						BasicStroke.JOIN_BEVEL, 
            						1f,
            						new float[] { 8f, 7f, 2f, 7f },
            						0f );
        }
    },
    DASHED {
        @Override
        public BasicStroke getStroke() {
            return new BasicStroke( 1f, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_BEVEL, 
					1f,
					new float[] { 8f, 4f },
					0f );
        }

    };

	public abstract BasicStroke getStroke();

	public static BasicStroke getStroke( BasicStroke stroke, 
										 final float linewidh )
	{
		if ( stroke.getDashArray() != null )
		{
			float[] dashArray = new float[ stroke.getDashArray().length ];
			for ( int i = 0; i < stroke.getDashArray().length; i++ )
				dashArray[i] = (float)
						( stroke.getDashArray()[i] * Math.sqrt( linewidh ) );
			return new BasicStroke( linewidh,
									stroke.getEndCap(), 
									stroke.getLineJoin(),
									stroke.getMiterLimit() ,
									dashArray,
									stroke.getDashPhase() );
		}

		return new BasicStroke( linewidh, 
								stroke.getEndCap(), 
								stroke.getLineJoin() );
	}
	
	/**
	 * Copy stroke with a different line width
	 * @param linewidth shape linewidth
	 * @param stroke original stroke
	 * @return new stroke identical to original except for line width
	 */
	public static BasicStroke cloneStrokeStyle( float linewidth, 
												BasicStroke stroke )
	{
		return new BasicStroke( linewidth, 
								stroke.getEndCap(), 
								stroke.getLineJoin(), 
								stroke.getMiterLimit(),
								stroke.getDashArray() == null
								? null
								: Arrays.copyOf( stroke.getDashArray(), 
												 stroke.getDashArray().length ), 
								stroke.getDashPhase() );
	}
}
