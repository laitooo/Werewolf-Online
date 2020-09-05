package zxc.laitooo.warewolfonline.events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class NoEventsFragment extends Fragment{

    Context c;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event,container,false);
        TextView text = (TextView)v.findViewById(R.id.text_event);
        ImageView imageView = (ImageView)v.findViewById(R.id.image_event);

        c = getContext();
        Picasso.with(c)
                .load(R.drawable.error)
                .into(imageView);
        text.setText("No thing happened today");

        return v;
    }
}
