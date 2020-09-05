package zxc.laitooo.warewolfonline.objects.winner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Roles;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class WinnersAdapter extends RecyclerView.Adapter<WinnersHolder> {

    Context c;
    ArrayList<Winner> list;

    public WinnersAdapter(Context c, ArrayList<Winner> list) {
        this.c = c;
        this.list = list;
    }

    @Override
    public WinnersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.winner,parent,false);
        return new WinnersHolder(v);
    }

    @Override
    public void onBindViewHolder(WinnersHolder holder, int position) {
        Winner winner = list.get(position);
        holder.num.setText((position +1) + "");
        holder.name.setText(winner.getUsername());
        holder.role.setText(Roles.RolesNames[winner.getRole()]);
        if (winner.isWinner()){
            holder.image.setBorderWidth(2);
        }else {
            holder.image.setBorderWidth(0);
        }

        if (winner.isAlive()){
            holder.dead.setVisibility(View.GONE);
        }

        Picasso.with(c)
                .load(winner.getPicture())
                .into(holder.image);
        if (position == list.size() -1){
            holder.line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
