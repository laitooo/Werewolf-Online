package zxc.laitooo.warewolfonline.objects.request;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.socket.client.Socket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;

/**
 * Created by Laitooo San on 5/22/2020.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsHolder> {

    Context c;
    ArrayList<Request> list;
    Socket socket;
    User user;

    public RequestsAdapter(Context c, ArrayList<Request> list,Socket socket) {
        this.c = c;
        this.list = list;
        this.socket = socket;
        user = new UserData(c).getUser();
    }

    @Override
    public RequestsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request,parent,false);
        return new RequestsHolder(v);
    }

    @Override
    public void onBindViewHolder(RequestsHolder holder, int position) {
        final Request r = list.get(position);
        holder.name.setText(r.getUsername());
        Picasso.with(c)
                .load(r.getPicture())
                .transform(new CircleTransformation())
                .into(holder.image);

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("acceptRequest",r.getId(),user.getId(),r.getUsername()
                        ,user.getUsername());
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("cancelRequest",r.getId(),user.getId(),r.getUsername()
                        ,user.getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
