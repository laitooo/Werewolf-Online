package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Roles;

/**
 * Created by Laitooo San on 7/7/2020.
 */

public class VoteResultDialog extends Dialog{

    Context c;
    boolean isDraw;
    String name;
    String pic;
    int role;

    public VoteResultDialog(Activity activity) {
        super(activity);
        isDraw = true;
    }

    public VoteResultDialog(Activity activity,String username, String image, int r) {
        super(activity);
        isDraw = false;
        name = username;
        pic = image;
        role = r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (isDraw){
            setContentView(R.layout.fragment_event);
            TextView text = (TextView)findViewById(R.id.text_event);
            ImageView imageView = (ImageView)findViewById(R.id.image_event);

            c = getContext();
            text.setText("Votes are draw");
            Picasso.with(c)
                    .load(R.drawable.error)
                    .into(imageView);
        } else {
            setContentView(R.layout.fragment_event);
            TextView text = (TextView)findViewById(R.id.text_event);
            ImageView imageView = (ImageView)findViewById(R.id.image_event);

            c = getContext();
            Picasso.with(c)
                    .load(pic)
                    .into(imageView);
            text.setText("Villagers Voted to kill " + name + " the " + Roles.RolesNames[role]);
        }
    }

    public void destroy(){
        dismiss();
    }

}
