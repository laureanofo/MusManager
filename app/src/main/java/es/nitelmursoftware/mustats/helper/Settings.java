package es.nitelmursoftware.mustats.helper;

import android.content.SharedPreferences;

public class Settings {
    public static String ARG_PLAYER = "player";
    public static String ARG_GAME = "game";
    public static String ARG_ME = "me";
    public static String PARAM_MINIMUMGAMES = "minimumgames";
    public static String PARAM_LASTGAMES = "lastgames";
    public static String PARAM_GAMEAVERAGE = "gameaverage";
    public static String PARAM_GAMEABSOLUTE = "gameabsolute";
    public static String PARAM_GAMETOTAL = "gametotal";
    public static String PARAM_LASTPLAYERS = "lastplayers";
    public static String PARAM_DAYSABOSULTE = "daysabsolute";
    public static String PARAM_DAYSPARTIAL = "dayspartial";
    public static String PARAM_MINIMUMPERCENTAGE = "minimumporcentage";
    public static String PARAM_MINIMUMPLAYINGPERCENTAGEDAYS = "minimumporcentagedays";
    public static String PARAM_MINIMUMPLAYINGDAYS = "minimumdays";

    public static int ORDER_NAME = 1;
    public static int ORDER_ALIAS = 2;
    public static int ORDER_RANKING = 3;

    public static String URL = "musstats.nitelmursoftware.es";
    public static String DB_PATH = "/Download/";
    public static String DB_NAME = "mus.sqlite";

    // Dropbox
    public static String APP_KEY = "yipg9f9a9ejjow2";
    public static String APP_SECRET = "zuj0vdknxlb4664";
    public static final int REQUEST_LINK_TO_DBX = 0;
    public static final int DBX_CHOOSER_REQUEST = 1;

    // Preferences


    public static int DOWNLOADDELAY1 = 2000;
    public static int DOWNLOADDELAY2 = 2000;

    public static SharedPreferences pref;

}
