package zxc.laitooo.warewolfonline.objects.request;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/22/2020.
 */

public class RequestsHolder extends RecyclerView.ViewHolder {

    TextView name;
    ImageView image;
    ImageButton accept,cancel;

    public RequestsHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.name);
        image = (ImageView)itemView.findViewById(R.id.image);
        accept = (ImageButton) itemView.findViewById(R.id.accept);
        cancel = (ImageButton) itemView.findViewById(R.id.cancel);
    }
}
