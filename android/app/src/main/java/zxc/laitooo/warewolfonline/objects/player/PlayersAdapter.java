package zxc.laitooo.warewolfonline.objects.player;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GameActivity;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;

/**
 * Created by Laitooo San on 4/28/2020.
 */

public class PlayersAdapter extends RecyclerView.Adapter<PlayersHolder> {

    ArrayList<Player> list;
    Context c;

    public PlayersAdapter(ArrayList<Player> list, Context c) {
        this.list = list;
        this.c = c;
    }

    @Override
    public PlayersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.player,parent,false);
        return new PlayersHolder(v);
    }

    @Override
    public void onBindViewHolder(PlayersHolder holder, int position) {
        Player player = list.get(position);
        holder.name.setText(player.getName());
        //Log.e("Pic",player.getPicture());
        Picasso.with(c)
                .load(player.getPicture())
                //.load(R.drawable.icon1)
                .transform(new CircleTransformation())
                .into(holder.image);

        if (player.getRole() != 0) {
            if (player.isRevealed()) {
                holder.role.setVisibility(View.VISIBLE);
                Picasso.with(c)
                        .load(Roles.RolesImages[player.getRole()])
                        .into(holder.role);
            }else {
                if (Roles.isWolf(GameActivity.myRole)){
                    if (Roles.isWolf(player.getRole())){
                        holder.role.setVisibility(View.VISIBLE);
                        Picasso.with(c)
                                .load(Roles.RolesImages[player.getRole()])
                                .into(holder.role);
                    }else {
                        holder.role.setVisibility(View.GONE);
                    }
                }else {
                    holder.role.setVisibility(View.GONE);
                }
            }
        }else {
            holder.role.setVisibility(View.GONE);
        }

        if (player.isOnline()){
            holder.online.setImageResource(R.drawable.online);
        }else {
            holder.online.setImageResource(R.drawable.offline);
        }

        if (player.isAlive()){
            holder.dead.setVisibility(View.GONE);
        }else {
            holder.dead.setVisibility(View.VISIBLE);
        }
        player.printPlayer();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
