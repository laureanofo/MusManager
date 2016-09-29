package es.nitelmursoftware.mustats.db;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lferolm on 4/7/16.
 */
public class DBInfo {
    private final String name = "name";
    private final String date = "date";
    private final String db_version = "db_version";
    private final String android_version = "android_version";
    private final String ios_version = "ios_version";
    JSONObject jsonObject;

    public DBInfo() throws JSONException {
        this.jsonObject = new JSONObject();
        jsonObject.put(name, "");
        jsonObject.put(date, "");
        jsonObject.put(db_version, -1);
        jsonObject.put(android_version, -1);
        jsonObject.put(ios_version, -1);
    }

    public DBInfo(String json) throws JSONException {
        this.jsonObject = new JSONObject(json);
    }

    public String getName() {
        try {
            return jsonObject.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setName(String name) {
        try {
            jsonObject.put(this.name, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        try {
            return jsonObject.getString(date);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDate(String date) {
        try {
            jsonObject.put(this.date, date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getDB_version() {
        try {
            return jsonObject.getInt(db_version);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setDB_version(int db_version) {
        try {
            jsonObject.put(this.db_version, db_version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getAndroid_version() {
        try {
            return jsonObject.getInt(android_version);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setAndroid_version(int android_version) {
        try {
            jsonObject.put(this.android_version, android_version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getIos_version() {
        try {
            return jsonObject.getInt(ios_version);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setIos_version(int ios_version) {
        try {
            jsonObject.put(this.ios_version, ios_version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void putJson(String json) throws JSONException {
        jsonObject = new JSONObject(json);
    }

    public String getJson() {
        return jsonObject.toString();
    }
}
