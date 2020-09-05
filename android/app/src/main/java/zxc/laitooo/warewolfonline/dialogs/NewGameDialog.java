package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.MainActivity;
import zxc.laitooo.warewolfonline.objects.game.Game;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ObjectToJson;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 5/14/2020.
 */

public class NewGameDialog extends Dialog {

    Activity activity;
    int numPlayers;

    public NewGameDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_game_options);
        SeekBar seekBar = (SeekBar) findViewById(R.id.num_picker);
        seekBar.setMax(8);
        seekBar.setProgress(0);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.color1)
                , PorterDuff.Mode.SRC_IN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            seekBar.getThumb().setColorFilter(ContextCompat.getColor(activity,R.color.color1),
                    PorterDuff.Mode.SRC_IN);
        }

        final TextView num = (TextView)findViewById(R.id.num_player);
        numPlayers = 2;
        num.setText("2");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numPlayers = progress + 2;
                num.setText("" + numPlayers);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Button ok = (Button)findViewById(R.id.create);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData userData = new UserData(activity);
                Game game = new Game(0,0,numPlayers,userData.getUser().getId(),userData.getUser().getUsername());
                if (MainActivity.mainSocket.connected()) {
                    MainActivity.mainSocket.emit("createGame", ObjectToJson.gameToJson(game));
                    dismiss();
                }else {
                    ToastUtils.networkError(activity);
                }
            }
        });
    }
}
