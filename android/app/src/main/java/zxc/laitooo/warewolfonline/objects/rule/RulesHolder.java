package zxc.laitooo.warewolfonline.objects.rule;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 7/23/2020.
 */

public class RulesHolder extends RecyclerView.ViewHolder {

    RelativeLayout layout;
    ImageView icon;
    TextView name;

    public RulesHolder(View itemView) {
        super(itemView);
        layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        name = (TextView)itemView.findViewById(R.id.name);
    }
}
