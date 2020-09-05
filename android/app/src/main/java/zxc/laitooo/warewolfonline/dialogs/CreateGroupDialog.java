package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 6/3/2020.
 */

public class CreateGroupDialog extends Dialog {

    Activity activity;
    EditText groupName;
    Button creaete;
    User user;

    public CreateGroupDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        user = new UserData(this.activity).getUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_group);

        groupName = (EditText)findViewById(R.id.group_name);
        creaete = (Button)findViewById(R.id.create);
        creaete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupName.getText().toString().length() == 0){
                    ToastUtils.shortToast(activity,"Please fill the field");
                }else {
                    if (MainActivity.mainSocket.connected()){
                    MainActivity.mainSocket.emit("createGroup",user.getId(), user.getUsername(),
                            groupName.getText().toString());
                    }else {
                        ToastUtils.networkError(activity);
                    }
                }
            }
        });

    }

}
