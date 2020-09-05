package zxc.laitooo.warewolfonline.events;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class EventsPagerAdapter extends FragmentStatePagerAdapter{

    ArrayList<Event> list;

    public EventsPagerAdapter(FragmentManager fm,ArrayList<Event> events) {
        super(fm);
        list = events;
    }

    public EventsPagerAdapter(FragmentManager fm) {
        super(fm);
        list = new ArrayList<>();
    }


    @Override
    public Fragment getItem(int position) {
        if (list.size() == 0) {
            return new NoEventsFragment();
        } else {
            return EventFragment.newInstance(list.get(position));
        }
    }

    @Override
    public int getCount() {
        if (list.size() == 0){
            return 1;
        }else {
            return list.size();
        }
    }
}
