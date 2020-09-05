package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import io.socket.client.Socket;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 7/29/2020.
 */

public class ChangeGroupNameDialog extends Dialog {

    Activity activity;
    EditText groupName;
    Button rename;
    User user;
    Socket socket;

    public ChangeGroupNameDialog(Activity activity,Socket socket) {
        super(activity);
        this.socket = socket;
        this.activity = activity;
        user = new UserData(this.activity).getUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_group_name);

        groupName = (EditText)findViewById(R.id.group_name);
        rename = (Button)findViewById(R.id.create);
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupName.getText().toString().length() == 0){
                    ToastUtils.shortToast(activity,"Please fill the field");
                }else {
                    if (socket.connected()) {
                        socket.emit("renameGroup", user.getUsername(),
                                groupName.getText().toString());
                        dismiss();
                    }else {
                        ToastUtils.networkError(activity);
                    }
                }
            }
        });

    }

}
