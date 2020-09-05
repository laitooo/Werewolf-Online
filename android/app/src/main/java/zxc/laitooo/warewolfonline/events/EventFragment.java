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

public class EventFragment extends Fragment {

    Event event;
    Context c;

    public static EventFragment newInstance(Event e) {

        Bundle args = new Bundle();
        args.putString("text",e.getText());
        args.putBoolean("show",e.isShowName());
        args.putString("image",e.getImage());
        args.putString("name",e.getUsername());
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        event = new Event(b.getString("text"),b.getString("image"),b.getString("name"),
                b.getBoolean("show"));
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
                .load(event.getImage())
                .into(imageView);
        text.setText(event.getUsername() + " The " + event.getText());

        return v;
    }
}
