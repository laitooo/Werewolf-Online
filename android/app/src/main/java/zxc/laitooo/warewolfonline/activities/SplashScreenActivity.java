package zxc.laitooo.warewolfonline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Integeres;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler = new Handler();
    AppData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        data = new AppData(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (data.getData().isLogged()){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, Integeres.SPLASH_SCREEN_DELAY);
    }
}
