package es.nitelmursoftware.mustats.db;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class Parameters {
	public Map<String, String> map;

	public Parameters() {
		super();
		map = new HashMap<String, String>();
	}

	public static Parameters getParameters(Cursor c) {
		if (c == null || c.getCount() == 0)
			return null;

		Parameters result = new Parameters();

		c.moveToFirst();
		do {
			String name = c.getString(c.getColumnIndex("name"));
			String value = c.getString(c.getColumnIndex("value"));

			result.map.put(name, value);
		} while (c.moveToNext());

		return result;
	}

}
