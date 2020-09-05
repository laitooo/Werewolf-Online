package zxc.laitooo.warewolfonline.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsListFragment();
            case 1:
                return new GamesListFragment();
            case 2:
                return new GroupsListFragment();
            default:
                return new GamesListFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
        /*switch (position){
            case 0:
                return "Games";
            case 1:
                return "Chats";
            case 2:
                return "Groups";
            default:
                return "Games";
        }*/
    }
}
