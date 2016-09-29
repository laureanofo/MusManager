package es.nitelmursoftware.mustats.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import es.nitelmursoftware.mustats.helper.Settings;

public class DBMMus {
    private final String dbpath;

    public DBMMus(String dbpath) {
        this.dbpath = dbpath;
    }

    public SQLiteDatabase open(int mode) {
        SQLiteDatabase database = null;

        try {
            database = SQLiteDatabase.openDatabase(dbpath, null, mode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return database;
    }

    public Parameters getParameter(String parametername) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c = null;
        Parameters result = null;

        try {
            String SQL = "SELECT * FROM parameters";

            if (parametername != null && parametername.equals(""))
                SQL = SQL + " WHERE name LIKE '" + parametername + "'";

            c = database.rawQuery(SQL, null);
            c.moveToFirst();
            result = Parameters.getParameters(c);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        if (c != null)
            c.close();

        if (database != null)
            database.close();

        return result;
    }

    public Player getPlayer(long id) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        Player temp = null;

        try {
            String SQL = "SELECT * FROM players WHERE _id LIKE ?";
            String[] where = {id + ""};

            c_games = database.rawQuery(SQL, where);
            temp = Player.getPlayer(c_games);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();
        if (c_games != null)
            c_games.close();

        return temp;
    }

    public long[] getPlayers(int order) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        long[] temp = null;

        try {
            String SQL = "SELECT *, positives - negatives AS difference FROM players";

            if (order == Settings.ORDER_RANKING)
                SQL = SQL
                        + " ORDER BY lastranking DESC,positives DESC, difference DESC, ranking DESC";
            else if (order == Settings.ORDER_NAME)
                SQL = SQL + " ORDER BY name ASC";
            else if (order == Settings.ORDER_ALIAS)
                SQL = SQL + " ORDER BY alias ASC";

            c_games = database.rawQuery(SQL, null);
            temp = Player.getPlayers(c_games);
        } catch (Exception e) {
        }

        if (c_games != null)
            c_games.close();
        if (database != null)
            database.close();

        return temp;
    }

    public List<Player> getPlayerList(int order) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        List<Player> temp = null;

        try {
            String SQL = "SELECT *, positives - negatives AS difference FROM players";

            if (order == Settings.ORDER_RANKING)
                SQL = SQL
                        + " ORDER BY lastranking DESC,positives DESC, difference DESC, ranking DESC";
            else if (order == Settings.ORDER_NAME)
                SQL = SQL + " ORDER BY name ASC";
            else if (order == Settings.ORDER_ALIAS)
                SQL = SQL + " ORDER BY alias ASC";

            c_games = database.rawQuery(SQL, null);
            temp = Player.getPlayerList(c_games);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c_games != null)
            c_games.close();
        if (database != null)
            database.close();

        return temp;
    }

    public Game getGame(long id) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        Game temp = null;

        try {
            String SQL = "SELECT * FROM games WHERE _id LIKE ?";
            String[] where = {id + ""};

            c_games = database.rawQuery(SQL, where);
            temp = Game.getGame(c_games);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();
        if (c_games != null)
            c_games.close();

        return temp;
    }

    public long[] getGames(int lastday, String order) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        long[] temp = null;

        try {
            String SQL = "SELECT * FROM games";
            if (lastday > 0)
                SQL = SQL + " WHERE date>=" + lastday;

            SQL = SQL + " ORDER BY date " + order + ", _id " + order;

            c_games = database.rawQuery(SQL, null);
            temp = Player.getPlayers(c_games);
        } catch (Exception e) {
        }

        if (c_games != null)
            c_games.close();
        if (database != null)
            database.close();

        return temp;
    }

    public List<Game> getGameList(int lastday, long playerid,
                                  String order) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        List<Game> temp = null;

        try {
            String SQL = "SELECT * FROM games";
            if (lastday > 0)
                SQL = SQL + " WHERE date>=" + lastday;
            if (playerid >= 0) {
                if (lastday <= 0)
                    SQL = SQL + " WHERE";
                else
                    SQL = SQL + " AND";

                SQL = SQL + " (player11=" + playerid;
                SQL = SQL + " OR player12=" + playerid;
                SQL = SQL + " OR player21=" + playerid;
                SQL = SQL + " OR player22=" + playerid + ")";
            }

            SQL = SQL + " ORDER BY date " + order + ", _id " + order;

            c_games = database.rawQuery(SQL, null);
            temp = Game.getGameList(c_games);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c_games != null)
            c_games.close();
        if (database != null)
            database.close();

        return temp;
    }

    public List<Game> getGameListMonth(Calendar month) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        List<Game> temp = null;

        String m = (month.get(Calendar.MONTH) + 1) + "";

        String month_string = month.get(Calendar.YEAR) + "" + (m.length() == 1 ? "0" + m : m);

        try {
            String SQL = "SELECT * FROM games";
            SQL = SQL + " WHERE date LIKE '%" + month_string + "%'";

            c_games = database.rawQuery(SQL, null);
            temp = Game.getGameList(c_games);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c_games != null)
            c_games.close();
        if (database != null)
            database.close();

        return temp;
    }

    public List<Game> getGamesByPlayerList(long id, String order) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        List<Game> temp = null;

        try {
            String SQL = "SELECT * FROM games WHERE player11 LIKE ? OR player12 LIKE ? OR player21 LIKE ? OR player22 LIKE ?  ORDER BY date "
                    + order + ", _id " + order;
            String[] where = {id + "", id + "", id + "", id + ""};

            c_games = database.rawQuery(SQL, where);
            temp = Game.getGameList(c_games);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();
        if (c_games != null)
            c_games.close();

        return temp;
    }

    public long[] getGamesByPlayer(long id, String order) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);
        Cursor c_games = null;
        long[] temp = null;

        try {
            String SQL = "SELECT * FROM games WHERE player11 LIKE ? OR player12 LIKE ? OR player21 LIKE ? OR player22 LIKE ?  ORDER BY date "
                    + order + ", _id " + order;
            String[] where = {id + "", id + "", id + "", id + ""};

            c_games = database.rawQuery(SQL, where);
            temp = Game.getGames(c_games);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();
        if (c_games != null)
            c_games.close();

        return temp;
    }

    public long addPlayer(String name, String alias, byte[] picture) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            ContentValues initialValues = new ContentValues();
            if (name != null)
                initialValues.put("name", name);
            if (alias != null)
                initialValues.put("alias", alias);
            if (picture != null)
                initialValues.put("picture", picture);
            result = database.insert("players", null, initialValues);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();

        return result;
    }

    public long updatePlayer(ContentValues initialValues, long playerId) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            result = database.update("players", initialValues, "_id=?",
                    new String[]{playerId + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (database != null)
            database.close();

        return result;
    }

    public long addParameter(String name, String value) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("name", name);
            initialValues.put("value", value);
            result = database.insert("parameters", null, initialValues);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();

        return result;
    }

    public long addGame(ContentValues initialValues) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            result = database.insert("games", null, initialValues);

            recalculate();
        } catch (Exception e) {
        }

        if (database != null)
            database.close();

        return result;
    }

    public long deleteGame(long gameId) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            result = database.delete("games", "_id = ?", new String[]{gameId
                    + ""});

            recalculate();
        } catch (Exception e) {
        }

        if (database != null)
            database.close();

        return result;
    }

    public long updateParameters(String parametername, String value) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("value", value);
            result = database.update("parameters", initialValues, "name=?",
                    new String[]{parametername});
            if (result == 0) {
                initialValues.put("name", parametername);
                result = database.insert("parameters", null, initialValues);
            }
        } catch (Exception e) {
        }

        if (database != null)
            database.close();

        return result;
    }

    public void updatePlayers(long playerId) {
        try {
            List<Player> players;
            if (playerId < 0)
                players = getPlayerList(Settings.ORDER_RANKING);
            else {
                players = new ArrayList<Player>();
                players.add(getPlayer(playerId));
            }


            int daysBefore = 30;
            try {
                daysBefore = Integer.parseInt(getParameter(Settings.PARAM_DAYSPARTIAL).map.get(Settings.PARAM_DAYSPARTIAL));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int lastdayPartial = getLastday(daysBefore);

            for (Player player : players) {
                daysBefore = 365;
                try {
                    daysBefore = Integer.parseInt(getParameter(Settings.PARAM_DAYSABOSULTE).map.get(Settings.PARAM_DAYSABOSULTE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int lastdayAbsolute = getLastday(daysBefore);

                List<Game> games = getGameList(lastdayAbsolute, player.id, "ASC");
                //List<Game> games = getGamesByPlayerList(player.id, "ASC");

                ContentValues initialValues = new ContentValues();
                List<Long> winnerList = new ArrayList<Long>();
                List<Long> looserList = new ArrayList<Long>();

                // reset player parameters
                initialValues.put("lastgames", 0);
                initialValues.put("lastwins", 0);
                initialValues.put("worstmate", 0);
                initialValues.put("bestmate", 0);
                initialValues.put("games", 0);
                initialValues.put("positives", 0);
                initialValues.put("negatives", 0);
                initialValues.put("rafale", 0);
                initialValues.put("rafalewins", 0);
                initialValues.put("rafalelosts", 0);
                initialValues.put("wins", 0);
                initialValues.put("ranking", 0);
                initialValues.put("lastranking", 0);
                initialValues.put("days", 0);
                initialValues.put("lastdays", 0);
                updatePlayer(initialValues, player.id);

                initialValues = new ContentValues();

                if (games != null && games.size() > 0) {
                    initialValues.put("games", games.size());

                    int wins = 0;
                    int lastWins = 0;
                    int lastgames = 0;
                    int positives = 0;
                    int negatives = 0;
                    int rafale_wins = 0;
                    int rafale_losts = 0;
                    int rafale_counter = 0;
                    boolean rafale_winning = false;
                    boolean rafale_lossing = false;
                    boolean firstgame = true;

                    Set<String> days = new HashSet<String>();
                    Set<String> lastdays = new HashSet<String>();
                    for (Game game : games) {
                        days.add(game.date);

                        int playerPosition;

                        if (game.player11 == player.id)
                            playerPosition = 1;
                        else if (game.player12 == player.id)
                            playerPosition = 2;
                        else if (game.player21 == player.id)
                            playerPosition = 3;
                        else if (game.player22 == player.id)
                            playerPosition = 4;
                        else
                            continue;

                        if (Integer.parseInt(game.date) >= lastdayPartial) {
                            lastgames++;
                            lastdays.add(game.date);
                        }

                        if (playerPosition <= 2) {
                            positives += game.result1;
                            negatives += game.result2;
                            if (firstgame) {
                                if (game.result1 > game.result2)
                                    rafale_winning = true;
                                else
                                    rafale_lossing = true;
                                firstgame = false;
                            }

                            if (game.result1 > game.result2) {
                                // couple 1 won

                                wins++;

                                if (!rafale_winning) {
                                    rafale_winning = true;
                                    rafale_lossing = false;
                                    if (rafale_counter > rafale_losts)
                                        rafale_losts = rafale_counter;
                                    rafale_counter = 0;
                                }

                                if (Integer.parseInt(game.date) >= lastdayPartial)
                                    lastWins++;

                                if (playerPosition == 1)
                                    winnerList.add(game.player12);
                                else
                                    winnerList.add(game.player11);
                            } else {
                                if (!rafale_lossing) {
                                    rafale_winning = false;
                                    rafale_lossing = true;
                                    if (rafale_counter > rafale_wins)
                                        rafale_wins = rafale_counter;
                                    rafale_counter = 0;
                                }

                                if (playerPosition == 1)
                                    looserList.add(game.player12);
                                else
                                    looserList.add(game.player11);
                            }
                        } else {
                            positives += game.result2;
                            negatives += game.result1;

                            if (firstgame) {
                                if (game.result1 > game.result2)
                                    rafale_winning = true;
                                else
                                    rafale_lossing = true;
                                firstgame = false;
                            }

                            if (game.result1 < game.result2) {
                                // couple 2 won
                                wins++;

                                if (!rafale_winning) {
                                    rafale_winning = true;
                                    rafale_lossing = false;
                                    if (rafale_counter > rafale_losts)
                                        rafale_losts = rafale_counter;
                                    rafale_counter = 0;
                                }

                                if (Integer.parseInt(game.date) >= lastdayPartial)
                                    lastWins++;

                                if (playerPosition == 3)
                                    winnerList.add(game.player22);
                                else
                                    winnerList.add(game.player21);
                            } else {
                                if (!rafale_lossing) {
                                    rafale_winning = false;
                                    rafale_lossing = true;
                                    if (rafale_counter > rafale_wins)
                                        rafale_wins = rafale_counter;
                                    rafale_counter = 0;
                                }

                                if (playerPosition == 3)
                                    looserList.add(game.player22);
                                else
                                    looserList.add(game.player21);
                            }
                        }
                        rafale_counter++;
                    }

                    initialValues.put("wins", wins);
                    initialValues.put("ranking",
                            (double) wins / (double) games.size());
                    initialValues.put("lastwins", lastWins);
                    initialValues.put(Settings.PARAM_LASTGAMES, lastgames);

                    initialValues.put("lastranking", getPlayerRate(lastWins, lastgames - lastWins, lastdays.size(), -1));

                    initialValues.put("positives", positives);
                    initialValues.put("negatives", negatives);

                    if (rafale_winning)
                        initialValues.put("rafale", rafale_counter);
                    else
                        initialValues.put("rafale", rafale_counter * -1);
                    initialValues.put("rafalewins", rafale_wins);
                    initialValues.put("rafalelosts", rafale_losts);
                    initialValues.put("days", days.size());
                    initialValues.put("lastdays", lastdays.size());

                    // best and worst players
                    long playerIds[] = getPlayers(Settings.ORDER_NAME);
                    long bestCouple = 0;
                    double bestRatio = 0;
                    long worstCouple = 0;
                    double worstRatio = 0;
                    for (long id : playerIds) {
                        if (id == player.id || id == 0)
                            continue;

                        double bestCount = Collections.frequency(winnerList, id);
                        double worstCount = Collections.frequency(looserList, id);
                        double ratioPlayer = 0;

                        if ((bestCount + worstCount) <= 0) continue;

                        ratioPlayer = bestCount + Math.floor(bestCount / 5) - worstCount - Math.floor(worstCount / 10);

                        System.out.println(player.id + "-" + id + " W/L: " + bestCount + "/" + worstCount + " R: " + ratioPlayer);

                        if (ratioPlayer > bestRatio) {
                            bestCouple = id;
                            bestRatio = ratioPlayer;
                        }
                        if (ratioPlayer < worstRatio) {
                            worstCouple = id;
                            worstRatio = ratioPlayer;
                        }
                    }

                    initialValues.put("bestmate", bestCouple);
                    initialValues.put("worstmate", worstCouple);

                    updatePlayer(initialValues, player.id);
                }
            }

        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    public long getBestPlayer(Calendar month) {
        long winner = -1;

        Parameters parameters = getParameter(null);

        try {
            List<Game> games = getGameListMonth(month);

            List<Long> winnerList = new ArrayList<Long>();
            List<Long> looserList = new ArrayList<Long>();
            Set<Long> players = new HashSet<Long>();
            Set<String> days = new HashSet<String>();

            if (games != null && games.size() > 0) {
                for (Game game : games) {
                    int playerPosition;

                    days.add(game.date);

                    players.add(game.player11);
                    players.add(game.player12);
                    players.add(game.player21);
                    players.add(game.player22);

                    if (game.result1 > game.result2) {
                        winnerList.add(game.player11);
                        winnerList.add(game.player12);
                        looserList.add(game.player21);
                        looserList.add(game.player22);
                    } else {
                        looserList.add(game.player11);
                        looserList.add(game.player12);
                        winnerList.add(game.player21);
                        winnerList.add(game.player22);
                    }
                }

                long[] playersId = getPlayers(Settings.ORDER_RANKING);
                double ranking = 0;

                int percentage = Integer
                        .parseInt(getParameter(Settings.PARAM_MINIMUMPERCENTAGE).map
                                .get(Settings.PARAM_MINIMUMPERCENTAGE));
                double minimumgames = (double) games.size() * percentage / 100;

                for (long id : playersId) {
                    int wins = Collections.frequency(winnerList, id);
                    int losts = Collections.frequency(looserList, id);

                    Player p = getPlayer(id);

                    double rate = getPlayerRate(wins, losts, days.size(), minimumgames);

                    if (rate > ranking) {
                        ranking = rate;
                        winner = id;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        return winner;
    }

    private double getPlayerRate(double wins, double losts, int playingdays, double minimumgames) {
        double rate = 0;

        Parameters parameters = getParameter(null);

        if (minimumgames < 0)
            minimumgames = Double.parseDouble(parameters.map
                    .get(Settings.PARAM_MINIMUMGAMES));

        if (wins + losts > 0)
            if (wins + losts < minimumgames)
                rate = (double) wins / minimumgames;
            else
                rate = (double) wins / (wins + losts);

        double minimumplayingdays = Double.parseDouble(parameters.map
                .get(Settings.PARAM_MINIMUMPLAYINGDAYS));

        if (playingdays < minimumplayingdays)
            rate = rate * playingdays / minimumgames;

        return rate;
    }

    public void updateParameters() {
        try {
            int days = 30;
            try {
                days = Integer.parseInt(getParameter(Settings.PARAM_DAYSPARTIAL).map.get(Settings.PARAM_DAYSPARTIAL));
            } catch (Exception e) {
            }
            int lastday = getLastday(days);

            int absolutedays = 365;
            try {
                absolutedays = Integer.parseInt(getParameter(Settings.PARAM_DAYSABOSULTE).map.get(Settings.PARAM_DAYSABOSULTE));
            } catch (Exception e) {
            }
            int absoluteday = getLastday(absolutedays);

            double gameaverage;
            double gameabsolute;
            double gametotal;
            double minimumgames;
            int percentage = 25;
            int minimumpercentagedays = 35;
            List<Game> games = new ArrayList<Game>();
            List<Long> players = new ArrayList<Long>();
            Set<String> playingdays = new HashSet<String>();

            try {
                games = getGameList(lastday, -1, "ASC");
                gametotal = games.size();

                games = getGameList(0, -1, "ASC");
                gameabsolute = games.size();

                games = getGameList(lastday, -1, "ASC");

                for (Game game : games) {
                    playingdays.add(game.date);
                    if (!players.contains(game.player11))
                        players.add(game.player11);
                    if (!players.contains(game.player12))
                        players.add(game.player12);
                    if (!players.contains(game.player21))
                        players.add(game.player21);
                    if (!players.contains(game.player22))
                        players.add(game.player22);
                }

                gameaverage = (double) games.size();// * 4 / players.size();

                try {
                    percentage = Integer
                            .parseInt(getParameter(Settings.PARAM_MINIMUMPERCENTAGE).map
                                    .get(Settings.PARAM_MINIMUMPERCENTAGE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    minimumpercentagedays = Integer
                            .parseInt(getParameter(Settings.PARAM_MINIMUMPLAYINGPERCENTAGEDAYS).map
                                    .get(Settings.PARAM_MINIMUMPLAYINGPERCENTAGEDAYS));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                minimumgames = gameaverage * percentage / 100;
            } catch (Exception e) {
                e.printStackTrace();
                gameaverage = 99999;
                gameabsolute = 99999;
                gametotal = 99999;
                minimumgames = 99999;
            }

            try {
                updateParameters(Settings.PARAM_LASTGAMES, games.size() + "");
            } catch (Exception e) {
                updateParameters(Settings.PARAM_LASTGAMES, "0");
            }
            updateParameters(Settings.PARAM_GAMEAVERAGE, gameaverage + "");

            updateParameters(Settings.PARAM_GAMEABSOLUTE, gameabsolute + "");

            updateParameters(Settings.PARAM_GAMETOTAL, gametotal + "");

            updateParameters(Settings.PARAM_GAMETOTAL, gametotal + "");

            updateParameters(Settings.PARAM_MINIMUMPLAYINGDAYS, playingdays.size() * minimumpercentagedays / 100 + "");

            updateParameters(Settings.PARAM_LASTPLAYERS, players.size() + "");

            updateParameters(Settings.PARAM_MINIMUMGAMES, "" + minimumgames);
        } catch (Exception e) {
        }
    }

    public long updateGame(long id, long player11, long player12,
                           long player21, long player22, int result1, int result2, int date, int forecast) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READWRITE);
        long result = -1;

        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("_id", id);
            initialValues.put("player11", player11);
            initialValues.put("player12", player12);
            initialValues.put("player21", player21);
            initialValues.put("player22", player22);
            initialValues.put("result1", result1);
            initialValues.put("result2", result2);
            initialValues.put("date", date);
            initialValues.put("forecast", forecast);
            result = database.update("games", initialValues, "_id = " + id,
                    null);
        } catch (Exception e) {
        }

        if (database != null)
            database.close();

        return result;
    }

    public int getLastday(int days) {
        SQLiteDatabase database = open(SQLiteDatabase.OPEN_READONLY);

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -days);
        int lastday = today.get(Calendar.YEAR) * 10000
                + (today.get(Calendar.MONTH) + 1) * 100
                + today.get(Calendar.DAY_OF_MONTH);

        if (database != null)
            database.close();

        return lastday;
    }

    public void recalculate() {
        updateParameters();
        updatePlayers(-1);
    }

    public class SortByRepetition implements Comparator<Integer> {
        public int compare(Integer n1, Integer n2) {
            return n1 - n2;
        }
    }
}
