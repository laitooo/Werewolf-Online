package zxc.laitooo.warewolfonline.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.constants.Rules;

/**
 * Created by Laitooo San on 7/23/2020.
 */

public class RuleDetailsActivity extends AppCompatActivity {

    int role;
    ImageView icon;
    TextView name,detail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_details);
        role = getIntent().getIntExtra("rule",0);
        icon = (ImageView)findViewById(R.id.icon);
        name = (TextView)findViewById(R.id.name);
        detail = (TextView)findViewById(R.id.details);

        name.setText(Roles.RolesNames[role]);
        icon.setImageResource(Roles.RolesImages[role]);
        detail.setText(Rules.STORIES[role]);
    }
}
