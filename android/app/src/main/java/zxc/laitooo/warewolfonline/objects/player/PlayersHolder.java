package zxc.laitooo.warewolfonline.objects.player;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 4/28/2020.
 */

public class PlayersHolder extends RecyclerView.ViewHolder {

    TextView name;
    ImageView image;
    ImageView role;
    ImageView online;
    ImageView dead;

    public PlayersHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.name_player);
        image = (ImageView)itemView.findViewById(R.id.image_player);
        role = (ImageView)itemView.findViewById(R.id.role);
        online = (ImageView)itemView.findViewById(R.id.online);
        dead = (ImageView)itemView.findViewById(R.id.dead);
    }
}
