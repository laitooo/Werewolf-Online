package zxc.laitooo.warewolfonline.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Integeres;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.dialogs.EventsDialog;
import zxc.laitooo.warewolfonline.dialogs.NightDialog;
import zxc.laitooo.warewolfonline.dialogs.ShowRoleDialog;
import zxc.laitooo.warewolfonline.dialogs.VoteDialog;
import zxc.laitooo.warewolfonline.dialogs.VoteResultDialog;
import zxc.laitooo.warewolfonline.events.Event;
import zxc.laitooo.warewolfonline.objects.game.Game;
import zxc.laitooo.warewolfonline.objects.message.Message;
import zxc.laitooo.warewolfonline.objects.message.MessagesAdapter;
import zxc.laitooo.warewolfonline.objects.player.Player;
import zxc.laitooo.warewolfonline.objects.player.PlayersAdapter;
import zxc.laitooo.warewolfonline.objects.winner.Winner;
import zxc.laitooo.warewolfonline.user.AppData;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 4/27/2020.
 */

public class GameActivity extends AppCompatActivity {

    private int myOrder;
    private Game gameInfo;
    private boolean isGameOwner;
    public static int myRole;
    private boolean iamAlive;
    private int state;
    public static int pastTarget;
    public static boolean usedExir, usedPoison, convertedWolf, usedConversion, mayorRevealed,
            mayorVoted;
    public static int round;
    public static int numBullets;
    public static boolean juniorDead;
    public static boolean playerVote, playerEvent;

    private NightDialog nightDialog;
    private EventsDialog eventsDialog;
    private VoteResultDialog voteResultDialog;
    private VoteDialog voteDialog;
    private ShowRoleDialog showRoleDialog;

    Context c;
    private Socket socket;
    private UserData userData;
    private int numPlayers;

    public RecyclerView recyclerMessages;
    public ArrayList<Message> MessageList ;
    public MessagesAdapter adapter;

    public RecyclerView recyclerPlayers;
    public PlayersAdapter playersAdapter;
    private ArrayList<Player> playersList;

    View blackScreen, newMessages;
    RelativeLayout background;
    LinearLayout layout;
    TextView status,timer;
    public EditText messagetxt ;
    public ImageButton send ;

    public static ArrayList<Winner> winners;
    TextView connectionStatus;
    boolean opened;

    int time;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time != 0){
                handler.postDelayed(runnable,1000);
                time = time - 1000;
                if (time < 10000){
                    timer.setText("00:0" + time / 1000);
                }else {
                    timer.setText("00:" + time / 1000);
                }
            }else {
                timer.setText("00:00");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        c = this;
        time = 0;
        round = 0;
        numBullets = 2;
        juniorDead = false;
        playerVote = false;
        playerEvent = false;

        final Intent intent = getIntent();
        gameInfo = new Game(intent.getIntExtra("game_id",0),intent.getIntExtra("game_num",0),
                intent.getIntExtra("game_max",0),intent.getIntExtra("game_owner_id",0),
                intent.getStringExtra("game_owner_name"));
        isGameOwner = intent.getBooleanExtra("isGameOwner",false);

        userData = new UserData(c);
        playersList = new ArrayList<>();
        winners = new ArrayList<>();
        if (isGameOwner) {
            playersList.add(new Player(userData.getUser().getId(), userData.getUser().getUsername(),
                    true,0, Roles.ERROR,true,true));
            myOrder = 0;
            myRole = 0;
            iamAlive = true;
        }

        state = 0;
        pastTarget = -1;
        usedExir = false;
        usedPoison = false;
        convertedWolf = false;
        usedConversion = false;
        mayorRevealed = false;
        mayorVoted = false;
        opened = true;


        connectionStatus = (TextView) findViewById(R.id.connection_status);
        background = (RelativeLayout) findViewById(R.id.background);
        layout = (LinearLayout) findViewById(R.id.layout);
        messagetxt = (EditText) findViewById(R.id.message);
        status = (TextView) findViewById(R.id.status);
        timer = (TextView) findViewById(R.id.timer);
        send = (ImageButton)findViewById(R.id.send);
        blackScreen = findViewById(R.id.black_screen);
        newMessages = findViewById(R.id.newMessages);

        connectionStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        connectionStatus.setVisibility(View.GONE);

        MessageList = new ArrayList<>();
        recyclerMessages = (RecyclerView) findViewById(R.id.messagelist);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(mLayoutManager);
        //recyclerMessages.setItemAnimator(new DefaultItemAnimator());
        adapter = new MessagesAdapter(c, MessageList);
        recyclerMessages.setAdapter(adapter);
        recyclerMessages.setVisibility(View.GONE);
        blackScreen.setVisibility(View.GONE);
        newMessages.setVisibility(View.GONE);


        recyclerPlayers = (RecyclerView) findViewById(R.id.recycler_players);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(c, Integeres.NUM_PLAYERS_PER_ROW);
        playersAdapter = new PlayersAdapter(playersList,c);
        recyclerPlayers.setLayoutManager(gridLayoutManager);
        recyclerPlayers.setAdapter(playersAdapter);

        timer.setText("1/" + gameInfo.getMaxPlayers());


        messagetxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    recyclerMessages.setVisibility(View.VISIBLE);

                    blackScreen.setVisibility(View.VISIBLE);
                    newMessages.setVisibility(View.GONE);
                }
            }
        });

        recyclerMessages.setOnTouchListener(new View.OnTouchListener() {
            long startClickTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                        recyclerMessages.setVisibility(View.GONE);
                        blackScreen.setVisibility(View.GONE);
                        GameActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        messagetxt.clearFocus();
                    } else {
                        // Touch was a not a simple tap.
                    }
                }
                return true;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state != Integeres.STATE_NIGHT) {
                    if (iamAlive) {
                        try {
                            if (!messagetxt.getText().toString().isEmpty()) {
                                socket.emit("messagedetection", userData.getUser().getUsername(),
                                        messagetxt.getText().toString(), userData.getUser().getId());
                                addMessage(new Message(userData.getUser().getUsername(),
                                        messagetxt.getText().toString(),true,Message.STATE_SENDING,
                                        userData.getUser().getId()));
                                messagetxt.setText("");
                            }
                        } catch (Exception e) {
                            Log.e("send", "e: " + e.getMessage());
                        }
                    } else {
                        ToastUtils.longToast(c, "You are dead, you cant send messages");
                    }
                }else {
                    ToastUtils.longToast(c, "No discussion at night");
                }
            }
        });

        opened = false;

        try {

            Log.e("Game url", Links.GAME_SOCKET + gameInfo.getId());
            socket = IO.socket(Links.GAME_SOCKET + gameInfo.getId());
            socket.connect();
            socket.emit("join",userData.getUser().getUsername(),userData.getUser().getId());

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connected");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.green));
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
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(GameActivity.this,R.color.yellow));
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
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(GameActivity.this,R.color.red));
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

            socket.on("num_players", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    numPlayers = (int) args[0];
                    Log.e("Num Players","" + numPlayers);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.setText(numPlayers + "/" + gameInfo.getMaxPlayers());
                            if (numPlayers == gameInfo.getMaxPlayers()){
                                time = Integeres.TIME_ROLES;
                                handler.postDelayed(runnable,1000);
                            }
                        }
                    });
                }
            });

            socket.on("youJoined", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray array = (JSONArray) args[0];
                                for (int i=0;i<array.length();i++){
                                    JSONObject o = array.getJSONObject(i);
                                    if (o.getInt("userId") != userData.getUser().getId()) {
                                        playersList.add(new Player(o.getInt("userId"),
                                                o.getString("userName"), o.getBoolean("online"),
                                                o.getInt("order"), Roles.ERROR, true,
                                                o.getBoolean("revealed")));
                                    } else {
                                        playersList.add(new Player(o.getInt("userId"),
                                                o.getString("userName"), o.getBoolean("online"),
                                                o.getInt("order"), Roles.ERROR, true, true));
                                    }
                                }
                                iamAlive = true;
                                //addMessage(new Message(true,"You joined the game",2));
                                playersAdapter.notifyDataSetChanged();
                                myOrder = playersList.size() - 1;
                            }catch (JSONException e){
                                Log.e("you joined","json e: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("youGotBack", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject object = (JSONObject) args[0];
                                numPlayers = object.getInt("numPlayer");
                                juniorDead = object.getBoolean("juniorDied");
                                if (juniorDead){
                                    addMessage(new Message(true, "The junior warewolf is dead, you can kill" +
                                            " two villagers this turn", 3));
                                    if (blackScreen.getVisibility() != View.VISIBLE){
                                        newMessages.setVisibility(View.VISIBLE);
                                    }
                                }

                                JSONArray array = object.getJSONArray("players");
                                for (int i=0;i<array.length();i++){
                                    JSONObject o = array.getJSONObject(i);
                                    Player p = new Player(o.getInt("userId"),
                                            o.getString("userName"),o.getBoolean("online"),
                                            o.getInt("order"),o.getInt("role"),
                                            o.getBoolean("alive"),o.getBoolean("revealed"));
                                    if (o.getInt("userId") == userData.getUser().getId()){
                                        iamAlive = o.getBoolean("alive");
                                        myOrder = o.getInt("order");
                                        myRole = o.getInt("role");
                                        Log.e("you got","back " + myRole);
                                        p.setRevealed(true);
                                    }
                                    playersList.add(p);
                                }
                                playersAdapter.notifyDataSetChanged();

                                JSONArray array2 = object.getJSONArray("messages");
                                for (int i=0;i<array2.length();i++){
                                    JSONObject m = array2.getJSONObject(i);
                                    int id = m.getInt("senderId");
                                    addMessage(new Message(m.getString("senderNickname"),
                                            m.getString("message"),id == userData.getUser().getId(),
                                            Message.STATE_SENT,id,m.getInt("id")));
                                }
                                Log.e("you got","back messages loaded");
                                if (array2.length() > 0 && blackScreen.getVisibility() != View.VISIBLE){
                                    newMessages.setVisibility(View.VISIBLE);
                                }

                                //num_player.setText(numPlayers + "/" + gameInfo.getMaxPlayers());
                                JSONArray votes = object.getJSONArray("playersVotes");
                                if (votes.length() > 0) {
                                    playerVote = votes.getBoolean(myOrder);
                                }

                                JSONArray events = object.getJSONArray("playersEvents");
                                if (events.length() > 0) {
                                    playerEvent = events.getBoolean(myOrder);
                                }


                                int state = object.getInt("state");
                                loadState(state);


                                loadRoleState(object);
                            }catch (JSONException e){
                                Log.e("you joined","json e: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("userJoinedTheGame", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject data = (JSONObject) args[0];
                                Log.e("User Joined", "user " + data.getString("user_name") + " id "
                                        + data.getInt("user_id") + " joined the game");
                                playersList.add(new Player(data.getInt("user_id"),
                                        data.getString("user_name"),true,
                                        playersList.size(),Roles.ERROR,true,data.getBoolean("revealed")));
                                playersAdapter.notifyDataSetChanged();
                                //addMessage(new Message(true,data.getString("user_name") +
                                //        " joined the game",2));
                            }catch (JSONException e){
                                Log.e("user joined ","json e: " + e.getMessage());
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
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String nickname = data.getString("senderNickname");
                                String message = data.getString("message");

                                Message m = new Message(nickname,message,false,0,data.getInt("senderId"),
                                        data.getInt("id"));
                                addMessage(m);
                                if (blackScreen.getVisibility() != View.VISIBLE){
                                    newMessages.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            });

            socket.on("messageSent", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String nickname = data.getString("senderNickname");
                                String message = data.getString("message");
                                for (int i=0;i<MessageList.size();i++){
                                    Message m = MessageList.get(i);
                                    if (m.isFromServer())
                                        continue;
                                    if (m.getUserName().equals(nickname) &&
                                            m.getText().equals(message) && m.getState() == Message.STATE_SENDING){
                                        m.setState(Message.STATE_SENT);
                                        m.setId(data.getInt("id"));
                                        adapter.notifyItemChanged(i);
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            });

            socket.on("userdisconnect", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = (String) args[0];
                            ToastUtils.longToast(c,data);
                        }
                    });
                }
            });

            socket.on("playerOffline", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int order = (int) args[0];
                                playersList.get(order).setOnline(false);
                                playersAdapter.notifyDataSetChanged();
                            }catch (Exception e){
                                Log.e("player offline","E: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            //TODO Index out of bounds exception
            socket.on("playerOnline", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int order = (int) args[0];
                            playersList.get(order).setOnline(true);
                            playersAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

            socket.on("yourOrder", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    myOrder = (int) args[0];
                }
            });

            socket.on("loadMessages", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray array = (JSONArray) args[0];
                                for (int i=0;i<array.length();i++){
                                    JSONObject m = array.getJSONObject(i);
                                    int id = m.getInt("senderId");
                                    addMessage(new Message(m.getString("senderNickname"),
                                            m.getString("message"),id == userData.getUser().getId()
                                            ,0,id,m.getInt("id")));
                                }
                                if (array.length() > 0 && blackScreen.getVisibility() != View.VISIBLE){
                                    newMessages.setVisibility(View.VISIBLE);
                                }
                            }catch (JSONException e){
                                Log.e("load messages","json e: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("rolesGenerated", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("Morning");
                            playersList.clear();
                            JSONArray data = (JSONArray) args[0];
                            try {
                                for (int i=0;i<data.length();i++){
                                    JSONObject o = data.getJSONObject(i);
                                    Player p = new Player(o.getInt("userId"),
                                            o.getString("userName"),o.getBoolean("online"),
                                            o.getInt("order"),o.getInt("role"),true, o.getBoolean("revealed"));
                                    if (o.getInt("userId") == userData.getUser().getId()){
                                        myOrder = o.getInt("order");
                                        myRole = o.getInt("role");
                                        showRoleDialog = new ShowRoleDialog(GameActivity.this
                                                ,myRole);
                                        safeShowDialog(showRoleDialog);
                                        if (myRole == Roles.GUNNER) {
                                            numBullets = 2;
                                        }
                                        p.setRevealed(true);
                                    }
                                    playersList.add(p);
                                }
                                playersAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                Log.e("roles generated","json e: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("convertedtoWolf", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int id = (int) args[0];
                            if (id == userData.getUser().getId()){
                                myRole = Roles.WAREWOLF;
                                convertedWolf = true;
                            }
                            for (Player p: playersList){
                                if (p.getId() == id){
                                    p.setRole(Roles.WAREWOLF);
                                    Log.e("Converted num" , " id : " + p.getId());

                                }
                            }
                            playersAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

            socket.on("stateChanged", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    state = (int) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeCallbacks(runnable);
                            switch (state){
                                case Integeres.STATE_DAY:
                                    //background.setBackgroundResource(R.drawable.wallpaper_day);
                                    time = Integeres.TIME_DAY;
                                    handler.postDelayed(runnable,1000);
                                    changeConversionMessage();
                                    //addMessage(new Message(true,"Its Morning",2));
                                    status.setText("Morning");
                                    if (eventsDialog != null){
                                        eventsDialog.destroy();
                                    }
                                    break;
                                case Integeres.STATE_NIGHT:
                                    //background.setBackgroundResource(R.drawable.wallpaper_night);
                                    time = Integeres.TIME_NIGHT;
                                    handler.postDelayed(runnable,1000);
                                    //addMessage(new Message(true,"Night has came",2));
                                    status.setText("Night");
                                    if (voteResultDialog != null){
                                        if (voteResultDialog.isShowing()){
                                            voteResultDialog.destroy();
                                        }
                                    }
                                    break;
                                case Integeres.STATE_VOTE:
                                    //background.setBackgroundResource(R.drawable.wallpaper_vote);
                                    time = Integeres.TIME_VOTE;
                                    handler.postDelayed(runnable,1000);
                                    //addMessage(new Message(true,"Voting time",2));
                                    status.setText("Voting");
                                    if (showRoleDialog != null){
                                        if (showRoleDialog.isShowing()){
                                            showRoleDialog.dismiss();
                                        }
                                    }
                                    break;
                                case Integeres.STATE_REVEALING:
                                    playerVote = false;
                                    playerEvent = false;
                                    time = Integeres.TIME_REVEAL;
                                    handler.postDelayed(runnable,1000);
                                    if (nightDialog != null){
                                        if (nightDialog.isShowing()){
                                            nightDialog.destroy();
                                        }
                                    }
                                    status.setText("Reveal");
                                    break;
                                case Integeres.STATE_VOTE_RESULT:
                                    playerVote = false;
                                    playerEvent = false;
                                    time = Integeres.TIME_VOTE_RESULT;
                                    handler.postDelayed(runnable,1000);
                                    status.setText("Vote result");
                                    if (voteDialog != null){
                                        if (voteDialog.isShowing()){
                                            voteDialog.destroy();
                                        }
                                    }
                                    break;
                                default:
                                    time = 0;
                                    status.setText("ERROR");
                                    break;
                            }
                        }
                    });
                }
            });

            socket.on("startVoting", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("start voting","started");
                            if (iamAlive) {
                                voteDialog = new VoteDialog(GameActivity.this, playersList,
                                        socket, myRole);
                                safeShowDialog(voteDialog);
                            }
                            //addMessage(new Message(true,"Game started",1));
                        }
                    });
                }
            });

            socket.on("startNight", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("start night","started");
                            if (Roles.hasAbility(myRole)) {
                                if (iamAlive) {
                                    if (myRole == Roles.MAYOR){
                                        if (!mayorRevealed){
                                            nightDialog = new NightDialog(GameActivity.this, playersList,
                                                    socket, myRole, pastTarget);
                                            safeShowDialog(nightDialog);
                                        }
                                    }else {
                                        nightDialog = new NightDialog(GameActivity.this, playersList,
                                                socket, myRole, pastTarget);
                                        safeShowDialog(nightDialog);
                                    }
                                }
                            }else {
                                //ToastUtils.longToast(c,"ERROR");
                            }
                        }
                    });
                }
            });

            socket.on("drawVotes", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("voting finished","draw");
                            //ToastUtils.longToast(c,"Vote draw");
                            //addMessage(new Message(true,"Votes are draw",1));
                            voteResultDialog = new VoteResultDialog(GameActivity.this);
                            safeShowDialog(voteResultDialog);
                        }
                    });
                }
            });

            socket.on("playerDiedByVotes", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int userId = (int) args[0];
                            //ToastUtils.longToast(c,"User with id " + userId + " died by vote");
                            String name = "",pic = "";
                            int role = 0;
                            int n=0;
                            for (Player p :playersList){
                                if (p.getId() == userId){
                                    if (p.getId() == userData.getUser().getId()){
                                        iamAlive = false;
                                    }
                                    p.setAlive(false);
                                    name = p.getName();
                                    pic = p.getPicture();
                                    role = p.getRole();
                                }
                            }
                            playersAdapter.notifyDataSetChanged();
                            addMessage(new Message(true,"The villagers voted to kill " + name,3));
                            voteResultDialog = new VoteResultDialog(GameActivity.this,
                                    name,pic,role);
                            safeShowDialog(voteResultDialog);
                            if (blackScreen.getVisibility() != View.VISIBLE){
                                newMessages.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            socket.on("playerDiedByMad", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int userId = (int) args[0];
                            //ToastUtils.longToast(c,"User with id " + userId + " died by vote");
                            String name = "",pic = "";
                            int role = 0;
                            int n=0;
                            for (Player p :playersList){
                                if (p.getId() == userId){
                                    if (p.getId() == userData.getUser().getId()){
                                        iamAlive = false;
                                    }
                                    p.setAlive(false);
                                    name = p.getName();
                                    pic = p.getPicture();
                                    role = p.getRole();
                                }
                            }
                            playersAdapter.notifyDataSetChanged();
                            addMessage(new Message(true,"The mad scientist killed " + name,3));
                            if (blackScreen.getVisibility() != View.VISIBLE){
                                newMessages.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            socket.on("nightEvents", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONArray events = (JSONArray) args[0];
                                    boolean isGameOver = (int) args[1] < 2;
                                    try {
                                        ArrayList<Event> eventArrayList = new ArrayList<Event>();
                                        for (int i = 0; i < events.length(); i++) {
                                            JSONObject o = events.getJSONObject(i);
                                            int userId = o.getInt("userId");
                                            //ToastUtils.longToast(c, "User with id " + userId + " died by Warewolfs");
                                            String name = "", pic = "";
                                            int role = 0;
                                            for (Player p : playersList) {
                                                if (p.getId() == userId) {
                                                    if (p.getId() == userData.getUser().getId()) {
                                                        iamAlive = false;
                                                    }
                                                    p.setAlive(false);
                                                    name = p.getName();
                                                    pic = p.getPicture();
                                                    role = p.getRole();
                                                }
                                            }
                                            eventArrayList.add(new Event(Roles.RolesNames[role] +
                                                    " has been killed", pic, name, true));
                                            playersAdapter.notifyDataSetChanged();
                                            addMessage(new Message(true, name + " is killed", 3));
                                            if (blackScreen.getVisibility() != View.VISIBLE){
                                                newMessages.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        if (!isGameOver && !GameActivity.this.isFinishing()) {
                                            eventsDialog = new EventsDialog(eventArrayList);
                                            eventsDialog.show(getSupportFragmentManager(), "");
                                        }
                                        round++;
                                    } catch (JSONException j) {
                                        Log.e("night events", "json j: " + j.getMessage());
                                    }
                                }
                            });

                        }
                    });


                    socket.on("gameOver", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            state = Integeres.STATE_GAME_OVER;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        handler.removeCallbacks(runnable);
                                        JSONObject obj = (JSONObject) args[0];
                                        JSONArray array1 = obj.getJSONArray("winners");
                                        for (int i = 0; i < array1.length(); i++) {
                                            JSONObject o = array1.getJSONObject(i);
                                            winners.add(new Winner(o.getInt("userId"),
                                                    o.getString("userName"), o.getInt("role"),
                                                    o.getBoolean("alive"), o.getBoolean("winner")));
                                        }

                                        addMessage(new Message(true, "Game Over", 1));
                                        timer.setText("");
                                        status.setText("Game Over");
                                        new AppData(c).finishGame(gameInfo.getId());

                                        JSONArray array2 = obj.getJSONArray("players");
                                        for (int i = 0; i < array2.length(); i++) {
                                            JSONObject o = array2.getJSONObject(i);
                                            for (Player p : playersList) {
                                                if (p.getId() == o.getInt("userId")) {
                                                    p.setAlive(o.getBoolean("alive"));
                                                }
                                            }
                                        }
                                        if (blackScreen.getVisibility() != View.VISIBLE){
                                            newMessages.setVisibility(View.VISIBLE);
                                        }
                                        playersAdapter.notifyDataSetChanged();
                                        Runnable r = new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent1 = new Intent(c, GameOverActivity.class);
                                                startActivity(intent1);
                                                finish();
                                            }
                                        };
                                        new Handler().postDelayed(r,2000);
                                    } catch (JSONException j) {
                                        Log.e("On Game Over", "json j: " + j.getMessage());
                                    }
                                }
                            });
                        }
                    });

                    /*socket.on("winners", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        handler.removeCallbacks(runnable);
                                        JSONArray array = (JSONArray) args[0];
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject o = array.getJSONObject(i);
                                            winners.add(new Winner(o.getInt("userId"),
                                                    o.getString("userName"), o.getInt("role"),
                                                    o.getBoolean("alive"), o.getBoolean("winner")));
                                        }

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent1 = new Intent(c, GameOverActivity.class);
                                                startActivity(intent1);
                                            }
                                        }, Integeres.TIME_FINISHED);
                                    } catch (JSONException j) {
                                        Log.e("On winners", "json j: " + j.getMessage());
                                    }
                                }
                            });
                        }
                    });*/

            socket.on("revealPlayer", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject o = (JSONObject) args[0];
                                addMessage(new Message(true, o.getString("name") + " " +
                                        Roles.RolesNames[o.getInt("role")] + " has revealed himself "
                                        , 3));
                                for (Player p: playersList){
                                    if (p.getId() == o.getInt("id")){
                                        p.setRevealed(true);
                                    }
                                }
                                if (blackScreen.getVisibility() != View.VISIBLE){
                                    newMessages.setVisibility(View.VISIBLE);
                                }
                                playersAdapter.notifyDataSetChanged();
                            }catch (JSONException e){
                                Log.e("revealMayor","json e: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            socket.on("juniorDied", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            juniorDead = true;
                            addMessage(new Message(true, "The junior warewolf is dead, you can kill" +
                                    " two targets this time", 3));
                            if (blackScreen.getVisibility() != View.VISIBLE){
                                newMessages.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            socket.on("gameAbandoned", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.longToast(c,"Game finished due to inactivity");
                        }
                    });
                    Log.e("GameActivity","game abandoned");
                    finish();
                }
            });


        } catch(URISyntaxException e) {
            Log.e("Game Socket", "uri e: " + e.getMessage());
        }
    }

    private void changeConversionMessage() {
        if (convertedWolf){
            for (Message m:MessageList){
                if (m.isFromServer() && m.isConvertedMessage())
                    return;
            }
            addMessage(new Message(true,Message.CONVERSION_MESSAGE,3));
            if (blackScreen.getVisibility() != View.VISIBLE){
                newMessages.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadRoleState(JSONObject object) throws JSONException{
        if (myRole == Roles.DOCTOR) {
            JSONArray doctors = object.getJSONArray("doctors");
            for (int i=0;i<doctors.length();i++){
                JSONObject doctor = doctors.getJSONObject(i);
                if (doctor.getInt("id") == userData.getUser().getId()){
                    pastTarget = doctor.getInt("lastTarget");
                    if (pastTarget == 0)
                        pastTarget = -1;
                }
            }
            Log.e("you got","back roles doctors target: " + pastTarget);
        } else if (myRole == Roles.WITCH) {
            JSONArray witches = object.getJSONArray("witches");
            for (int i=0;i<witches.length();i++){
                JSONObject witch = witches.getJSONObject(i);
                if (witch.getInt("id") == userData.getUser().getId()){
                    usedExir = witch.getBoolean("usedExir");
                    usedPoison = witch.getBoolean("usedPoison");
                }
            }
            Log.e("you got","back roles witches exir: " + (usedExir? " yes":"no") + " poison: " +
                    (usedPoison? " yes":"no"));
        } else if (myRole == Roles.GUNNER) {
            JSONArray gunners = object.getJSONArray("gunners");
            for (int i=0;i<gunners.length();i++){
                JSONObject gunner = gunners.getJSONObject(i);
                if (gunner.getInt("id") == userData.getUser().getId()){
                    numBullets = gunner.getInt("numBullets");
                }
            }
            Log.e("you got","back roles gunners bullets: " + numBullets);
        } else if (myRole == Roles.MAYOR) {
            JSONArray mayors = object.getJSONArray("mayors");
            for (int i=0;i<mayors.length();i++){
                JSONObject mayor = mayors.getJSONObject(i);
                if (mayor.getInt("id") == userData.getUser().getId()){
                    mayorRevealed = mayor.getBoolean("isRevealed");
                    mayorVoted = mayor.getBoolean("doubleVoted");
                }
            }
            Log.e("you got","back roles mayors revealed: " + (mayorRevealed ? " true" : " false")
                    + " double voted: "+ (mayorVoted ? " true" : " false"));
        } else if (myRole == Roles.ALPHA_WAREWOLF) {
            JSONObject alpha = object.getJSONObject("alphaWolf");
            if (alpha.getInt("id") == userData.getUser().getId()){
                usedConversion = alpha.getBoolean("converted");
            }
            Log.e("you got","alpha warewolf " + (usedConversion ? "converted" : "not converted"));
        }
        playersAdapter.notifyDataSetChanged();
    }

    private void loadState(int state) {
        Log.e("you got","back states loaded");
        if (playersList.size() >= gameInfo.getMaxPlayers()) {
            timer.setText("00:00");
        }else {
            timer.setText(numPlayers + "/" + gameInfo.getMaxPlayers());
        }
        switch (state){
            case Integeres.STATE_VOTE_RESULT:
                status.setText("Vote result");
                break;
            case Integeres.STATE_VOTE:
                Log.e("start voting","started");
                if (iamAlive) {
                    // TODO :: handle mayor
                        if (playerVote) {
                            voteDialog = new VoteDialog(GameActivity.this, playersList,
                                    socket, myRole);
                            safeShowDialog(voteDialog);
                        }
                }
                status.setText("Voting");
                break;
            case Integeres.STATE_DAY:
                status.setText("Morning");
                break;
            case Integeres.STATE_NIGHT:
                Log.e("start night","started");
                if (Roles.hasAbility(myRole)) {
                    if (iamAlive) {
                        if (myRole == Roles.MAYOR){
                            if (!mayorRevealed){
                                if (!playerEvent) {
                                    nightDialog = new NightDialog(GameActivity.this, playersList,
                                            socket, myRole, pastTarget);
                                    safeShowDialog(nightDialog);
                                }
                            }
                        }else {
                            if (!playerEvent) {
                                nightDialog = new NightDialog(GameActivity.this, playersList,
                                        socket, myRole, pastTarget);
                                safeShowDialog(nightDialog);
                            }
                        }
                    }
                }
                status.setText("Night");
                break;
            case Integeres.STATE_REVEALING:
                status.setText("Revealing");
                break;
            default:
                status.setText("Error");
                break;
        }
    }

    public void addMessage(Message message){
        MessageList.add(message);
        //Log.e("Message",message.log());
        adapter.notifyDataSetChanged();
        recyclerMessages.scrollToPosition(MessageList.size()-1);
    }

    public void safeShowDialog(Dialog dialog){
        if(!this.isFinishing()) {
            dialog.show();
        }else {
            ToastUtils.longToast(this,"ERROR SAFE SHOW DIALOG");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.emit("getOffline",userData.getUser().getId(),userData.getUser().getUsername(),
                    myOrder);
        }catch (Exception i){
            Log.e("gameActivity","onDestroy i: " + i.getMessage());
        }finally {
            socket.disconnect();
            socket.close();
            handler.removeCallbacks(runnable);
        }
    }

}