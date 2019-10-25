package ello.adapters.action;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ello.R;
import ello.helpers.Reasons;

/**
 * Created by ranaasad on 16/05/2019.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.Holder> {

    private Context context;
    ArrayList<Reasons> arrayList;
    AdapterCallback callback;
    private int checked=-1;
    public ActionAdapter(Context context, ArrayList<Reasons> arrayList, AdapterCallback callback) {
        this.context = context;
        this.arrayList=arrayList;
        this.callback=callback;


    }

    @Override
    public ActionAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_layout, parent, false);

        return new ActionAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(final ActionAdapter.Holder holder, int position) {
        String value=arrayList.get(position).getReasonMessage();
        if(checked!=-1){
            if (checked==position){
                holder.tickIcon.setVisibility(View.VISIBLE);
            }else{
                holder.tickIcon.setVisibility(View.GONE);
            }
        }
        holder.title.setText(value);
    }


    @Override
    public int getItemCount() {
        return  arrayList.size();

    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView tickIcon;

        public Holder(final View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.description);
            tickIcon=itemView.findViewById(R.id.tick);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checked=getAdapterPosition();
                    callback.onClick(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

        }

    }
}

