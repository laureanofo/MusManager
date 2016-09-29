package es.nitelmursoftware.mustats.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class Game {
    public long id;
    public String date;
    public long player11;
    public long player12;
    public long player21;
    public long player22;
    public int result1;
    public int result2;
    public int forecast;

    public Game(long id, String date, long player11, long player12,
                long player21, long player22, int result1, int result2, int forecast) {
        super();
        this.id = id;
        this.date = date;
        this.player11 = player11;
        this.player12 = player12;
        this.player21 = player21;
        this.player22 = player22;
        this.result1 = result1;
        this.result2 = result2;
        this.forecast = forecast;
    }

    public static long[] getGames(Cursor c) {
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

    public static List<Game> getGameList(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        List<Game> result = new ArrayList<Game>();
        c.moveToFirst();
        do {
            result.add(getGame(c, c.getPosition()));
        } while (c.moveToNext());

        return result;
    }

    public static Game getGame(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex("_id"));
        String date = c.getString(c.getColumnIndex("date"));
        long player11 = c.getLong(c.getColumnIndex("player11"));
        long player12 = c.getLong(c.getColumnIndex("player12"));
        long player21 = c.getLong(c.getColumnIndex("player21"));
        long player22 = c.getLong(c.getColumnIndex("player22"));
        int result1 = c.getInt(c.getColumnIndex("result1"));
        int result2 = c.getInt(c.getColumnIndex("result2"));
        int forecast = c.getInt(c.getColumnIndex("forecast"));

        Game result = new Game(id, date, player11, player12, player21,
                player22, result1, result2, forecast);

        return result;
    }

    public static Game getGame(Cursor c, int position) {
        if (c == null || c.getCount() == 0)
            return null;

        if (!c.moveToPosition(position))
            return null;

        long id = c.getLong(c.getColumnIndex("_id"));
        String date = c.getString(c.getColumnIndex("date"));
        long player11 = c.getLong(c.getColumnIndex("player11"));
        long player12 = c.getLong(c.getColumnIndex("player12"));
        long player21 = c.getLong(c.getColumnIndex("player21"));
        long player22 = c.getLong(c.getColumnIndex("player22"));
        int result1 = c.getInt(c.getColumnIndex("result1"));
        int result2 = c.getInt(c.getColumnIndex("result2"));
        int forecast = c.getInt(c.getColumnIndex("forecast"));

        Game result = new Game(id, date, player11, player12, player21,
                player22, result1, result2, forecast);

        return result;
    }
}
