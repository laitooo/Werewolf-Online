package zxc.laitooo.warewolfonline.objects.group;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class GroupsHolder extends RecyclerView.ViewHolder {

    RelativeLayout layout;
    ImageView icon;
    TextView name;

    public GroupsHolder(View itemView) {
        super(itemView);
        icon = (ImageView)itemView.findViewById(R.id.group_icon);
        name = (TextView)itemView.findViewById(R.id.group_name);
        layout = (RelativeLayout)itemView.findViewById(R.id.layout);
    }

}
