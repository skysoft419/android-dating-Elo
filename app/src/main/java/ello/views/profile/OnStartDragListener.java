package ello.views.profile;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ranaasad on 31/05/2019.
 */
interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
