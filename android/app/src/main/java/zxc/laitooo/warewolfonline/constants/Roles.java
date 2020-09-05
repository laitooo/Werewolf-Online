package zxc.laitooo.warewolfonline.constants;

import zxc.laitooo.warewolfonline.R;

/**
 * Created by Laitooo San on 5/11/2020.
 */

public class Roles {

    public static final int VILLAGERS_TEAM = 1;
    public static final int WAREWOLFS_TEAM = 2;
    public static final int SERIAL_KILLERS_TEAM = 3;
    public static final int TANNERS_TEAM = 4;
    public static final int OTHERS = 5;


    public static final int ERROR = 0;
    public static final int WAREWOLF = 1;
    public static final int VILLAGER = 2;
    public static final int DOCTOR = 3;
    public static final int HARLOT = 4;
    public static final int SEER = 5;
    public static final int HUNTER = 6;
    public static final int WITCH = 7;
    public static final int LITTLE_GIRL = 8;
    public static final int GUNNER = 9;
    public static final int SERIAL_KILLER = 10;
    public static final int ALPHA_WAREWOLF = 11;
    public static final int TANNER = 12;
    public static final int MAYOR = 13;
    public static final int MAD_SCIENTIST = 14;
    public static final int JUNIOR_WAREWOLF = 15;

    public static final String[] TEAMS_WIN_TEXT = {"Everyone is dead", "The Villagers won the game",
            "The Warewolfs won the game","The serial killer Won","The tanner won","Others won"};

    public static final int[] RolesList = {ERROR,WAREWOLF,VILLAGER,DOCTOR,HARLOT,SEER,HUNTER,
            WITCH,LITTLE_GIRL,GUNNER,SERIAL_KILLER,ALPHA_WAREWOLF,TANNER,MAYOR,MAD_SCIENTIST,
            JUNIOR_WAREWOLF};
    public static final int[] RolesImages = {R.drawable.error,R.drawable.role1,R.drawable.role2,
    R.drawable.icon6,R.drawable.icon9,R.drawable.icon5,R.drawable.icon8,R.drawable.icon10
            ,R.drawable.icon7,R.drawable.icon4,R.drawable.icon3,R.drawable.icon1,R.drawable.icon2,
            R.drawable.icon6,R.drawable.icon9,R.drawable.role1};
    public static final String[] RolesNames = {"Error","Warewolf","Villeger","Doctor","Harlot",
            "Seer","Hunter","Witch","Little girl","Gunner","Serial killer","Aplha warewolf",
            "Tanner","The Mayor","The mad scientist","Junior warewolf"};
    public static final String[] RolesButtons = {"Error","Kill","Error","Heal","Sleep","See",
            "Select","Kill","Error","Shoot","Kill","convert","Error","Reveal","Error","Kill"};
    public static final String[] RolesTexts = {"Error","Who do you want to kill","Error",
            "Who do you want to heal","Who do you want to sleep with","Who do you want to see",
            "Who do you want to select","Who do you want to kill","Error","Who do you want to kill"
            ,"Who do you want to kill","Who do you want to convert","Error",
            "Do you want to reveal yourself?","Error","Who do you want to kill"};
    public static final String[] RolesTags = {"Error","warewolfKill","Error","doctorHeal",
            "harlotSleep","seerSee","hunterHunt","witchPoison","girlSee","gunnerShoot","serialKill",
            "warewolfConvert","Error","revealMayor","Error","warewolfKill"};


    public static boolean hasAbility(int role){
        switch (role){
            case VILLAGER:
            case TANNER:
            case MAD_SCIENTIST:
                return false;
            default:
                return true;
        }
    }

    public static boolean canChooseHimself(int role){
        switch (role){
            case DOCTOR:
                return true;
            default:
                return false;
        }
    }

    public static int getTeam(int role){
        switch (role){
            case WAREWOLF:
            case ALPHA_WAREWOLF:
            case JUNIOR_WAREWOLF:
                return WAREWOLFS_TEAM;
            case SERIAL_KILLER:
                return SERIAL_KILLERS_TEAM;
            case TANNER:
                return TANNERS_TEAM;
            default:
                return VILLAGERS_TEAM;
        }
    }

    public static boolean canRepeat(int role){
        switch (role){
            case DOCTOR:
                return false;
            default:
                return true;
        }
    }

    public static boolean isWolf(int role){
        switch (role){
            case WAREWOLF:
            case ALPHA_WAREWOLF:
            case JUNIOR_WAREWOLF:
                return true;
            default:
                return false;
        }
    }

}
