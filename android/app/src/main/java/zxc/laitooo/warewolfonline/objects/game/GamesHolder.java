package zxc.laitooo.warewolfonline.objects.game;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class GamesHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView info;
    Button join;

    public GamesHolder(View itemView) {
        super(itemView);
        icon = (ImageView)itemView.findViewById(R.id.game_icon);
        info = (TextView)itemView.findViewById(R.id.game_info);
        join = (Button)itemView.findViewById(R.id.join);
    }
}
