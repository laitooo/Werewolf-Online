package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import io.socket.client.Socket;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GameActivity;
import zxc.laitooo.warewolfonline.constants.Integeres;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.objects.player.Player;
import zxc.laitooo.warewolfonline.objects.vote.Vote;
import zxc.laitooo.warewolfonline.objects.vote.VotesAdapter;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 5/12/2020.
 */

public class VoteDialog extends Dialog {

    ArrayList<Vote> list;
    Activity activity;
    public VotesAdapter adapter;
    Socket socket;
    User user;
    boolean dismissed;
    int myRole;

    public VoteDialog(Activity activity, ArrayList<Player> players, Socket s, int role) {
        super(activity);
        this.activity = activity;
        user = new UserData(activity).getUser();
        list = new ArrayList<>();
        for (Player p:players){
            if (p.isAlive()) {
                if (p.getId() != user.getId()) {
                    list.add(new Vote(p.getId(), p.getName(), p.getPicture(), false, p.getOrder(),
                            p.getRole()));
                }
            }
        }
        myRole = role;
        adapter = new VotesAdapter(activity, list, Roles.isWolf(myRole));
        socket = s;
    }

    public void updateConverted(int id){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getId() == id){
                list.get(i).setRole(Roles.WAREWOLF);
                adapter.notifyItemChanged(i);
            }
        }
    }

    int numVotes = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vote);
        final Button vote = (Button) findViewById(R.id.vote);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_votes);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, Integeres.NUM_PLAYERS_PER_ROW);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setCanceledOnTouchOutside(false);

        if (myRole != Roles.MAYOR) {
            vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.selectedOrder != -1) {
                        socket.emit("vote", user.getId(), user.getUsername(), adapter.selectedOrder);
                        GameActivity.playerVote = true;
                        dismiss();
                    } else {
                        ToastUtils.shortToast(getContext(), "Select first");
                    }
                }
            });
        }else {
            if (GameActivity.mayorRevealed) {
                numVotes = 2;
                if (GameActivity.mayorVoted) {
                    numVotes = 1;
                }else {
                    GameActivity.mayorVoted = true;
                }
            }



            vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.selectedOrder != -1) {
                        if (numVotes == 2) {
                            socket.emit("vote", user.getId(), user.getUsername(), adapter.selectedOrder);
                            vote.setText("Vote again");
                            adapter.deselectAll();
                            GameActivity.playerVote = true;
                            numVotes--;
                        }else if (numVotes == 1) {
                            numVotes--;
                            socket.emit("vote", user.getId(), user.getUsername(), adapter.selectedOrder);
                            dismiss();
                        }
                    } else {
                        ToastUtils.shortToast(getContext(), "Select first");
                    }
                }
            });
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dismissed) {
                    dismissed = true;
                    dismiss();
                }
            }
        }, Integeres.TIME_VOTE);
    }

    public void destroy(){
        if (!dismissed) {
            dismissed = true;
            dismiss();
        }
    }
}
