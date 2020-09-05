package zxc.laitooo.warewolfonline.objects.chatmessage;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/29/2020.
 */

public class ChatMessagesHolder extends RecyclerView.ViewHolder {

    ViewGroup layout;
    TextView content;
    TextView date;

    public ChatMessagesHolder(View itemView) {
        super(itemView);

        layout = (ViewGroup) itemView.findViewById(R.id.layout);
        content = (TextView)itemView.findViewById(R.id.content);
        date = (TextView)itemView.findViewById(R.id.date);
    }
}
