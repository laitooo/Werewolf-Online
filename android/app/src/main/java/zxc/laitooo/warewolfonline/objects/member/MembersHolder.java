package zxc.laitooo.warewolfonline.objects.member;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class MembersHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView name,admin;
    RelativeLayout layout;

    public MembersHolder(View itemView) {
        super(itemView);
        icon = (ImageView)itemView.findViewById(R.id.member_pic);
        name = (TextView)itemView.findViewById(R.id.member_name);
        admin = (TextView)itemView.findViewById(R.id.isAdmin);
        layout = (RelativeLayout)itemView.findViewById(R.id.layout);
    }
}
