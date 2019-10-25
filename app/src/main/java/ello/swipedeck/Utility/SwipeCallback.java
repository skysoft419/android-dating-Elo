package ello.swipedeck.Utility;

import android.view.View;

import ello.swipedeck.CardContainer;

/**
 * Created by trioangle on 10/08/2016.
 */
public interface SwipeCallback {
    void cardSwipedLeft(View card);

    void cardSwipedRight(View card);

    void cardOffScreen(CardContainer cardContainer);

    void cardActionDown();

    void cardActionUp();

    void cardSwipedTop(View card);

    /**
     * Check whether we can start dragging current view.
     *
     * @return true if we can start dragging view, false otherwise
     */
    boolean isDragEnabled();
}
