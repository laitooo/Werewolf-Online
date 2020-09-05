package zxc.laitooo.warewolfonline.main;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GroupActivity;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.dialogs.AddFriendDialog;
import zxc.laitooo.warewolfonline.dialogs.CreateGroupDialog;
import zxc.laitooo.warewolfonline.objects.friend.Friend;
import zxc.laitooo.warewolfonline.objects.friend.FriendsAdapter;
import zxc.laitooo.warewolfonline.objects.game.GamesAdapter;
import zxc.laitooo.warewolfonline.objects.group.Group;
import zxc.laitooo.warewolfonline.objects.group.GroupsAdapter;
import zxc.laitooo.warewolfonline.objects.request.Request;
import zxc.laitooo.warewolfonline.objects.request.RequestsAdapter;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class GroupsListFragment extends Fragment {

    GroupsAdapter adapter;
    ArrayList<Group> list;
    Context c;

    User user;
    CreateGroupDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getContext();
        user = new UserData(c).getUser();
        list = new ArrayList<>();

        MainActivity.mainSocket.emit("joinGroups",user.getId());
        adapter = new GroupsAdapter(c,list);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_groups_list,container,false);

        final RecyclerView recyclerGroups = (RecyclerView)v.findViewById(R.id.recycler_groups);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(c);
        recyclerGroups.setLayoutManager(linearLayoutManager);
        recyclerGroups.setAdapter(adapter);

        c = getContext();

        final TextView info = (TextView)v.findViewById(R.id.info);
        FloatingActionButton add = (FloatingActionButton) v.findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            add.setColorFilter(ContextCompat.getColor(c,R.color.color3),
                    PorterDuff.Mode.SRC_IN);
        }

        info.setText("You have no groups yet");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new CreateGroupDialog(getActivity());
                dialog.show();
            }
        });

        updateView(info);

        try {
            MainActivity.mainSocket.on("loadGroups", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONArray array = (JSONArray) args[0];
                        Log.e("load groups"," " + array.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int i=0;i<array.length();i++){
                                        JSONObject o = array.getJSONObject(i);
                                        list.add(new Group(o.getInt("idGroup"),
                                                o.getString("name"),o.getInt("idAdmin"),
                                                o.getString("nameAdmin"),o.getInt("numMembers")));
                                    }
                                    adapter.notifyDataSetChanged();
                                    updateView(info);
                                    Log.e("load groups","size: " + list.size());
                                }catch (JSONException j){
                                    Log.e("load groups", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("load groups", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("reloadGroups", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {

                        final JSONArray array = (JSONArray) args[0];
                        Log.e("list size","ldd: " + array.length());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    list.clear();
                                    //adapter.notifyDataSetChanged();
                                    for (int i=0;i<array.length();i++){
                                        JSONObject o = array.getJSONObject(i);
                                        list.add(new Group(o.getInt("idGroup"),
                                                o.getString("name"),o.getInt("idAdmin"),
                                                o.getString("nameAdmin"),o.getInt("numMembers")));
                                    }
                                }catch (JSONException j){
                                    Log.e("load groups", "json j: " + j.getMessage());
                                }finally {
                                    adapter.notifyDataSetChanged();
                                    updateView(info);
                                    Log.e("list size","l: " + list.size());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("load groups", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("groupCreated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject o = (JSONObject) args[0];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!o.getBoolean("error")){
                                        ToastUtils.longToast(c,"Group created");
                                        if (dialog != null)
                                            dialog.dismiss();
                                        list.add(new Group(o.getInt("id"),o.getString("name"),
                                                user.getId(),user.getUsername(),1));
                                        adapter.notifyDataSetChanged();
                                        updateView(info);
                                    }else {
                                        if (o.getInt("state") == 1){
                                            ToastUtils.longToast(c,"Name not available");
                                        }
                                    }
                                }catch (JSONException j){
                                    Log.e("group created", "json j: " + j.getMessage());
                                }
                            }
                        });
                    }catch (NullPointerException n) {
                        Log.e("group created", "null n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("addedMember", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("addedMember","id:" + (int)args[0]);
                            if ((int)args[0] == user.getId()){
                                MainActivity.mainSocket.emit("refresh",user.getId());
                            }
                        }
                    });
                }
            });

            MainActivity.mainSocket.on("removedMember", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("removedMember","id:" + (int)args[0]);
                            if ((int)args[0] == user.getId()){
                                MainActivity.mainSocket.emit("refresh",user.getId());
                            }
                        }
                    });
                }
            });

            MainActivity.mainSocket.on("groupDeleted", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainSocket.emit("refresh",user.getId());
                        }
                    });
                }
            });

            MainActivity.mainSocket.on("nameChanged", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject o = (JSONObject) args[0];
                                int id = o.getInt("groupId");
                                for (Group g:list){
                                    if (g.getId() == id){
                                        g.setGroupName(o.getString("newName"));
                                        adapter.notifyItemChanged(list.indexOf(g));
                                    }
                                }
                            }catch (JSONException j){
                                Log.e("nameChanged","json: " + j.getMessage());
                            }
                        }
                    });
                }
            });

        }catch (Exception i){
            Log.e("groups","i: " + i.getMessage());
        }

        return v;
    }

    private void updateView(TextView info) {
        if (list.size() == 0){
            info.setVisibility(View.VISIBLE);
        }else {
            info.setVisibility(View.GONE);
        }
    }

}
