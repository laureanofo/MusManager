package es.nitelmursoftware.mustats.db;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ContestCouple {
    public long id;
    public long contestId;
    public long player1;
    public long player2;
    public int group;

    public ContestCouple(long id, long contestId, long player1, long player2, int group) {
        super();
        this.id = id;
        this.contestId = contestId;
        this.player1 = player1;
        this.player2 = player2;
        this.group = group;
    }

    public static long[] getCouples(Cursor c) {
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

    public static List<ContestCouple> getCoupleList(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        List<ContestCouple> result = new ArrayList<ContestCouple>();
        c.moveToFirst();
        do {
            result.add(getCouple(c, c.getPosition()));
        } while (c.moveToNext());

        return result;
    }

    public static ContestCouple getCouple(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex("_id"));
        long contestId = c.getLong(c.getColumnIndex("contest_id"));
        long player1 = c.getLong(c.getColumnIndex("player1"));
        long player2 = c.getLong(c.getColumnIndex("player2"));
        int group = c.getInt(c.getColumnIndex("group"));

        ContestCouple result = new ContestCouple(id, contestId, player1, player2, group);

        return result;
    }

    public static ContestCouple getCouple(Cursor c, int position) {
        if (c == null || c.getCount() == 0)
            return null;

        if (!c.moveToPosition(position))
            return null;

        long id = c.getLong(c.getColumnIndex("_id"));
        long contestId = c.getLong(c.getColumnIndex("contest_id"));
        long player1 = c.getLong(c.getColumnIndex("player1"));
        long player2 = c.getLong(c.getColumnIndex("player2"));
        int group = c.getInt(c.getColumnIndex("group"));

        ContestCouple result = new ContestCouple(id, contestId, player1, player2, group);

        return result;
    }
}
