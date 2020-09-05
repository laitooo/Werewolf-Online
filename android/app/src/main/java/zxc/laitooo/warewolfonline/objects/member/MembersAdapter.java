package zxc.laitooo.warewolfonline.objects.member;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.ChatActivity;
import zxc.laitooo.warewolfonline.activities.GroupActivity;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.objects.friend.Friend;
import zxc.laitooo.warewolfonline.objects.friend.FriendsHolder;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class MembersAdapter extends RecyclerView.Adapter<MembersHolder> {

    public ArrayList<Member> list;
    private Context c;
    private User user;
    private boolean iamAdmin;

    public MembersAdapter(Context c, ArrayList<Member> members, boolean iamAdmin) {
        this.c = c;
        list = members;
        user = new UserData(c).getUser();
        this.iamAdmin = iamAdmin;
    }

    @Override
    public MembersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.member,parent,false);
        return new MembersHolder(v);
    }

    @Override
    public void onBindViewHolder(final MembersHolder holder, int position) {
        final Member member = list.get(position);
        holder.name.setText(member.getNameUser());
        holder.admin.setText(member.isAdmin()? "Admin" : "");
        Picasso.with(c)
                .load(member.getPicture())
                .fit()
                .transform(new CircleTransformation())
                .into(holder.icon);
        if (iamAdmin && member.getIdUser() != user.getId()) {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(c, holder.name);

                    popup.getMenuInflater()
                            .inflate(R.menu.admin_member, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.remove) {
                                GroupActivity.socket.emit("removeMember", user.getUsername(),
                                        member.getIdUser(), member.getNameUser());
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
