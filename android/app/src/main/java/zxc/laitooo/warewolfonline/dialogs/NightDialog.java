package zxc.laitooo.warewolfonline.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GameActivity;
import zxc.laitooo.warewolfonline.constants.Integeres;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.objects.player.Player;
import zxc.laitooo.warewolfonline.objects.vote.Vote;
import zxc.laitooo.warewolfonline.objects.vote.VotesAdapter;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.RandomUtil;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 5/13/2020.
 */

public class NightDialog extends Dialog {

    ArrayList<Vote> list;
    ArrayList<Player> playersList;
    Activity activity;
    public VotesAdapter adapter;
    Socket socket;
    User user;
    String emitTag;
    int myRole;
    int lastTarget;
    Context c;
    boolean dismissed;

    public NightDialog(Activity activity, ArrayList<Player> players, Socket s,int myRole,
                       int last) {
        super(activity);
        this.activity = activity;
        this.myRole = myRole;
        lastTarget = last;
        playersList = players;
        dismissed = false;
        list = new ArrayList<>();
        user = new UserData(activity).getUser();
        for (Player p : playersList) {
            if (p.isAlive()) {
                if (p.getId() != user.getId()) {
                    if (!Roles.canRepeat(myRole) && p.getId() == lastTarget){
                        // cant repeat this target
                    }else {
                        list.add(new Vote(p.getId(), p.getName(), p.getPicture(), false,
                                p.getOrder(), p.getRole()));
                    }
                } else {
                    if (Roles.canChooseHimself(myRole)) {
                        if (!Roles.canRepeat(myRole) && p.getId() == lastTarget){
                            // cant repeat himself
                        }else {
                            list.add(new Vote(p.getId(), p.getName(), p.getPicture(), false,
                                    p.getOrder(), p.getRole()));
                        }
                    }
                }
            }
        }
        if (list.size() == 0){
            dismiss();
            return;
        }
        adapter = new VotesAdapter(activity, list, Roles.isWolf(myRole));
        socket = s;
    }

    public void updateConverted(int id){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getId() == id){
                list.get(i).setRole(Roles.WAREWOLF);
                adapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = getContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (myRole){
            case Roles.WITCH:
                witchRole();
                break;

            case Roles.JUNIOR_WAREWOLF:
            case Roles.WAREWOLF:
                warewolfRole();
                break;

            case Roles.LITTLE_GIRL:
                littleGirlRole();
                break;

            case Roles.GUNNER:
                gunnerRole();
                break;

            case Roles.ALPHA_WAREWOLF:
                alphaWarewolfRole();
                break;

            case Roles.MAYOR:
                if (GameActivity.mayorRevealed){
                    dismiss();
                }else {
                    mayorRole();
                }
                break;

            default:
                setContentView(R.layout.dialog_night);
                TextView vote_text = (TextView) findViewById(R.id.text_vote);
                Button vote = (Button) findViewById(R.id.vote);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_votes);
                GridLayoutManager layoutManager = new GridLayoutManager(activity, Integeres.NUM_PLAYERS_PER_ROW);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                setCanceledOnTouchOutside(false);

                emitTag = Roles.RolesTags[myRole];
                vote.setText(Roles.RolesButtons[myRole]);
                vote_text.setText(Roles.RolesTexts[myRole]);

                vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.selectedOrder != -1) {
                            if (myRole != Roles.SEER) {
                                socket.emit(emitTag, user.getId(), adapter.selectedId);
                                GameActivity.playerEvent = true;
                                dismiss();
                                if (!Roles.canRepeat(myRole)) {
                                    GameActivity.pastTarget = adapter.selectedId;
                                }
                            } else {
                                GameActivity.playerEvent = true;
                                int targetId = adapter.selectedId;
                                int role = adapter.selectedRole;
                                String name = adapter.selectedName;

                                setContentView(R.layout.dialog_view_charracter);
                                ImageView imageRole = (ImageView) findViewById(R.id.role);
                                TextView textRole = (TextView) findViewById(R.id.text_role);
                                imageRole.setImageResource(Roles.RolesImages[role]);
                                textRole.setText(name + " is a " + Roles.RolesNames[role]);
                                Button ok = (Button) findViewById(R.id.ok);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dismiss();
                                    }
                                });
                            }
                        } else {
                            ToastUtils.shortToast(getContext(), "Select first");
                        }
                    }
                });
                break;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dismissed) {
                    dismissed = true;
                    if (myRole == Roles.DOCTOR) {
                        if (adapter.selectedId == -1) {
                            GameActivity.pastTarget = -1;
                            socket.emit("doctorSkip", user.getId());
                        }
                    }
                    dismiss();
                }
            }
        }, Integeres.TIME_NIGHT);

    }

    private void warewolfRole() {
        setContentView(R.layout.dialog_night);
        TextView vote_text = (TextView) findViewById(R.id.text_vote);
        final Button vote = (Button) findViewById(R.id.vote);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_votes);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, Integeres.NUM_PLAYERS_PER_ROW);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setCanceledOnTouchOutside(false);

        emitTag = Roles.RolesTags[myRole];
        vote.setText(Roles.RolesButtons[myRole]);
        vote_text.setText(Roles.RolesTexts[myRole]);

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.selectedOrder != -1) {
                    socket.emit(emitTag, user.getId(), adapter.selectedId);
                    if (GameActivity.juniorDead){
                        vote.setText(Roles.RolesButtons[myRole] + " again");
                        adapter.deselectAll();
                        GameActivity.playerEvent = true;
                    }else {
                        dismiss();
                    }
                } else {
                    ToastUtils.shortToast(getContext(), "Select first");
                }
            }
        });
        GameActivity.juniorDead = false;
    }

    private void mayorRole() {
        if (GameActivity.mayorRevealed){
            dismiss();
        }
        setContentView(R.layout.dialog_mayor);
        ImageView icon = (ImageView) findViewById(R.id.icon);
        Button reveal = (Button) findViewById(R.id.reveal);
        Button skip = (Button) findViewById(R.id.skip);
        setCanceledOnTouchOutside(false);
        emitTag = Roles.RolesTags[Roles.MAYOR];
        Picasso.with(c)
                .load(Roles.RolesImages[Roles.MAYOR])
                .into(icon);

        reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.mayorRevealed = true;
                socket.emit(emitTag, user.getId(), adapter.selectedId);
                GameActivity.playerEvent = true;
                dismiss();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void alphaWarewolfRole() {
        setContentView(R.layout.dialog_alpha_wolf);
        View space = findViewById(R.id.space);
        TextView killText = (TextView) findViewById(R.id.text_vote);
        final Button kill = (Button) findViewById(R.id.kill);
        Button convert = (Button) findViewById(R.id.convert);

        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recycler_votes);
        GridLayoutManager layoutManager1 = new GridLayoutManager(activity, Integeres.NUM_PLAYERS_PER_ROW);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setAdapter(adapter);

        setCanceledOnTouchOutside(false);

        emitTag = Roles.RolesTags[Roles.WAREWOLF];
        final String convertTag = Roles.RolesTags[myRole];

        if (GameActivity.usedConversion){
            space.setVisibility(View.GONE);
            convert.setVisibility(View.GONE);
        }

        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.selectedOrder != -1) {
                    socket.emit(emitTag, user.getId(), adapter.selectedId);
                    if (GameActivity.juniorDead){
                        kill.setText("Kill again");
                        adapter.deselectAll();
                        GameActivity.playerEvent = true;
                    }else {
                        dismiss();
                    }
                } else {
                    ToastUtils.shortToast(getContext(), "Select first");
                }
            }
        });

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.selectedOrder != -1) {
                    socket.emit(convertTag, user.getId(), adapter.selectedId);
                    dismiss();
                    GameActivity.usedConversion = true;
                    GameActivity.playerEvent = true;
                } else {
                    ToastUtils.shortToast(getContext(), "Select first");
                }
            }
        });
        GameActivity.juniorDead = false;
    }

    private void gunnerRole() {
        String text = "";
        if (GameActivity.numBullets == 0) {
            dismissed = true;
            dismiss();
        }else if (GameActivity.numBullets == 1){
            text = "Who do you want to shoot with your last bullet";
        }else {
            text = "Who do you want to shoot with your first bullet";
        }

        setContentView(R.layout.dialog_night);
        TextView killText = (TextView) findViewById(R.id.text_vote);
        Button kill = (Button) findViewById(R.id.vote);
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recycler_votes);
        GridLayoutManager layoutManager1 = new GridLayoutManager(activity, Integeres.NUM_PLAYERS_PER_ROW);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setAdapter(adapter);

        setCanceledOnTouchOutside(false);

        emitTag = Roles.RolesTags[myRole];
        kill.setText(Roles.RolesButtons[myRole]);
        killText.setText(text);

        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.selectedOrder != -1) {
                    socket.emit(emitTag, user.getId(), adapter.selectedId);
                    dismiss();
                    GameActivity.playerEvent = true;
                } else {
                    ToastUtils.shortToast(getContext(), "Select first");
                }
            }
        });
    }

    private void littleGirlRole() {
        GameActivity.playerEvent = true;
        if (RandomUtil.didGirlSeeWolf()){
            setContentView(R.layout.fragment_event);
            TextView text = (TextView)findViewById(R.id.text_event);
            ImageView imageView = (ImageView)findViewById(R.id.image_event);

            Player player = RandomUtil.showWarewolf(playersList);

            c = getContext();
            text.setText(player.getName() + " is a " + Roles.RolesNames[player.getRole()]);
            Picasso.with(c)
                    .load(Roles.RolesImages[player.getRole()])
                    .into(imageView);
        } else {
            setContentView(R.layout.fragment_event);
            TextView text = (TextView)findViewById(R.id.text_event);
            ImageView imageView = (ImageView)findViewById(R.id.image_event);

            c = getContext();
            text.setText("You saw nothing");
            Picasso.with(c)
                    .load(R.drawable.error)
                    .into(imageView);
        }
    }

    private void witchRole(){
        if (GameActivity.usedPoison && GameActivity.usedExir){
            dismiss();
        }

        setContentView(R.layout.dialog_witch);
        final Button use = (Button) findViewById(R.id.vote);
        final RelativeLayout p = (RelativeLayout) findViewById(R.id.poison);
        final RelativeLayout e = (RelativeLayout) findViewById(R.id.exir);
        final CircleImageView ip = (CircleImageView) findViewById(R.id.icon_poison);
        final CircleImageView ie = (CircleImageView) findViewById(R.id.icon_exir);
        View v = findViewById(R.id.view_mid);

        final boolean[] selectP = {false};
        final boolean[] selectE = {false};

        if (GameActivity.usedExir){
            if (!GameActivity.usedPoison) {
                e.setVisibility(View.GONE);
                v.setVisibility(View.GONE);
            }
        }else {
            if (GameActivity.usedPoison) {
                p.setVisibility(View.GONE);
                v.setVisibility(View.GONE);
            }
        }

        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectP[0]){
                    selectP[0] = false;
                    selectE[0] = false;
                    ip.setBorderColor(ContextCompat.getColor(c,R.color.color2));
                    ie.setBorderColor(ContextCompat.getColor(c,R.color.color2));
                }else {
                    selectP[0] = true;
                    selectE[0] = false;
                    ip.setBorderColor(ContextCompat.getColor(c,R.color.color1));
                    ie.setBorderColor(ContextCompat.getColor(c,R.color.color2));
                }
            }
        });

        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectE[0]){
                    selectE[0] = false;
                    selectP[0] = false;
                    ie.setBorderColor(ContextCompat.getColor(c,R.color.color2));
                    ip.setBorderColor(ContextCompat.getColor(c,R.color.color2));
                }else {
                    selectE[0] = true;
                    selectP[0] = false;
                    ie.setBorderColor(ContextCompat.getColor(c,R.color.color1));
                    ip.setBorderColor(ContextCompat.getColor(c,R.color.color2));
                }
            }
        });

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.playerEvent = true;
                if (selectE[0]){
                    socket.emit("witchExir",user.getId());
                    GameActivity.usedExir = true;
                    dismiss();
                }else {
                    if (selectP[0]) {
                        usePoison();
                    }else {
                        ToastUtils.shortToast(c,"Pleas choose first");
                    }
                }
            }
        });
    }

    private void usePoison() {
        setContentView(R.layout.dialog_night);
        TextView vote_text = (TextView) findViewById(R.id.text_vote);
        Button vote = (Button) findViewById(R.id.vote);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_votes);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, Integeres.NUM_PLAYERS_PER_ROW);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        setCanceledOnTouchOutside(false);

        emitTag = Roles.RolesTags[myRole];
        vote.setText(Roles.RolesButtons[myRole]);
        vote_text.setText(Roles.RolesTexts[myRole]);

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.selectedOrder != -1) {
                    socket.emit(emitTag, user.getId(), adapter.selectedId);
                    GameActivity.usedPoison = true;
                    dismiss();
                    if (!Roles.canRepeat(myRole)) {
                        GameActivity.pastTarget = adapter.selectedId;
                    }
                } else {
                    ToastUtils.shortToast(getContext(), "Select first");
                }
            }
        });
    }

    public void destroy(){
        if (!dismissed) {
            dismissed = true;
            dismiss();
            if (myRole == Roles.DOCTOR){
                if (adapter.selectedId == -1){
                    GameActivity.pastTarget = -1;
                    socket.emit("doctorSkip",user.getId());
                }
            }
        }
    }

}
