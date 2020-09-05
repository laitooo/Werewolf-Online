package zxc.laitooo.warewolfonline.objects.winner;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class WinnersHolder extends RecyclerView.ViewHolder {

    TextView name,num,line;
    TextView role;
    TextView dead;
    CircleImageView image;

    public WinnersHolder(View itemView) {
        super(itemView);

        name = (TextView)itemView.findViewById(R.id.username);
        line = (TextView)itemView.findViewById(R.id.line);
        num = (TextView)itemView.findViewById(R.id.num);
        role = (TextView)itemView.findViewById(R.id.role);
        image = (CircleImageView)itemView.findViewById(R.id.circle);
        dead = (TextView) itemView.findViewById(R.id.dead);
    }
}
