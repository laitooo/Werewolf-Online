package zxc.laitooo.warewolfonline.objects.selectfriend;

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
import zxc.laitooo.warewolfonline.objects.friend.Friend;
import zxc.laitooo.warewolfonline.objects.friend.FriendsHolder;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;

/**
 * Created by Laitooo San on 6/5/2020.
 */

public class SelectFriendsAdapter  extends RecyclerView.Adapter<SelectFriendsHolder> {

    public ArrayList<SelectFriend> list;
    private Context c;

    public SelectFriendsAdapter(Context c, ArrayList<SelectFriend> friends) {
        this.c = c;
        list = friends;
    }

    @Override
    public SelectFriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.select_friend,parent,false);
        return new SelectFriendsHolder(v);
    }

    @Override
    public void onBindViewHolder(final SelectFriendsHolder holder, int position) {
        final SelectFriend friend = list.get(position);
        holder.name.setText(friend.getUserName());
        Picasso.with(c)
                .load(friend.getPicture())
                .fit()
                .transform(new CircleTransformation())
                .into(holder.icon);

        if (friend.isSelected()){
            holder.selected.setVisibility(View.VISIBLE);
        }else {
            holder.selected.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend.setSelected(!friend.isSelected());
                holder.selected.setVisibility(friend.isSelected() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
