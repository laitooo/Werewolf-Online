package zxc.laitooo.warewolfonline.objects.groupmessage;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class GroupMessagesHolder extends RecyclerView.ViewHolder {

    boolean infoMessage;

    RelativeLayout layout;
    ImageView image;
    TextView content, date, name;
    TextView info;

    public GroupMessagesHolder(View itemView,boolean infoMessage) {
        super(itemView);
        this.infoMessage = infoMessage;
        if (!infoMessage) {
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            image = (ImageView) itemView.findViewById(R.id.userImage);
            name = (TextView) itemView.findViewById(R.id.userName);
            content = (TextView) itemView.findViewById(R.id.content);
            date = (TextView) itemView.findViewById(R.id.date);
        } else {
            info = (TextView) itemView.findViewById(R.id.info);
        }
    }
}
