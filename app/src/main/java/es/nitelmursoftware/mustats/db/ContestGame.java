package es.nitelmursoftware.mustats.db;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ContestGame {
    public long id;
    public long contestId;
    public long couple1Id;
    public long couple2Id;
    public int[] games1;
    public int[] games2;
    public String date;

    public ContestGame(long id, String date, long contestId, long couple1Id, long couple2Id, int[] games1, int[] games2) {
        super();
        this.id = id;
        this.date = date;
        this.contestId = contestId;
        this.couple1Id = couple1Id;
        this.couple2Id = couple2Id;
        this.games1 = games1;
        this.games2 = games2;
    }

    public static long[] getContestGames(Cursor c) {
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

    public static List<ContestGame> getContestGameList(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        List<ContestGame> result = new ArrayList<ContestGame>();
        c.moveToFirst();
        do {
            result.add(getContest(c, c.getPosition()));
        } while (c.moveToNext());

        return result;
    }

    public static ContestGame getContestGame(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex("_id"));
        String date = c.getString(c.getColumnIndex("date"));
        long contestId = c.getLong(c.getColumnIndex("contestId"));
        long couple1Id = c.getLong(c.getColumnIndex("couple1Id"));
        long couple2Id = c.getLong(c.getColumnIndex("couple2Id"));

        String[] strings = c.getString(c.getColumnIndex("games1")).split(":");
        int[] games1 = new int[strings.length];
        int pos = 0;
        for (String s : strings)
            games1[pos++] = Integer.parseInt(s);

        strings = c.getString(c.getColumnIndex("games2")).split(":");
        int[] games2 = new int[strings.length];
        pos = 0;
        for (String s : strings)
            games2[pos++] = Integer.parseInt(s);

        ContestGame result = new ContestGame(id, date, contestId, couple1Id, couple2Id, games1, games2);

        return result;
    }

    public static ContestGame getContest(Cursor c, int position) {
        if (c == null || c.getCount() == 0)
            return null;

        if (!c.moveToPosition(position))
            return null;

        long id = c.getLong(c.getColumnIndex("_id"));
        String date = c.getString(c.getColumnIndex("date"));
        long contestId = c.getLong(c.getColumnIndex("contestId"));
        long couple1Id = c.getLong(c.getColumnIndex("couple1Id"));
        long couple2Id = c.getLong(c.getColumnIndex("couple2Id"));

        String[] strings = c.getString(c.getColumnIndex("games1")).split(":");
        int[] games1 = new int[strings.length];
        int pos = 0;
        for (String s : strings)
            games1[pos++] = Integer.parseInt(s);

        strings = c.getString(c.getColumnIndex("games2")).split(":");
        int[] games2 = new int[strings.length];
        pos = 0;
        for (String s : strings)
            games2[pos++] = Integer.parseInt(s);

        ContestGame result = new ContestGame(id, date, contestId, couple1Id, couple2Id, games1, games2);

        return result;
    }
}
