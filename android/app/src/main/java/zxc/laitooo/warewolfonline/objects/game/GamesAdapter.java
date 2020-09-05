package zxc.laitooo.warewolfonline.objects.game;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GameActivity;
import zxc.laitooo.warewolfonline.main.GamesListFragment;
import zxc.laitooo.warewolfonline.user.AppData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class GamesAdapter extends RecyclerView.Adapter<GamesHolder> {

    public ArrayList<Game> list;
    private Context c;
    private boolean isGoing;

    public GamesAdapter(Context c,ArrayList<Game> games) {
        this.c = c;
        list = games;
        isGoing = false;
    }

    public GamesAdapter(Context c,ArrayList<Game> games,boolean going) {
        this.c = c;
        list = games;
        isGoing = going;
    }
    @Override
    public GamesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(viewType,parent,false);
        return new GamesHolder(v);
    }

    @Override
    public void onBindViewHolder(GamesHolder holder, final int position) {
        final Game game = list.get(position);
        if (isGoing) {
            if (game.getNumPlayers() == game.getMaxPlayers()){
                holder.info.setText("Game started");
            }else {
                holder.info.setText(game.getNumPlayers() + "/" + game.getMaxPlayers() + " joined, by"
                + game.getOwnerName());
            }
        } else {
            holder.info.setText(game.getNumPlayers() + " joined from " + game.getMaxPlayers() + " by "
                    + game.getOwnerName());
        }
        Picasso.with(c)
                .load(R.drawable.logo1)
                .into(holder.icon);

        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGoing) {
                    if (!new AppData(c).isGamesFull()) {
                        Intent intent = new Intent(c, GameActivity.class);
                        intent.putExtra("game_id", game.getId());
                        intent.putExtra("game_owner_id", game.getOwnerId());
                        intent.putExtra("game_max", game.getMaxPlayers());
                        intent.putExtra("game_num", game.getNumPlayers());
                        intent.putExtra("game_owner_name", game.getOwnerName());
                        intent.putExtra("isGameOwner", false);
                        c.startActivity(intent);
                        new AppData(c).addGame(game.getId());
                        GamesListFragment.goingGames.add(game);
                        GamesListFragment.goingAdapter.notifyDataSetChanged();
                        list.remove(position);
                        notifyDataSetChanged();
                        GamesListFragment.updateViews();
                    } else {
                        ToastUtils.longToast(c, "You already joined three games");
                    }
                }else {
                    Intent intent = new Intent(c, GameActivity.class);
                    intent.putExtra("game_id", game.getId());
                    intent.putExtra("game_owner_id", game.getOwnerId());
                    intent.putExtra("game_max", game.getMaxPlayers());
                    intent.putExtra("game_num", game.getNumPlayers());
                    intent.putExtra("game_owner_name", game.getOwnerName());
                    intent.putExtra("isGameOwner", false);
                    c.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isGoing ? R.layout.going_game : R.layout.game;
    }
}