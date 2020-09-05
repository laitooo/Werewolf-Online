package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Integeres;
import zxc.laitooo.warewolfonline.constants.Roles;

/**
 * Created by Laitooo San on 5/11/2020.
 */

public class ShowRoleDialog extends Dialog {

    Activity activity;
    int role;

    public ShowRoleDialog(Activity activity, int role) {
        super(activity);
        this.activity = activity;
        this.role = role;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_charracter);
        ImageView imageRole = (ImageView)findViewById(R.id.role);
        TextView textRole = (TextView)findViewById(R.id.text_role);
        imageRole.setImageResource(Roles.RolesImages[role]);
        textRole.setText("You are a " + Roles.RolesNames[role]);
        Button ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, Integeres.TIME_ROLES);
    }
}
