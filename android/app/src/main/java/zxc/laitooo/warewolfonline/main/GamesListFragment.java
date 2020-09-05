package zxc.laitooo.warewolfonline.main;

import android.content.Context;
import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import zxc.laitooo.warewolfonline.activities.GameActivity;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.dialogs.NewGameDialog;
import zxc.laitooo.warewolfonline.objects.game.Game;
import zxc.laitooo.warewolfonline.objects.game.GamesAdapter;
import zxc.laitooo.warewolfonline.user.AppData;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class GamesListFragment extends Fragment {

    GamesAdapter adapter;
    public static GamesAdapter goingAdapter;
    public static ArrayList<Game> goingGames;
    static ArrayList<Game> list;
    Context c;

    UserData userData;
    AppData appData;
    RequestQueue queue;
    public int numPlayers = 1;

    static TextView title1;
    static TextView title2;
    RecyclerView goingRecycler,gamesRecycler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getContext();
        list = new ArrayList<>();
        goingGames = new ArrayList<>();
        adapter = new GamesAdapter(c,list);
        goingAdapter = new GamesAdapter(c,goingGames,true);
        userData = new UserData(c);
        appData = new AppData(c);
        queue = Volley.newRequestQueue(c);

        try {
            MainActivity.numPlayers.setText("1 player");
        }catch (NullPointerException n){
            Log.e("games list","l 83 null n: " + n.getMessage());
        }


        try {
            MainActivity.mainSocket.on("num_players", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        numPlayers = (int) args[0];
                        Log.e("number of online", "" + numPlayers);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (numPlayers != 0) {
                                    MainActivity.numPlayers.setText(numPlayers + " players");
                                } else {
                                    MainActivity.numPlayers.setText("1 player");
                                }
                            }
                        });
                    } catch (NullPointerException n) {
                        Log.e("list fragment", "106 null n:" + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("load_games", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    final JSONArray data = (JSONArray) args[0];
                    Log.e("games list",data.toString());
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject o = data.getJSONObject(i);
                                        Game game = new Game(o.getInt("id"), o.getInt("num"),
                                                o.getInt("max"), o.getInt("idOwner"),
                                                o.getString("nameOwner"));
                                        if (o.getInt("num") != o.getInt("max")) {
                                            if (appData.isGameGoing(game.getId())) {
                                                goingGames.add(game);
                                            } else {
                                                list.add(game);
                                            }
                                        } else {
                                            if (appData.isGameGoing(game.getId())) {
                                                goingGames.add(game);
                                            }
                                        }
                                    }
                                    removeOldGoingGames();
                                    adapter.notifyDataSetChanged();
                                    goingAdapter.notifyDataSetChanged();
                                    updateViews();
                                } catch (JSONException j) {
                                    Log.e("games list", "json j: " + j.getMessage());
                                } catch (NullPointerException n) {
                                    Log.e("games list", "null n: " + n.getMessage());
                                }
                            }
                        });
                    } catch (NullPointerException n) {
                        Log.e("list fragment", "null n:" + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("numChanged", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int gameID = (int) args[0];
                                int num = (int) args[1];
                                for (Game game : list) {
                                    if (game.getId() == gameID) {
                                        game.setNumPlayers(num);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                for (Game game : goingGames) {
                                    if (game.getId() == gameID) {
                                        game.setNumPlayers(num);
                                        goingAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        });
                    } catch (NullPointerException n) {
                        Log.e("list fragment", "null n:" + n.getMessage());
                    }
                }
            });

        }catch (NullPointerException n) {
            Log.e("games list","null n: " + n.getMessage());
        }
    }

    private void removeOldGoingGames() {
        if (appData.getFirstGame() != appData.ERROR_ID_GAME) {
            if (!appData.stillExists(goingGames, appData.getFirstGame())) {
                appData.finishGame(appData.getFirstGame());
            }
        }
        if (appData.getSecondGame() != appData.ERROR_ID_GAME) {
            if (!appData.stillExists(goingGames, appData.getSecondGame())) {
                appData.finishGame(appData.getSecondGame());
            }
        }
        if (appData.getThirdGame() != appData.ERROR_ID_GAME) {
            if (!appData.stillExists(goingGames, appData.getThirdGame())) {
                appData.finishGame(appData.getThirdGame());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games_list,container,false);

        title1 = (TextView)v.findViewById(R.id.title1);
        title2 = (TextView)v.findViewById(R.id.title2);
        title1.setVisibility(View.GONE);
        title2.setVisibility(View.GONE);

        gamesRecycler = (RecyclerView)v.findViewById(R.id.recycler_games);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(c);
        gamesRecycler.setLayoutManager(linearLayoutManager);
        gamesRecycler.setAdapter(adapter);

        goingRecycler = (RecyclerView)v.findViewById(R.id.recycler_going);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(c);
        goingRecycler.setLayoutManager(linearLayoutManager2);
        goingRecycler.setAdapter(goingAdapter);

        FloatingActionButton add = (FloatingActionButton) v.findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            add.setColorFilter(ContextCompat.getColor(c,R.color.color3),
                    PorterDuff.Mode.SRC_IN);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appData.isGamesFull()){
                    ToastUtils.longToast(c,"You already joined three games");
                }else {
                    NewGameDialog newGameDialog = new NewGameDialog(getActivity());
                    newGameDialog.show();
                }

            }
        });

        try {
            MainActivity.mainSocket.on("youCreatedGame", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject o = (JSONObject) args[0];
                                    Game game = new Game(o.getInt("id"), o.getInt("num"),
                                            o.getInt("max"), o.getInt("idOwner"), o.getString("nameOwner"));
                                    //list.add(game);
                                    //adapter.notifyDataSetChanged();
                                    appData.addGame(game.getId());
                                    goingGames.add(game);
                                    goingAdapter.notifyDataSetChanged();
                                    updateViews();

                                    ToastUtils.longToast(c, "Game created");
                                    Intent intent = new Intent(c, GameActivity.class);
                                    intent.putExtra("game_id", game.getId());
                                    intent.putExtra("game_owner_id", game.getOwnerId());
                                    intent.putExtra("game_max", game.getMaxPlayers());
                                    intent.putExtra("game_num", game.getNumPlayers());
                                    intent.putExtra("game_owner_name", game.getOwnerName());
                                    intent.putExtra("isGameOwner", true);
                                    c.startActivity(intent);
                                } catch (JSONException e) {
                                    ToastUtils.jsonException(c);
                                }
                            }
                        });
                    }catch (NullPointerException n){
                        Log.e("list fragment","n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("gameCreated", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject o = (JSONObject) args[0];
                                    Game game = new Game(o.getInt("id"),o.getInt("num"),
                                            o.getInt("max"),o.getInt("idOwner"),o.getString("nameOwner"));
                                    list.add(game);
                                    adapter.notifyDataSetChanged();
                                    updateViews();
                                } catch (JSONException e) {
                                    ToastUtils.jsonException(c);
                                }
                            }
                        });
                    }catch (NullPointerException n){
                        Log.e("list fragment","n: " + n.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("gameFull", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int id = (int) args[0];
                                        for (int i=0;i<list.size();i++) {
                                            if (list.get(i).getId() == id) {
                                                adapter.list.remove(i);
                                                adapter.notifyDataSetChanged();
                                                i--;
                                            }
                                        }
                                        /*for (Game game: list){
                                            if (game.getId() == id){
                                                adapter.list.remove(game);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }*/
                                        updateViews();
                                    }
                                },3000);

                            }
                        });
                    }catch (NullPointerException n){
                        Log.e("games list","n: " + n.getMessage());
                    }catch (ConcurrentModificationException c){
                        Log.e("games list","c: " + c.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("gameOver", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int id = (int) args[0];
                                        for (int i=0;i<goingGames.size();i++){
                                            if (goingGames.get(i).getId() == id){
                                                goingGames.remove(i);
                                                goingAdapter.notifyDataSetChanged();
                                                i--;
                                                appData.finishGame(id);
                                            }
                                        }
                                        updateViews();
                                    }
                                },3000);

                            }
                        });
                    }catch (NullPointerException n){
                        Log.e("games list","n: " + n.getMessage());
                    }catch (ConcurrentModificationException c){
                        Log.e("games list","c: " + c.getMessage());
                    }
                }
            });

            MainActivity.mainSocket.on("gameAbandoned", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int id = (int) args[0];
                                        for (Game game: goingGames){
                                            if (game.getId() == id){
                                                goingGames.remove(game);
                                                goingAdapter.notifyDataSetChanged();
                                                appData.finishGame(id);
                                            }
                                        }
                                        updateViews();
                                    }
                                },3000);

                            }
                        });
                    }catch (NullPointerException n){
                        Log.e("games list","n: " + n.getMessage());
                    }catch (ConcurrentModificationException c){
                        Log.e("games list","c: " + c.getMessage());
                    }
                }
            });
        }catch (Exception e){
            Log.e("GamesListFragment","e: " + e.getMessage());
        }

        return v;
    }

    public static void updateViews(){
        if (list.size() == 0){
            title2.setVisibility(View.GONE);
        }else {
            title2.setVisibility(View.VISIBLE);
        }
        if (goingGames.size() == 0){
            title1.setVisibility(View.GONE);
        }else {
            title1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("OnResume","going: " + goingGames.size());
        ArrayList<Game> newList = new ArrayList<>();
        for (Game g:goingGames){
            //Log.e("game","id: " + g.getId() + " is " + (appData.isGameGoing(g.getId()) ? "going" :
            //"not going"));
            if (appData.isGameGoing(g.getId())){
                newList.add(g);
            }
        }
        goingGames.clear();
        goingGames.addAll(newList);
        goingAdapter.notifyDataSetChanged();
        updateViews();
        //Log.e("OnResume","going: " + goingGames.size());
    }

}
