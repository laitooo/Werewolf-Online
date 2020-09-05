package zxc.laitooo.warewolfonline.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.objects.group.Group;
import zxc.laitooo.warewolfonline.objects.groupmessage.GroupMessage;
import zxc.laitooo.warewolfonline.objects.groupmessage.GroupMessagesAdapter;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class GroupActivity extends AppCompatActivity {

    public static Socket socket;
    private Context c;
    private User user;
    private Group group;

    public RelativeLayout appBar;
    public RecyclerView myRecylerView ;
    public ArrayList<GroupMessage> list ;
    public GroupMessagesAdapter adapter;
    public EditText messagetxt ;
    public ImageButton send ;
    public TextView name;
    public ImageView pic;

    TextView connectionStatus;
    boolean opened;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        c = this;
        opened = true;


        connectionStatus = (TextView) findViewById(R.id.connection_status);
        appBar = (RelativeLayout) findViewById(R.id.appbar);
        messagetxt = (EditText) findViewById(R.id.message) ;
        name = (TextView) findViewById(R.id.name) ;
        pic = (ImageView) findViewById(R.id.pic) ;
        send = (ImageButton)findViewById(R.id.send);
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);

        connectionStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        connectionStatus.setVisibility(View.GONE);

        list = new ArrayList<>();
        adapter = new GroupMessagesAdapter(GroupActivity.this, list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);
        //mLayoutManager.setReverseLayout(true);

        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setAdapter(adapter);
        myRecylerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        user = new UserData(this).getUser();

        final Intent intent = getIntent();
        group = new Group(intent.getIntExtra("id",0),intent.getStringExtra("groupName"),
                intent.getIntExtra("idAdmin",0),intent.getStringExtra("nameAdmin"),
                intent.getIntExtra("numMembers",1));

        name.setText(group.getGroupName());
        Picasso.with(c)
                .load(R.drawable.icon)
                .fit()
                .transform(new CircleTransformation())
                .into(pic);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messagetxt.getText().toString().isEmpty()){
                    socket.emit("sendMessage",user.getUsername(),user.getId(),
                            messagetxt.getText().toString());
                    messagetxt.setText("");
                }else {
                    ToastUtils.shortToast(c,"Your message is empty");
                }
            }
        });

        appBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(GroupActivity.this,GroupMembersActivity.class);
                intent1.putExtra("id",group.getId());
                intent1.putExtra("name",group.getGroupName());
                intent1.putExtra("nameAdmin",group.getAdminName());
                intent1.putExtra("idAdmin",group.getAdminId());
                startActivity(intent1);
            }
        });

        try {

            socket = IO.socket(Links.GROUP_SOCKET + group.getId());
            socket.connect();
            socket.emit("join",user.getUsername());

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connected");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(GroupActivity.this, R.color.green));
                            if (opened) {
                                opened = false;
                                connectionStatus.animate()
                                        .translationY(0)
                                        .alpha(0.0f)
                                        .setDuration(500)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                connectionStatus.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    });
                }
            });

            socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connecting");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(GroupActivity.this,R.color.yellow));
                        }
                    });
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setVisibility(View.VISIBLE);
                            connectionStatus.setText("No Connection");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(GroupActivity.this,R.color.red));
                            if (!opened) {
                                opened = true;
                                connectionStatus.animate()
                                        .translationY(0)
                                        .alpha(1.0f)
                                        .setDuration(500)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                connectionStatus.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        }
                    });
                }
            });


            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    final JSONObject o = (JSONObject) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GroupMessage groupMessage = new GroupMessage(o.getInt("id"),
                                        o.getInt("userId"),o.getString("userName"),
                                        o.getString("messageContent"),o.getString("time"),
                                        o.getBoolean("info"));
                                list.add(groupMessage);
                            } catch (JSONException j) {
                                Log.e("on messages","json j: " + j.getMessage());
                            }
                            adapter.notifyDataSetChanged();
                            myRecylerView.scrollToPosition(list.size() -1);

                        }
                    });
                }
            });

            socket.on("loadGroupMessages", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    final JSONArray array = (JSONArray) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("array",array.toString());
                            try {
                                for (int i=0;i<array.length();i++){
                                    JSONObject o = array.getJSONObject(i);
                                    GroupMessage message = new GroupMessage(o.getInt("id"),
                                            o.getInt("userId"), o.getString("userName"),
                                            o.getString("content"), o.getString("time"),
                                            o.getInt("info") == 1);
                                    //Log.e("message",message.getUserName() + " : " + message.getContent());
                                    list.add(message);
                                }
                                adapter.notifyDataSetChanged();
                                myRecylerView.scrollToPosition(list.size() -1);
                            }catch (JSONException j){
                                Log.e("load chat messages","json j: " + j.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("closeGroup", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ((int)args[0] == user.getId()){
                                ToastUtils.longToast(c,"You have been removed from this group");
                                finish();
                            }
                        }
                    });
                }
            });

            socket.on("nameChanged", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            group.setGroupName((String) args[0]);
                            name.setText(group.getGroupName());
                        }
                    });
                }
            });

            socket.on("deletedGroup", new Emitter.Listener() {
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

            /*socket.on("userdisconnect", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = (String) args[0];
                            Toast.makeText(ChatActivity.this,data,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });*/

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.close();
    }

}
