package zxc.laitooo.warewolfonline.objects.vote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/12/2020.
 */

public class VotesAdapter extends RecyclerView.Adapter<VotesHolder> {

    private Context c;
    private ArrayList<Vote> list;
    public int selectedOrder,selectedId,selectedRole;
    public String selectedName;
    private boolean isWarewolf;

    public VotesAdapter(Context c, ArrayList<Vote> list, boolean isWarewolf) {
        this.c = c;
        this.list = list;
        this.selectedOrder = -1;
        this.selectedId = -1;
        this.selectedRole = -1;
        this.selectedName = "";
        this.isWarewolf = isWarewolf;
    }

    @Override
    public VotesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote,parent,false);
        return new VotesHolder(v);
    }

    @Override
    public void onBindViewHolder(final VotesHolder holder, final int position) {
        final Vote vote = list.get(position);
        holder.name.setText(vote.getName());
        if (vote.isSelected()){
            holder.image.setBorderWidth(3);
        }else {
            holder.image.setBorderWidth(0);
        }

        Picasso.with(c)
                .load(vote.getPic())
                .into(holder.image);

        if (isWarewolf){
            if (vote.isWolf()){
                holder.isWolf.setVisibility(View.VISIBLE);
            }else {
                //Log.e("ALPHA","role" + vote.getRole());
                holder.isWolf.setVisibility(View.GONE);
            }
        }else {
            holder.isWolf.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWarewolf){
                    if (!vote.isWolf()) {
                        if (!vote.isSelected()) {
                            for (Vote vote : list) {
                                vote.setSelected(false);
                            }
                            selectedOrder = vote.getOrder();
                            selectedId = vote.getId();
                            selectedName = vote.getName();
                            selectedRole = vote.getRole();
                            holder.image.setBorderWidth(3);
                            vote.setSelected(true);
                        }
                        notifyDataSetChanged();
                    }

                }else {
                    if (!vote.isSelected()) {
                        for (Vote vote : list) {
                            vote.setSelected(false);
                        }
                        selectedOrder = vote.getOrder();
                        selectedId = vote.getId();
                        selectedName = vote.getName();
                        selectedRole = vote.getRole();
                        holder.image.setBorderWidth(3);
                        vote.setSelected(true);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void deselectAll(){
        for (Vote v:list){
            v.setSelected(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
