package zxc.laitooo.warewolfonline.objects.friend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.ChatActivity;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsHolder> {

    public ArrayList<Friend> list;
    private Context c;

    public FriendsAdapter(Context c, ArrayList<Friend> friends) {
        this.c = c;
        list = friends;
    }

    @Override
    public FriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.friend,parent,false);
        return new FriendsHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendsHolder holder, int position) {
        final Friend friend = list.get(position);
        holder.name.setText(friend.getUserName());
        Picasso.with(c)
                .load(friend.getPicture())
                .fit()
                .transform(new CircleTransformation())
                .into(holder.icon);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ChatActivity.class);
                intent.putExtra("username", friend.getUserName());
                intent.putExtra("userid", friend.getUserId());
                intent.putExtra("chatid", friend.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
