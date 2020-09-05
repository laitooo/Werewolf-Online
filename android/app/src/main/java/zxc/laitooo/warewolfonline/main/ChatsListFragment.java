package zxc.laitooo.warewolfonline.main;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.dialogs.AddFriendDialog;
import zxc.laitooo.warewolfonline.objects.friend.Friend;
import zxc.laitooo.warewolfonline.objects.friend.FriendsAdapter;
import zxc.laitooo.warewolfonline.objects.request.Request;
import zxc.laitooo.warewolfonline.objects.request.RequestsAdapter;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class ChatsListFragment extends Fragment {

    FriendsAdapter friendsAdapter;
    ArrayList<Friend> friendsList;
    RequestsAdapter requestsAdapter;
    ArrayList<Request> requestsList;
    Context c;

    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getContext();
        user = new UserData(c).getUser();

        friendsList = new ArrayList<>();
        requestsList = new ArrayList<>();
        MainActivity.mainSocket.emit("joinChats",user.getId());

        friendsAdapter = new FriendsAdapter(c,friendsList);
        requestsAdapter = new RequestsAdapter(c,requestsList,MainActivity.mainSocket);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats_list,container,false);

        final RecyclerView recyclerFriends = (RecyclerView)v.findViewById(R.id.recycler_chats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(c);
        recyclerFriends.setLayoutManager(linearLayoutManager);
        recyclerFriends.setAdapter(friendsAdapter);

        c = getContext();

        final TextView info = (TextView)v.findViewById(R.id.info);
        final TextView title1 = (TextView)v.findViewById(R.id.title1);
        final TextView title2 = (TextView)v.findViewById(R.id.title2);
        final RecyclerView recyclerRequests = (RecyclerView)v.findViewById(R.id.recycler_requests);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(c);
        recyclerRequests.setLayoutManager(linearLayoutManager2);
        recyclerRequests.setAdapter(requestsAdapter);

        FloatingActionButton add = (FloatingActionButton) v.findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            add.setColorFilter(ContextCompat.getColor(c,R.color.color3),
                    PorterDuff.Mode.SRC_IN);
        }

        info.setText("You have no friends yet");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendDialog friendDialog = new AddFriendDialog(getActivity());
                friendDialog.show();
            }
        });

        updateView(recyclerRequests,recyclerFriends,title1,title2,info);
        if (getActivity() == null)
            ToastUtils.longToast(c,"Null activity");

        try {
            MainActivity.mainSocket.on("loadRequests", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONArray array = (JSONArray) args[0];
                        Log.e("loadreqests"," " + array.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int i=0;i<array.length();i++){
                                        JSONObject o = array.getJSONObject(i);
                                        requestsList.add(new Request(o.getInt("idSender"),
                                                o.getString("nameSender")));
                                    }
                                    requestsAdapter.notifyDataSetChanged();
                                    updateView(recyclerRequests,recyclerFriends,title1,title2,info);
                                }catch (JSONException j){
                                    Log.e("load requests", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("load requests", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("loadFriends", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONArray array = (JSONArray) args[0];
                        Log.e("loadfirends"," " + array.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int i=0;i<array.length();i++){
                                        JSONObject o = array.getJSONObject(i);
                                        int t = o.getInt("id1");
                                        String n = o.getString("name1");
                                        if (t == user.getId())
                                            t = o.getInt("id2");
                                        if (user.getUsername().equals(n))
                                            n = o.getString("name2");
                                        friendsList.add(new Friend(o.getInt("id"),t,n));
                                    }
                                    friendsAdapter.notifyDataSetChanged();
                                    updateView(recyclerRequests,recyclerFriends,title1,title2,info);
                                }catch (JSONException j){
                                    Log.e("load friends", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("load friends", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("requestSent", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject o = (JSONObject) args[0];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (o.getInt("idReceiver") == user.getId()) {
                                        requestsList.add(new Request(o.getInt("idSender"),
                                                o.getString("nameSender")));

                                        requestsAdapter.notifyDataSetChanged();
                                        updateView(recyclerRequests, recyclerFriends, title1, title2, info);
                                    }
                                }catch (JSONException j){
                                    Log.e("request sent", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("request sent", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("requestAccepted", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject o = (JSONObject) args[0];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (o.getInt("idSender") == user.getId()) {
                                        friendsList.add(new Friend(o.getInt("id"),
                                                o.getInt("idReceiver"),o.getString("nameReceiver")));

                                        friendsAdapter.notifyDataSetChanged();
                                        updateView(recyclerRequests, recyclerFriends, title1, title2, info);
                                    }
                                }catch (JSONException j){
                                    Log.e("request sent", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("request sent", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("accepted", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject jsonObject = (JSONObject) args[0];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!jsonObject.getBoolean("error")){
                                        for (Request request : requestsList){
                                            if (request.getId() == jsonObject.getInt("idSender")){
                                                requestsList.remove(request);
                                                friendsList.add(new Friend(jsonObject.getInt("id"),
                                                        request.getId(),request.getUsername()));
                                            }
                                        }
                                    }else {
                                        ToastUtils.shortToast(c,"Error accepting request");
                                    }

                                    requestsAdapter.notifyDataSetChanged();
                                    friendsAdapter.notifyDataSetChanged();
                                    updateView(recyclerRequests,recyclerFriends,title1,title2,info);
                                }catch (JSONException j){
                                    Log.e("accept request", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("accept request", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("canceled", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject jsonObject = (JSONObject) args[0];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!jsonObject.getBoolean("error")){
                                        for (Request request : requestsList){
                                            if (request.getId() == jsonObject.getInt("id")){
                                                requestsList.remove(request);
                                            }
                                        }
                                    }else {
                                        ToastUtils.shortToast(c,"Error canceling request");
                                    }

                                    requestsAdapter.notifyDataSetChanged();
                                }catch (JSONException j){
                                    Log.e("accept request", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("accept requests", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("deleteChat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject jsonObject = (JSONObject) args[0];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int id1 = jsonObject.getInt("id1");
                                    int id2 = jsonObject.getInt("id2");
                                    for (int i=0;i<friendsList.size();i++){
                                        if (friendsList.get(i).getUserId() == id1 ||
                                                friendsList.get(i).getUserId() == id2){
                                            if (user.getId() == id1 || user.getId() == id2){
                                                friendsList.remove(i);
                                            }
                                        }
                                    }
                                    friendsAdapter.notifyDataSetChanged();
                                }catch (JSONException j){
                                    Log.e("delete chat", "json j: " + j.getMessage());
                                }catch (ConcurrentModificationException c){
                                    Log.e("delete chat", "concurrent c: " + c.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("accept requests", "null n: " + n.getMessage());
                    }
                }
            });
        }catch (Exception i){
            Log.e("chats","i: " + i.getMessage());
        }

        return v;
    }

    public void updateView(RecyclerView rq,RecyclerView rf,TextView t1,TextView t2,TextView in){
        if (requestsList.size() == 0){
            rq.setVisibility(View.GONE);
            t1.setVisibility(View.GONE);
            if (friendsList.size() == 0){
                rf.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);
                in.setVisibility(View.VISIBLE);
            }else {
                rf.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);
                in.setVisibility(View.GONE);
            }
        }else {
            rq.setVisibility(View.VISIBLE);
            t1.setVisibility(View.VISIBLE);
            if (friendsList.size() == 0){
                rf.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);
            }else {
                rf.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);
            }
            in.setVisibility(View.GONE);
        }
    }

}
