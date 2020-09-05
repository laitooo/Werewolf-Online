package zxc.laitooo.warewolfonline.objects.vote;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/12/2020.
 */

public class VotesHolder extends RecyclerView.ViewHolder{

    CircleImageView image;
    ImageView isWolf;
    TextView name;

    public VotesHolder(View itemView) {
        super(itemView);
        image = (CircleImageView)itemView.findViewById(R.id.image_event);
        isWolf = (ImageView) itemView.findViewById(R.id.isWolf);
        name = (TextView)itemView.findViewById(R.id.name);
    }
}
