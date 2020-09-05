package zxc.laitooo.warewolfonline.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.objects.winner.Winner;
import zxc.laitooo.warewolfonline.objects.winner.WinnersAdapter;

/**
 * Created by Laitooo San on 2/28/2020.
 */

public class GameOverActivity extends AppCompatActivity{

    ArrayList<Winner> list;
    public WinnersAdapter adapter;
    Context c;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView winnerTeam = (TextView)findViewById(R.id.winners);
        Button ok = (Button) findViewById(R.id.ok);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_winners);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);

        int win_team = 0;
        list = new ArrayList<>();
        list.addAll(GameActivity.winners);
        for (int i=0;i<list.size();i++){
            if (list.get(i).isWinner()){
                win_team = Roles.getTeam(list.get(i).getRole());
            }
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        winnerTeam.setText(Roles.TEAMS_WIN_TEXT[win_team]);

        adapter = new WinnersAdapter(c,list);
        recyclerView.setAdapter(adapter);

        /*try {
            Socket socket = IO.socket(Links.AUDIO);
            socket.connect();
            socket.emit("client-stream-request");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
    }

}
