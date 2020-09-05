package zxc.laitooo.warewolfonline.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.objects.rule.Rule;
import zxc.laitooo.warewolfonline.objects.rule.RulesAdapter;

/**
 * Created by Laitooo San on 6/25/2020.
 */

public class GameRulesActivity extends AppCompatActivity {

    ArrayList<Rule> list;
    RulesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rules);

        list = new ArrayList<>();
        addRules();
        adapter = new RulesAdapter(this,list);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_rules);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ImageButton close = (ImageButton) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addRules() {
        for (int i=1;i< Roles.RolesList.length;i++){
            list.add(new Rule(i,Roles.RolesNames[i],Roles.RolesImages[i]));
        }
    }
}
