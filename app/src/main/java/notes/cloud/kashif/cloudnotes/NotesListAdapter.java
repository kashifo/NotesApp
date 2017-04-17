package notes.cloud.kashif.cloudnotes;

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
/*
RecyclerView adapter for viewing notes list
 */
public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.MyViewHolder>
        implements ItemTouchHelperClass.ItemTouchHelperAdapter {

    final String TAG = getClass().getSimpleName();
    Context context;
    ArrayList<Note> notesList;
    Adapter2Home adapter2Home;

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
            args.putSerializable( "note", notesList.get(position) );
            args.putInt( "position", position );
            args.putString( "action", RowAction.DELETE.toString() );

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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout linearLayout;
        public TextView tvTitle;

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
                args.putSerializable( "note", notesList.get(position) );
                args.putInt( "position", position );
                args.putString( "action", RowAction.CLICK.toString() );

                adapter2Home.adapterActionPerformed( args );

            }
        });

    }


    public String get12hrTime(int paramHour, int paramMinute) {

        int newHour = 0;
        String newMeridian = "";

        if (paramHour == 0) {
            newHour = 12;
            newMeridian = "AM";
        } else if (paramHour > 0 && paramHour < 12) {
            newHour = paramHour;
            newMeridian = "AM";
        } else if (paramHour == 12) {
            newHour = paramHour;
            newMeridian = "PM";
        } else if (paramHour > 12) {
            newHour = paramHour - 12;
            newMeridian = "PM";
        }

        return newHour + ":" + paramMinute + " " + newMeridian;
    }

}//end
