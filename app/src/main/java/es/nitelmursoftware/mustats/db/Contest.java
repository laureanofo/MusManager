package es.nitelmursoftware.mustats.db;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Contest {
    public long id;
    public String registerDate;
    public String startDate;
    public int type;

    public Contest(long id, String registerDate, String startDate, int type) {
        super();
        this.id = id;
        this.startDate = startDate;
        this.registerDate = registerDate;
        this.type = type;
    }

    public static long[] getContests(Cursor c) {
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

    public static List<Contest> getContestList(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        List<Contest> result = new ArrayList<Contest>();
        c.moveToFirst();
        do {
            result.add(getContest(c, c.getPosition()));
        } while (c.moveToNext());

        return result;
    }

    public static Contest getContest(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;

        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex("_id"));
        String startDate = c.getString(c.getColumnIndex("start_date"));
        String registerDate = c.getString(c.getColumnIndex("register_date"));
        int type = c.getInt(c.getColumnIndex("type"));

        Contest result = new Contest(id, registerDate, startDate, type);

        return result;
    }

    public static Contest getContest(Cursor c, int position) {
        if (c == null || c.getCount() == 0)
            return null;

        if (!c.moveToPosition(position))
            return null;

        long id = c.getLong(c.getColumnIndex("_id"));
        String startDate = c.getString(c.getColumnIndex("start_date"));
        String registerDate = c.getString(c.getColumnIndex("register_date"));
        int type = c.getInt(c.getColumnIndex("type"));

        Contest result = new Contest(id, registerDate, startDate, type);

        return result;
    }
}
