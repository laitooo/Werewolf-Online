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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import zxc.laitooo.warewolfonline.objects.chatmessage.ChatMessage;
import zxc.laitooo.warewolfonline.objects.chatmessage.ChatMessagesAdapter;
import zxc.laitooo.warewolfonline.picasso.CircleTransformation;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 5/1/2020.
 */

public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private Context c;
    private User user;

    private String userName;
    private int userId;
    private int chatId;
    private String userPic;
    private boolean unfrieded;

    public RecyclerView myRecylerView ;
    public ArrayList<ChatMessage> chatsList ;
    public ChatMessagesAdapter adapter;
    public EditText messagetxt ;
    public ImageButton send, menu;
    public TextView name;
    public ImageView pic;

    TextView connectionStatus;
    boolean opened;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        c = this;
        opened = true;


        connectionStatus = (TextView) findViewById(R.id.connection_status);
        messagetxt = (EditText) findViewById(R.id.message) ;
        name = (TextView) findViewById(R.id.name) ;
        pic = (ImageView) findViewById(R.id.pic) ;
        send = (ImageButton)findViewById(R.id.send);
        menu = (ImageButton)findViewById(R.id.menu);
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);

        connectionStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        connectionStatus.setVisibility(View.GONE);

        chatsList = new ArrayList<>();
        adapter = new ChatMessagesAdapter(ChatActivity.this, chatsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);

        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setAdapter(adapter);

        unfrieded = false;
        user = new UserData(this).getUser();

        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        userId = intent.getIntExtra("userid",0);
        chatId = intent.getIntExtra("chatid",0);
        userPic = Links.USER_PICTURE + userId + ".png";

        name.setText(userName);
        Picasso.with(c)
                .load(userPic)
                .fit()
                .transform(new CircleTransformation())
                .into(pic);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!unfrieded) {
                    if (!messagetxt.getText().toString().isEmpty()) {
                        socket.emit("sendMessage", user.getUsername(), user.getId(),
                                messagetxt.getText().toString());
                        messagetxt.setText("");
                    } else {
                        ToastUtils.shortToast(c, "Your message is empty");
                    }
                }else {
                    ToastUtils.longToast(c, "You are no longer friends");
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(c, v);

                popup.getMenuInflater()
                        .inflate(R.menu.friend_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.unfriend) {
                            if (socket.connected()){
                                socket.emit("unfriend",user.getId(),userId,user.getUsername(),userName);
                                popup.dismiss();
                            }
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        try {

            socket = IO.socket(Links.CHAT_SOCKET + chatId);
            socket.connect();
            socket.emit("join",user.getUsername());

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connected");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(ChatActivity.this, R.color.green));
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
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(ChatActivity.this,R.color.yellow));
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
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(ChatActivity.this,R.color.red));
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject m = (JSONObject) args[0];
                            try {
                                ChatMessage chatMessage = new ChatMessage(m.getInt("id"),
                                        chatId,m.getInt("idsender") == user.getId(),
                                        m.getString("content"),m.getString("time"));
                                chatsList.add(chatMessage);
                                adapter.notifyDataSetChanged();
                                myRecylerView.scrollToPosition(chatsList.size() -1);
                            } catch (JSONException j) {
                                Log.e("on message","json j: " + j.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("loadChatMessages", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    final JSONArray array = (JSONArray) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i=0;i<array.length();i++){
                                    JSONObject o = array.getJSONObject(i);
                                    ChatMessage chatMessage = new ChatMessage(o.getInt("id"),o.getInt("idchat"),
                                            o.getInt("sender") == user.getId(),o.getString("content"),
                                            o.getString("time"));
                                    chatsList.add(chatMessage);
                                }
                                adapter.notifyDataSetChanged();
                                myRecylerView.scrollToPosition(chatsList.size() -1);
                            }catch (JSONException j){
                                Log.e("load chat messages","json j: " + j.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("unfriended", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject o = (JSONObject) args[0];
                                int id1 = o.getInt("id1");
                                int id2 = o.getInt("id2");
                                if (user.getId() == id1){
                                    ToastUtils.longToast(ChatActivity.this,"you are no longer " +
                                            "friend with " + userName);
                                    unfrieded = true;
                                }
                                if (user.getId() == id2){
                                    ToastUtils.longToast(ChatActivity.this,"You are no longer " +
                                            "friend with " + userName);
                                    unfrieded = true;
                                }
                            } catch (JSONException e) {
                                Log.e("unfruended","json e: " + e.getMessage());
                            }
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
