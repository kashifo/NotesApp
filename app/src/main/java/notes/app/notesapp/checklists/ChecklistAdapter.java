package notes.app.notesapp.checklists;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import notes.app.notesapp.Adapter2Home;
import notes.app.notesapp.R;
import notes.app.notesapp.pojo.Checklist;

/**
 * Created by Kashif on 4/25/2017.
 */

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<Checklist> itemsList;
    private Adapter2Home adapter2Home;

    public ChecklistAdapter(List<Checklist> itemsList, Adapter2Home adapter2Home){
        this.itemsList = itemsList;
        this.adapter2Home = adapter2Home;

        Log.d(TAG, "size=" +itemsList.size() );
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){

        View view = LayoutInflater.
                from( viewGroup.getContext() ).
                inflate(R.layout.checklist_row, viewGroup, false);

        return new MyViewHolder(view);

    }

    @Override
    public int getItemCount(){
        return itemsList.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.textView.setText( itemsList.get(position).getText() );
        holder.checkBox.setChecked(itemsList.get(position).isChecked());
        holder.checkBox.setTag( position );

        if(itemsList.get(position).isChecked()) {
            holder.textView.setPaintFlags( holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox checkBox = (CheckBox) view;
                Checklist item = itemsList.get( Integer.parseInt(checkBox.getTag().toString()) );

                item.setChecked( checkBox.isChecked() );

                Bundle args = new Bundle();
                args.putInt( "position", position );

                if( checkBox.isChecked() ){
                    args.putBoolean( "checked", true );
                    holder.textView.setPaintFlags( holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
                }else{
                    args.putBoolean( "checked", false );
                    holder.textView.setPaintFlags( holder.textView.getPaintFlags() & ( ~ Paint.STRIKE_THRU_TEXT_FLAG) );
                }

                adapter2Home.adapterActionPerformed(args);


            }
        });
    }

}
