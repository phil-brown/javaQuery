/*
 * Copyright 2013 Phil Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package self.philbrown.javaQuery;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Detect Swipes on a per-container basis.
 * @author Phil Brown
 */
public class SwipeDetector implements MouseListener, MouseMotionListener
{
	/** Swipe Direction */
	public static enum Direction
	{
		RIGHT, DOWN, UP, LEFT, START, STOP
	}
	
	/**
	 * The minimum distance a finger must travel in order to register a swipe event.
	 */
	private int minSwipeDistance = 100;
	
	/** Maintains a reference to the first detected down touch event. */
    private float downX, downY;
    
    /** Maintains a reference to the first detected up touch event. */
    private float upX, upY;
    
    /** The View on which the swipe action is registered */
    private Component view;
    
    /**
     * provides callbacks to a listener class for various swipe gestures.
     */
    private SwipeListener listener;
    
    /**
     * Constructor
     * @param view
     * @param listener
     */
    public SwipeDetector(Component view, SwipeListener listener)
    {
    	this.listener = listener;
    	this.view = view;
    	double resolution = Math.sqrt(view.getWidth()*2 + view.getHeight()*2);
    	minSwipeDistance = (int) (resolution*16+0.5f);
    }

	@Override
	public void mouseDragged(MouseEvent event) {
		upX = event.getX();
		upY = event.getY();

		float deltaX = downX - upX;
		float deltaY = downY - upY;

		// swipe horizontal?
		if(Math.abs(deltaX) > minSwipeDistance)
		{
			// left or right
			if (deltaX < 0) 
			{ 
				if (listener != null)
				{
					listener.onRightSwipe(view);
				}
			}
			else if (deltaX > 0) 
			{ 
				if (listener != null)
				{
					listener.onLeftSwipe(view);
				}
			}
		}

		// swipe vertical?
		else if(Math.abs(deltaY) > minSwipeDistance)
		{
			// top or down
			if (deltaY < 0) 
			{ 
				if (listener != null)
				{
					listener.onDownSwipe(view);
				} 
			}
			else if (deltaY > 0) 
			{ 
				if (listener != null)
				{
					listener.onUpSwipe(view);
				} 
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent event) {}

	@Override
	public void mouseClicked(MouseEvent event) {}

	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	@Override
	public void mousePressed(MouseEvent event) {
		downX = event.getX();
		downY = event.getY();
		if (listener != null)
		{
			listener.onStartSwipe(view);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		upX = event.getX();
		upY = event.getY();
	
		float deltaX = downX - upX;
		float deltaY = downY - upY;
	
		// swipe horizontal?
		if(Math.abs(deltaX) > minSwipeDistance)
		{
			// left or right
			if (deltaX < 0) 
			{ 
				if (listener != null)
				{
					listener.onRightSwipe(view);
				}
			}
			else if (deltaX > 0) 
			{ 
				if (listener != null)
				{
					listener.onLeftSwipe(view);
				}
			}
		}
	
		// swipe vertical?
		else if(Math.abs(deltaY) > minSwipeDistance)
		{
			// top or down
			if (deltaY < 0) 
			{ 
				if (listener != null)
				{
					listener.onDownSwipe(view);
				} 
			}
			else if (deltaY > 0) 
			{ 
				if (listener != null)
				{
					listener.onUpSwipe(view);
				} 
			}
		}
		if (listener != null)
      	{
      		listener.onStopSwipe(view);
      	}
	}
    
    /**
     * Simulate a swipe up event
     */
    public void performSwipeUp()
    {
    	if (listener != null)
    		listener.onUpSwipe(view);
    }
    
    /**
     * Simulate a swipe right event
     */
    public void performSwipeRight()
    {
    	if (listener != null)
    		listener.onRightSwipe(view);
    }
    
    /**
     * Simulate a swipe left event
     */
    public void performSwipeLeft()
    {
    	if (listener != null)
    		listener.onLeftSwipe(view);
    }
    
    /**
     * Simulate a swipe down event
     */
    public void performSwipeDown()
    {
    	if (listener != null)
    		listener.onDownSwipe(view);
    }
	
	/**
	 * Provides callbacks to a registered listener for swipe events in {@link SwipeDetector}
	 * @author Phil Brown
	 */
	public interface SwipeListener
	{
		/** Callback for registering a new swipe motion from the bottom of the view toward its top. */
		public void onUpSwipe(Component v);
		/** Callback for registering a new swipe motion from the left of the view toward its right. */
	    public void onRightSwipe(Component v);
	    /** Callback for registering a new swipe motion from the right of the view toward its left. */
	    public void onLeftSwipe(Component v);
	    /** Callback for registering a new swipe motion from the top of the view toward its bottom. */
	    public void onDownSwipe(Component v);
	    /** Callback for registering that a new swipe motion has begun */
	    public void onStartSwipe(Component v);
	    /** Callback for registering that a swipe motion has ended */
	    public void onStopSwipe(Component v);
	}
}
