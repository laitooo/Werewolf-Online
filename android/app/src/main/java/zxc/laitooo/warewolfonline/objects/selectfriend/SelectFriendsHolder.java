package zxc.laitooo.warewolfonline.objects.selectfriend;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 6/5/2020.
 */

public class SelectFriendsHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    ImageView selected;
    TextView name;
    RelativeLayout layout;

    public SelectFriendsHolder(View itemView) {
        super(itemView);
        icon = (ImageView)itemView.findViewById(R.id.chat_icon);
        selected = (ImageView)itemView.findViewById(R.id.selected);
        name = (TextView)itemView.findViewById(R.id.chat_name);
        layout = (RelativeLayout)itemView.findViewById(R.id.layout);
    }
}
