package zxc.laitooo.warewolfonline.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.emitter.Emitter;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.dialogs.AddFriendDialog;
import zxc.laitooo.warewolfonline.dialogs.AddMemberDialog;
import zxc.laitooo.warewolfonline.dialogs.ChangeGroupNameDialog;
import zxc.laitooo.warewolfonline.objects.group.Group;
import zxc.laitooo.warewolfonline.objects.member.Member;
import zxc.laitooo.warewolfonline.objects.member.MembersAdapter;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 2/28/2020.
 */

public class GroupMembersActivity extends AppCompatActivity{

    ArrayList<Member> list;
    MembersAdapter adapter;
    Context c;
    User user;
    //RequestQueue queue;
    int id,idAdmin;
    String name,nameAdmin;
    AddMemberDialog dialog;
    boolean iamAdmin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        ImageButton exit = (ImageButton)findViewById(R.id.back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton rename = (ImageButton)findViewById(R.id.rename);
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGroupNameDialog dialog = new ChangeGroupNameDialog(GroupMembersActivity.this,
                        GroupActivity.socket);
                dialog.show();
            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        nameAdmin = intent.getStringExtra("nameAdmin");
        id = intent.getIntExtra("id",0);
        idAdmin = intent.getIntExtra("idAdmin",0);
        user = new UserData(this).getUser();
        iamAdmin = user.getId() == idAdmin;
        //Log.e("admin","id: " + user.getId() + " admin: " + idAdmin);

        final TextView title = (TextView)findViewById(R.id.name);
        title.setText(name);



        list = new ArrayList<>();
        adapter = new MembersAdapter(this,list,iamAdmin);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_members);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        c = this;

        final ImageButton add = (ImageButton)findViewById(R.id.add);
        if (!iamAdmin){
            add.setVisibility(View.GONE);
            add.setClickable(false);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(c, add);

                popup.getMenuInflater()
                        .inflate(iamAdmin? R.menu.admin : R.menu.member, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.add) {
                            addMember();
                        }else if (item.getItemId() == R.id.leave){
                            GroupActivity.socket.emit("leaveGroup",user.getId(), user.getUsername()
                                    , iamAdmin);
                        }else if (item.getItemId() == R.id.delete){
                            GroupActivity.socket.emit("deleteGroup",user.getId(), user.getUsername());
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        //queue = Volley.newRequestQueue(c);
        //loadMembers(id);
        GroupActivity.socket.emit("loadGroupMembers");
        GroupActivity.socket.on("GroupMembers", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray array = (JSONArray) args[0];
                            for (int i=0;i<array.length();i++){
                                JSONObject o = array.getJSONObject(i);
                                list.add(new Member(o.getInt("id"),o.getInt("idUser"),
                                        o.getString("nameUser"),o.getInt("isAdmin") == 1));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("load members","json e: " + e.getMessage());
                        }
                    }
                });
            }
        });

        GroupActivity.socket.on("addingMember", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject o = (JSONObject) args[0];
                            if (o.getBoolean("error")){
                                ToastUtils.longToast(c,"Server error");
                                dialog.dismiss();
                            }else {
                                list.add(new Member(o.getInt("id"), o.getInt("idUser"),
                                        o.getString("nameUser"), o.getInt("isAdmin") == 1));
                                adapter.notifyItemChanged(list.size() - 1);
                                dialog.numAdded--;
                                if (dialog.numAdded == 0){
                                    dialog.dismiss();
                                }
                            }
                        } catch (JSONException e) {
                            ToastUtils.longToast(c,"Server error");
                            dialog.dismiss();
                            Log.e("Json ","j: " + e.getMessage());
                        }
                    }
                });
            }
        });

        GroupActivity.socket.on("removingMember", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int id = (int) args[0];
                        for (int i=0;i<list.size();i++) {
                            if (list.get(i).getIdUser() == id) {
                                if (new UserData(c).getUser().getId() == id){
                                    finish();
                                }
                                list.remove(i);
                                i--;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        GroupActivity.socket.on("newName", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        name = (String) args[0];
                        title.setText(name);
                        ToastUtils.longToast(c, "You changed the group name");
                    }
                });
            }
        });

        GroupActivity.socket.on("groupDeleted", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });

    }

    private void addMember() {
        dialog = new AddMemberDialog(GroupMembersActivity.this,id,nameAdmin,list,adapter);
        dialog.show();
    }

}
