package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 5/22/2020.
 */

public class AddFriendDialog extends Dialog {

    Activity activity;
    EditText id;
    Button add;
    RequestQueue queue;
    User user;

    public AddFriendDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        queue = Volley.newRequestQueue(this.activity);
        user = new UserData(this.activity).getUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_friend);

        id = (EditText)findViewById(R.id.id_friend);
        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().length() == 0){
                    ToastUtils.shortToast(activity,"Please fill the field");
                }else {
                    if (id.getText().toString().equals(user.getId() + "")){
                        ToastUtils.shortToast(activity,"you cant send request to yourself");
                    }else {
                        if (MainActivity.mainSocket.connected()) {
                            //addFriend(id.getText().toString(), user);
                            MainActivity.mainSocket.emit("sendRequest", user.getId(), id.getText().toString() + "",
                                    user.getUsername());
                        }else {
                            ToastUtils.networkError(activity);
                        }
                    }
                }
            }
        });

        MainActivity.mainSocket.on("requestState", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    final JSONObject o = (JSONObject) args[0];
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!o.getBoolean("error")){
                                    ToastUtils.longToast(activity,"Friend Request sent");
                                    dismiss();
                                }else {
                                    if (o.getInt("state") == 1){
                                        ToastUtils.longToast(activity,"user does not exist");
                                    }else if (o.getInt("state") == 2){
                                        ToastUtils.longToast(activity,"you have already sent a request");
                                    }else if (o.getInt("state") == 3){
                                        ToastUtils.longToast(activity,"user already sent you a request");
                                    }else {
                                        ToastUtils.longToast(activity,"you are already members");
                                    }
                                }
                            } catch (JSONException j) {
                                Log.e("creaete Friend","json e: " + j.getMessage());
                            }
                        }
                    });
                }catch (NullPointerException n){
                    Log.e("creaete Friend","null n: " + n.getMessage());
                }
            }
        });

    }
}
