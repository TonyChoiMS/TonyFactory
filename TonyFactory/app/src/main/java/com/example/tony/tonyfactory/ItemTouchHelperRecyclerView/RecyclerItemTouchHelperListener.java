package com.example.tony.tonyfactory.ItemTouchHelperRecyclerView;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017-11-21.
 */

interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewholder, int direction, int position);
}
