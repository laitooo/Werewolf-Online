package zxc.laitooo.warewolfonline.objects.chatmessage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.utils.TimeUtils;

/**
 * Created by Laitooo San on 5/29/2020.
 */

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesHolder> {

    Context c;
    ArrayList<ChatMessage> list;

    public ChatMessagesAdapter(Context c, ArrayList<ChatMessage> list) {
        this.c = c;
        this.list = list;
    }

    @Override
    public ChatMessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        return new ChatMessagesHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatMessagesHolder holder, int position) {
        ChatMessage chatMessage = list.get(position);
        holder.content.setText(chatMessage.getContent());
        holder.date.setText(TimeUtils.getDisplayedText(chatMessage.getDate()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isFromMe())
            return R.layout.my_chat;
        else
            return R.layout.his_chat;
    }
}
