package com.ciaston.przemek.todoapp.utils;

/**
 * Created by Przemek on 2018-01-23.
 */

public interface ItemTouchHelperViewHolder {
    /**
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * state should be cleared.
     */
    void onItemClear();
}
