package zxc.laitooo.warewolfonline.objects.message;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/1/2020.
 */

public class MessagesHolder extends RecyclerView.ViewHolder {

    public TextView nickname;
    public TextView message;
    public ImageButton tick;

    public MessagesHolder(View view) {
        super(view);
        nickname = (TextView) view.findViewById(R.id.nickname);
        tick = (ImageButton) view.findViewById(R.id.tick);
        message = (TextView) view.findViewById(R.id.message);
    }
}
