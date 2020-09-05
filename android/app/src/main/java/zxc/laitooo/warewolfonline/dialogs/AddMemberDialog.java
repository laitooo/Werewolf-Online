package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GroupActivity;
import zxc.laitooo.warewolfonline.objects.member.Member;
import zxc.laitooo.warewolfonline.objects.member.MembersAdapter;
import zxc.laitooo.warewolfonline.objects.selectfriend.SelectFriend;
import zxc.laitooo.warewolfonline.objects.selectfriend.SelectFriendsAdapter;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 6/5/2020.
 */

public class AddMemberDialog extends Dialog {

    Activity activity;
    RecyclerView recyclerView;
    Button add;
    RequestQueue queue;
    User user;
    int groupId;
    String nameAdmin;

    ArrayList<SelectFriend> list;
    SelectFriendsAdapter adapter;

    ArrayList<Member> members;
    MembersAdapter membersAdapter;
    public int numAdded;

    public AddMemberDialog(Activity activity, int id, String nameAdmin, ArrayList<Member> members,
                           MembersAdapter membersAdapter) {
        super(activity);
        this.activity = activity;
        queue = Volley.newRequestQueue(this.activity);
        user = new UserData(this.activity).getUser();
        groupId = id;
        this.nameAdmin = nameAdmin;
        this.members = members;
        this.membersAdapter = membersAdapter;
        numAdded = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_member);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_add_member);
        add = (Button)findViewById(R.id.add);

        list = new ArrayList<>();
        adapter = new SelectFriendsAdapter(activity,list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //loadFriends(user.getId());
        if (GroupActivity.socket.connected()) {
            GroupActivity.socket.emit("loadFriends", user.getId());
        }else {
            ToastUtils.networkError(activity);
        }

        GroupActivity.socket.on("Friends", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray array = (JSONArray) args[0];
                            for (int i=0;i<array.length();i++){
                                JSONObject o = array.getJSONObject(i);
                                int t = o.getInt("id1");
                                String n = o.getString("name1");
                                if (t == user.getId())
                                    t = o.getInt("id2");
                                if (user.getUsername().equals(n))
                                    n = o.getString("name2");

                                boolean exists = false;
                                for (Member m: members){
                                    if (m.getIdUser() == t){
                                        exists = true;
                                        break;
                                    }
                                }
                                if (!exists) {
                                    list.add(new SelectFriend(o.getInt("id"), t, n, false));
                                }
                            }
                            adapter.notifyDataSetChanged();
                            if (list.size() == 0){
                                dismiss();
                                ToastUtils.longToast(activity,"you have no friends left");
                            }
                        } catch (JSONException e) {
                            Log.e("load members","json e: " + e.getMessage());
                        }
                    }
                });
            }
        });

        GroupActivity.socket.on("addingMembers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                members.clear();
                GroupActivity.socket.emit("loadGroupMembers");
                dismiss();
            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GroupActivity.socket.emit("addMembers",array.toString(),nameAdmin);
                for (SelectFriend f:list){
                    if (f.isSelected()){
                        GroupActivity.socket.emit("addMember",nameAdmin,groupId,f.getUserId(),
                                f.getUserName());
                        numAdded++;
                    }
                }
                add.setEnabled(false);
            }
        });
    }

}
