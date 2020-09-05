package zxc.laitooo.warewolfonline.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rd.PageIndicatorView;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.GameActivity;
import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.events.Event;
import zxc.laitooo.warewolfonline.events.EventsPagerAdapter;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class EventsDialog extends DialogFragment {

    EventsPagerAdapter adapter;
    ArrayList<Event> list;
    private boolean dismissed,noEvents;

    @SuppressLint("ValidFragment")
    public EventsDialog(ArrayList<Event> events) {
        list = events;
        noEvents = false;
    }

    public EventsDialog() {
        noEvents = true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }catch (NullPointerException n){
            // null
        }
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dismissed = false;
        if (!noEvents) {
            adapter = new EventsPagerAdapter(getChildFragmentManager(), list);
        } else {
            adapter = new EventsPagerAdapter(getChildFragmentManager());
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_events,container,false);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.my_pager);
        PageIndicatorView pageIndicatorView = (PageIndicatorView)v.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(viewPager);
        viewPager.setAdapter(adapter);
        if (noEvents){
            pageIndicatorView.setVisibility(View.GONE);
        }

        return v;
    }

    public void destroy(){
        if (!dismissed) {
            dismissed = true;
            dismiss();
        }
    }
}
