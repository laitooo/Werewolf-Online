package zxc.laitooo.warewolfonline.objects.group;

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
import zxc.laitooo.warewolfonline.activities.GroupActivity;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsHolder>{

    private ArrayList<Group> list;
    private Context c;

    public GroupsAdapter(Context c,ArrayList<Group> groups) {
        this.c = c;
        list = groups;
    }

    @Override
    public GroupsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.group,parent,false);
        return new GroupsHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupsHolder holder, int position) {
        final Group group = list.get(position);
        holder.name.setText(group.getGroupName());
        Picasso.with(c)
                .load(R.drawable.logo2)
                .into(holder.icon);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, GroupActivity.class);
                intent.putExtra("id", group.getId());
                intent.putExtra("groupName", group.getGroupName());
                intent.putExtra("idAdmin", group.getAdminId());
                intent.putExtra("nameAdmin", group.getAdminName());
                intent.putExtra("numMembers", group.getNumMembers());
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
