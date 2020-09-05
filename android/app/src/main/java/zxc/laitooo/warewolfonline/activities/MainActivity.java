package zxc.laitooo.warewolfonline.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.constants.Resources;
import zxc.laitooo.warewolfonline.main.MainPagerAdapter;
import zxc.laitooo.warewolfonline.user.AppData;
import zxc.laitooo.warewolfonline.user.Data;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.TimeUtils;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 2/28/2020.
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static TextView numPlayers;
    private Context c;
    public static io.socket.client.Socket mainSocket;

    TextView connectionStatus;
    boolean opened;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c = this;
        // TODO: Check if going games are finished

        connectionStatus = (TextView) findViewById(R.id.connection_status);
        ImageButton toogle = (ImageButton)findViewById(R.id.toogle);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs_main);
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager_main);
        tabLayout.setupWithViewPager(viewPager);

        numPlayers = (TextView)findViewById(R.id.num_online);
        //viewPager.setOffscreenPageLimit(1);
        printTimeZone();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);

        for (int j = 0; j < 3; j++) {
            View view = LayoutInflater.from(c)
                    .inflate(R.layout.custom_tab,null);
            tabLayout.getTabAt(j).setCustomView(initiateTabView(view,j,j == 0));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(updateView(tab.getCustomView(),true));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(updateView(tab.getCustomView(),false));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        User user = new UserData(this).getUser();

        View v = navigationView.getHeaderView(0);
        ImageView image = (ImageView)v.findViewById(R.id.imageView);
        final TextView name = (TextView)v.findViewById(R.id.name);
        Picasso.with(this)
                .load(user.getPic())
                .into(image);
        name.setText(user.getUsername() + "(id:" + user.getId() + ")");

        viewPager.setCurrentItem(1);
        opened = true;

        try {
            mainSocket = IO.socket(Links.MAIN_SOCKET);
            mainSocket.connect();
            mainSocket.emit("join",user.getUsername());

            mainSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connected");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                            if (opened) {
                                opened = false;
                                connectionStatus.animate()
                                        .translationY(0)
                                        .alpha(0.0f)
                                        .setDuration(500)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                connectionStatus.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    });
                }
            });

            mainSocket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("Connecting");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.yellow));
                            if (!opened) {
                                opened = true;
                                connectionStatus.animate()
                                        .translationY(0)
                                        .alpha(1.0f)
                                        .setDuration(500)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                connectionStatus.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        }
                    });
                }
            });

            mainSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectionStatus.setText("No Connection");
                            connectionStatus.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.red));
                            if (!opened) {
                                opened = true;
                                connectionStatus.animate()
                                        .translationY(0)
                                        .alpha(1.0f)
                                        .setDuration(500)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                connectionStatus.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            Log.e("main socket","uri e: " + e.getMessage());
        }

    }

    private void printTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        Log.e("Timezone","z:" + tz.getDisplayName());
        TimeUtils.printTimeZone();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.rules) {
            Intent intent = new Intent(this,GameRulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            AppData data = new AppData(this);
            data.setData(new Data(false,false));
            UserData userData = new UserData(this);
            userData.clearUser();
            ToastUtils.longToast(this,"Logged out");
            Intent intent = new Intent(this,LoginActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.contact) {
            /*Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","killuasan25@gmail.com", null));
            email.putExtra(Intent.EXTRA_SUBJECT, "Warewolf app");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));*/
            Intent intent = new Intent(Intent.ACTION_SENDTO);//common intent
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, "Warewolf app");
            //intent.putExtra(Intent.EXTRA_TEXT, "E-mail body" );
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"killuasan25@gmail.com"});
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        } else if (id == R.id.review){
            Intent intent = new Intent(this,ReviewActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private View initiateTabView(View v, int j,boolean selected) {
        ImageButton icon = (ImageButton) v.findViewById(R.id.icon);

        icon.setImageResource(Resources.MAIN_ACTIVITY_ICONS[j]);

        if (selected){
            icon.setColorFilter(ContextCompat.getColor(c, R.color.color1),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }else {
            icon.setColorFilter(ContextCompat.getColor(c, R.color.color6),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        icon.setClickable(false);

        return v;
    }

    private View updateView(View v, boolean selected){
        ImageButton icon = (ImageButton)v.findViewById(R.id.icon);

        if (selected){
            icon.setColorFilter(ContextCompat.getColor(c, R.color.color1),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }else {
            icon.setColorFilter(ContextCompat.getColor(c, R.color.color6),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        return v;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mainSocket.disconnect();
            mainSocket.close();
        }catch (Exception e){
            Log.e("onDestroy","e: " + e.getMessage());
        }
    }
}
