package zxc.laitooo.warewolfonline.utils;

import java.util.ArrayList;
import java.util.Random;

import zxc.laitooo.warewolfonline.constants.Roles;
import zxc.laitooo.warewolfonline.objects.player.Player;

/**
 * Created by Laitooo San on 7/11/2020.
 */

public class RandomUtil {

    public static boolean didGirlSeeWolf(){
        Random random = new Random();
        return random.nextInt(3) == 2;
    }

    public static Player showWarewolf(ArrayList<Player> players){
        ArrayList<Player> wolfs = new ArrayList<>();
        for (Player p : players) {
            if (p.getRole() == Roles.WAREWOLF && p.isAlive()) {
                wolfs.add(p);
            }
        }
        Random random = new Random();
        return players.get(random.nextInt(wolfs.size()));
    }
}
