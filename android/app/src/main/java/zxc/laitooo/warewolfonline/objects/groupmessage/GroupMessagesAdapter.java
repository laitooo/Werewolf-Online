package zxc.laitooo.warewolfonline.objects.groupmessage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.objects.chatmessage.ChatMessage;
import zxc.laitooo.warewolfonline.objects.chatmessage.ChatMessagesHolder;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.TimeUtils;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class GroupMessagesAdapter  extends RecyclerView.Adapter<GroupMessagesHolder> {

    Context c;
    ArrayList<GroupMessage> list;
    User user;

    public GroupMessagesAdapter(Context c, ArrayList<GroupMessage> list) {
        this.c = c;
        this.list = list;
        user = new UserData(c).getUser();
    }

    @Override
    public GroupMessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        return new GroupMessagesHolder(v,viewType == R.layout.info_message);
    }

    @Override
    public void onBindViewHolder(GroupMessagesHolder holder, int position) {
        if (!holder.infoMessage) {
            GroupMessage message = list.get(position);
            holder.content.setText(message.getContent());
            holder.content.setMaxLines(100);
            holder.date.setText(TimeUtils.getDisplayedText(message.getTime()));
            if (message.getUserId() != user.getId()) {
                Picasso.with(c)
                        .load(message.getUserPicture())
                        .transform(new CircleTransformation())
                        .into(holder.image);
                //Log.e("pic", message.getUserPicture());
                holder.name.setText(message.getUserName());
            }
        }else {
            holder.info.setText(list.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isInfoMessage())
            return R.layout.info_message;
        if (list.get(position).getUserId() == user.getId())
            return R.layout.my_group_chat;
        else
            return R.layout.his_group_chat;
    }
}
