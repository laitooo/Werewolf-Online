package zxc.laitooo.warewolfonline.objects.message;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/1/2020.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesHolder> {

    private ArrayList<Message> list;
    private Context c;

    public MessagesAdapter(Context c,ArrayList<Message> messages) {
        this.c = c;
        this.list = messages;
    }

    @Override
    public MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
        return new MessagesHolder(v);
    }

    @Override
    public void onBindViewHolder(MessagesHolder holder, int position) {
        Message m = list.get(position);


        if (m.isFromServer()){
            holder.tick.setVisibility(View.GONE);
            holder.message.setText(m.getText());

            if (m.getState() == 1) {
                holder.message.setTextColor(ContextCompat.getColor(c, R.color.red));
            }else if(m.getState() == 2) {
                holder.message.setTextColor(ContextCompat.getColor(c, R.color.color1));
            }else {
                holder.message.setTextColor(ContextCompat.getColor(c, R.color.red));
            }
            holder.nickname.setVisibility(View.GONE);
        }else {
            if (!m.isFromYou()){
                holder.tick.setVisibility(View.GONE);
                holder.nickname.setText(m.getUserName() + " : " );
            }else {
                holder.tick.setVisibility(View.VISIBLE);
                switch (m.getState()){
                    case Message.STATE_SENT:
                        holder.tick.setImageResource(R.drawable.icon_sent);
                        break;
                    case Message.STATE_ERROR:
                        holder.tick.setImageResource(R.drawable.icon_not_sent);
                        break;
                    case Message.STATE_SENDING:
                        holder.tick.setImageResource(R.drawable.icon_wait);
                        break;
                    default:
                        holder.tick.setVisibility(View.GONE);
                        break;
                }
                holder.nickname.setText("you : " );
            }
            holder.nickname.setVisibility(View.VISIBLE);
            //holder.message.setTextColor(ContextCompat.getColor(c,R.color.color3));
            holder.message.setText(m.getText() );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
