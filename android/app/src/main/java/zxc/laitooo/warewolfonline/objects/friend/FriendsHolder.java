package zxc.laitooo.warewolfonline.objects.friend;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class FriendsHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView name;
    RelativeLayout layout;

    public FriendsHolder(View itemView) {
        super(itemView);
        icon = (ImageView)itemView.findViewById(R.id.chat_icon);
        name = (TextView)itemView.findViewById(R.id.chat_name);
        layout = (RelativeLayout)itemView.findViewById(R.id.layout);
    }
}
