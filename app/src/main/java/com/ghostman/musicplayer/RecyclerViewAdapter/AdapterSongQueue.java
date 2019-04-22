package com.ghostman.musicplayer.RecyclerViewAdapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghostman.musicplayer.DragViewHelper.ItemTouchHelperAdapter;
import com.ghostman.musicplayer.DragViewHelper.ItemTouchHelperViewHolder;
import com.ghostman.musicplayer.DragViewHelper.OnStartDragListener;
import com.ghostman.musicplayer.Fragments.dummy.DummyContent;
import com.ghostman.musicplayer.R;

import java.util.Collections;
import java.util.List;

public class AdapterSongQueue extends RecyclerView.Adapter<AdapterSongQueue.ViewHolder>
        implements ItemTouchHelperAdapter {

    private final Context mcontext;
    private final List<DummyContent.DummyItem> mValues;
    private final OnStartDragListener mOnStartDragListener;

    public AdapterSongQueue(Context context, List<DummyContent.DummyItem> items, OnStartDragListener onStartDragListener) {
        //super();
        mcontext = context;
        mValues = items;
        mOnStartDragListener = onStartDragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_song_queue, parent, false);
        return new AdapterSongQueue.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("Song Name " + mValues.get(position).id);
        holder.mArtistName.setText("Artist Name " + mValues.get(position).content);
        holder.mDragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mArtistName;
        final ImageView mDragger;
        DummyContent.DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.tvSongName_listSongQueue);
            mArtistName = view.findViewById(R.id.tvArtistName_listSongQueue);
            mDragger = view.findViewById(R.id.iv_drager_listSongQueue);
        }

        @Override
        public void onItemSelected() {
            mView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorBackground));
        }

        @Override
        public void onItemClear() {
            mView.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onItemDismiss(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition) {
            for(int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mValues, i, i + 1);
            }
        } else {
            for(int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mValues, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }
}
