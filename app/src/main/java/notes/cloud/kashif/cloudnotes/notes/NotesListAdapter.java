package notes.cloud.kashif.cloudnotes.notes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import notes.cloud.kashif.cloudnotes.Adapter2Home;
import notes.cloud.kashif.cloudnotes.helpers.ItemTouchHelperClass;
import notes.cloud.kashif.cloudnotes.R;
import notes.cloud.kashif.cloudnotes.RowAction;
import notes.cloud.kashif.cloudnotes.pojo.Note;

/*
RecyclerView adapter for viewing notes list
 */
public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.MyViewHolder>
        implements ItemTouchHelperClass.ItemTouchHelperAdapter {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private ArrayList<Note> notesList;
    private Adapter2Home adapter2Home;

    public NotesListAdapter(Activity context, ArrayList<Note> notesList, Adapter2Home adapter2Home) {
        this.context = context;
        this.notesList = notesList;
        this.adapter2Home = adapter2Home;

        Log.d(TAG, "size=" +notesList.size() );
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(notesList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(notesList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemRemoved(int position) {

        try {
            Log.d("onItemRemoved", "pos=" + position +"onItemRemoved size=" + notesList.size() );

            Bundle args = new Bundle();
            args.putString( "action", RowAction.DELETE.toString() );
            args.putInt( "noteId", notesList.get(position).getId() );
            args.putInt( "position", position );

            adapter2Home.adapterActionPerformed( args );

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.notes_list_row, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView tvTitle;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            linearLayout = (LinearLayout) itemLayoutView.findViewById(R.id.linearLayout);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvTitle);

        }
    }


    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {

        viewHolder.tvTitle.setText( notesList.get(position).getTitle() );

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Bundle args = new Bundle();
                args.putString( "action", RowAction.CLICK.toString() );
                args.putInt( "noteId", notesList.get(position).getId() );
                args.putInt( "position", position );
                args.putInt( "noteType", notesList.get(position).getType() );

                adapter2Home.adapterActionPerformed( args );

            }
        });

    }


}//end
