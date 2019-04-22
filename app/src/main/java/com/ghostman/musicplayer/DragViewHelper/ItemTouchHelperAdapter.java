package com.ghostman.musicplayer.DragViewHelper;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
