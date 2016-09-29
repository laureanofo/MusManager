package es.nitelmursoftware.mustats.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class Player {
    public long id;
    public String name;
    public String alias;
    public byte[] picture;
    public double lastranking;
    public double ranking;
    public int lastwins;
    public int lastgames;
    public int games;
    public int wins;
    public int bestmate;
    public int worstmate;
    public int positives;
    public int negatives;
    public int rafale;
    public int rafale_wins;
    public int rafale_losts;
    public int days;
    public int lastdays;
    private String fragmentName = "";

    public Player(long id, String name, String alias, byte[] picture,
                  int lastgames, int lastwins, int games, int wins, int bestmate,
                  int worstmate, int positives, int negatives, int rafale,
                  int rafale_wins, int rafale_losts, double lastranking,
                  double ranking, int days, int lastdays) {
        super();
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.picture = picture;
        this.lastgames = lastgames;
        this.lastwins = lastwins;
        this.games = games;
        this.wins = wins;
        this.bestmate = bestmate;
        this.worstmate = worstmate;
        this.positives = positives;
        this.negatives = negatives;
        this.rafale = rafale;
        this.rafale_losts = rafale_losts;
        this.rafale_wins = rafale_wins;
        this.lastranking = lastranking;
        this.ranking = ranking;
        this.days = days;
        this.lastdays = lastdays;
    }

    public static long[] getPlayers(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        long[] result = new long[c.getCount()];
        c.moveToFirst();
        int i = 0;
        do {
            result[i++] = c.getInt(c.getColumnIndex("_id"));
        } while (c.moveToNext());

        return result;
    }

    public static List<Player> getPlayerList(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        List<Player> result = new ArrayList<Player>();
        c.moveToFirst();
        do {
            result.add(getPlayer(c, c.getPosition()));
        } while (c.moveToNext());

        return result;
    }

    public static Player getPlayer(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex("_id"));
        String name = c.getString(c.getColumnIndex("name"));
        String alias = c.getString(c.getColumnIndex("alias"));
        byte[] picture = null;
        try {
            picture = c.getBlob(c.getColumnIndex("picture"));
        } catch (Exception e) {
        }
        int lastgames = c.getInt(c.getColumnIndex("lastgames"));
        int lastwins = c.getInt(c.getColumnIndex("lastwins"));
        int games = c.getInt(c.getColumnIndex("games"));
        int wins = c.getInt(c.getColumnIndex("wins"));
        int bestmate = c.getInt(c.getColumnIndex("bestmate"));
        int worstmate = c.getInt(c.getColumnIndex("worstmate"));
        int positives = c.getInt(c.getColumnIndex("positives"));
        int negatives = c.getInt(c.getColumnIndex("negatives"));
        int rafale = c.getInt(c.getColumnIndex("rafale"));
        int rafale_wins = c.getInt(c.getColumnIndex("rafalewins"));
        int rafale_losts = c.getInt(c.getColumnIndex("rafalelosts"));
        double ranking = c.getDouble(c.getColumnIndex("ranking"));
        double lastranking = c.getDouble(c.getColumnIndex("lastranking"));
        int days = c.getInt(c.getColumnIndex("days"));
        int lastdays = c.getInt(c.getColumnIndex("lastdays"));

        Player result = new Player(id, name, alias, picture, lastgames,
                lastwins, games, wins, bestmate, worstmate, positives,
                negatives, rafale, rafale_wins, rafale_losts, lastranking,
                ranking, days, lastdays);

        return result;
    }

    public static Player getPlayer(Cursor c, int position) {
        if (c == null || c.getCount() == 0)
            return null;

        if (!c.moveToPosition(position))
            return null;

        long id = c.getLong(c.getColumnIndex("_id"));
        String name = c.getString(c.getColumnIndex("name"));
        String alias = c.getString(c.getColumnIndex("alias"));
        byte[] picture = null;
        try {
            picture = c.getBlob(c.getColumnIndex("picture"));
        } catch (Exception e) {
        }
        int lastgames = c.getInt(c.getColumnIndex("lastgames"));
        int lastwins = c.getInt(c.getColumnIndex("lastwins"));
        int games = c.getInt(c.getColumnIndex("games"));
        int wins = c.getInt(c.getColumnIndex("wins"));
        int bestmate = c.getInt(c.getColumnIndex("bestmate"));
        int worstmate = c.getInt(c.getColumnIndex("worstmate"));
        int positives = c.getInt(c.getColumnIndex("positives"));
        int negatives = c.getInt(c.getColumnIndex("negatives"));
        int rafale = c.getInt(c.getColumnIndex("rafale"));
        int rafale_wins = c.getInt(c.getColumnIndex("rafalewins"));
        int rafale_losts = c.getInt(c.getColumnIndex("rafalelosts"));
        double ranking = c.getDouble(c.getColumnIndex("ranking"));
        double lastranking = c.getDouble(c.getColumnIndex("lastranking"));
        int days = c.getInt(c.getColumnIndex("days"));
        int lastdays = c.getInt(c.getColumnIndex("lastdays"));

        Player result = new Player(id, name, alias, picture, lastgames,
                lastwins, games, wins, bestmate, worstmate, positives,
                negatives, rafale, rafale_wins, rafale_losts, lastranking,
                ranking, days, lastdays);

        return result;
    }

    public static List<String> getPlayerAliasList(List<Player> players) {
        if (players == null)
            return null;

        List<String> alias = new ArrayList<String>();

        for (Player player : players)
            if (player.alias == null || player.alias.length() < 2)
                alias.add(player.name);
            else
                alias.add(player.alias);

        return alias;
    }

}
