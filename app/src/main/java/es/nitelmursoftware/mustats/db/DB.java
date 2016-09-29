package es.nitelmursoftware.mustats.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

public class DB {
    public static int error_different_id = -1;
    public static int error_not_exist_table = -2;
    public static int error_updating_data = -3;
    public static int error_adding_data = -3;

    public static void deleteDataBase(String dbpath, String dbname)
            throws IOException {
        File file = new File(dbpath + dbname);
        file.delete();
    }

    public static void createDataBase(Context context, String dbpath,
                                      String dbname) throws IOException {
        File data = Environment.getDataDirectory();

        File currentDB;
        String currentDBPath = data + "/data/" + context.getPackageName() + dbpath + dbname;
        currentDB = new File(currentDBPath);

        boolean dbExist = checkDataBase(currentDB);
        boolean replace = false;

        if (dbExist) {
            copyDataBase(context, currentDB, "temp");
            String myPath = currentDB.getParent() + "//" + "temp";
            SQLiteDatabase myDataBaseTemp1 = SQLiteDatabase.openDatabase(
                    myPath, null, SQLiteDatabase.OPEN_READONLY);
            SQLiteDatabase myDataBaseTemp2 = SQLiteDatabase.openDatabase(
                    currentDBPath, null, SQLiteDatabase.OPEN_READONLY);
            if (getVersion(myDataBaseTemp1) > getVersion(myDataBaseTemp2)) {
                File file = new File(dbpath + dbname);
                file.delete();
                replace = true;
            }
            myDataBaseTemp1.close();
            myDataBaseTemp2.close();
            File file = new File(dbpath + "temp");
            file.delete();
        }
        if (!dbExist || replace) {
            try {
                copyDataBase(context, currentDB, null);
            } catch (IOException e) {
                System.out.println("Error copying DB " + e);
            }
        }
    }

    public static int getVersion(SQLiteDatabase myDataBase) {
        int temp = -1;
        Cursor c = null;
        try {
            String SQL = "SELECT * FROM data";

            c = myDataBase.rawQuery(SQL, null);
            c.moveToFirst();
            temp = c.getInt(c.getColumnIndex("version"));

        } catch (Exception e) {
        }
        if (c != null)
            c.close();
        return temp;
    }

    private static boolean checkDataBase(File database) {
        SQLiteDatabase checkDB = null;
        boolean temp = false;
        try {
            if (database.exists()) {
                checkDB = SQLiteDatabase.openDatabase(database.getPath(), null,
                        SQLiteDatabase.OPEN_READONLY);
                temp = checkDB != null ? true : false;
            }
        } catch (SQLiteException e) {
            // database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return temp;
    }

    private static void copyDataBase(Context context, File database, String dbnamedest) throws IOException {
        String assetsName = database.getName();
        InputStream myInput = context.getAssets().open(assetsName);

        File destinationFile = database;
        if (dbnamedest != null)
            destinationFile = new File(database.getParent() + "/" + dbnamedest);

        if (!destinationFile.exists())
            database.mkdirs();
        else
            destinationFile.delete();

        String outFileName = database.getAbsolutePath();
        OutputStream myOutput = new FileOutputStream(destinationFile.getPath());
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public static boolean execSql(String dbpath, String dbname, String sql) {
        SQLiteDatabase myDataBase = null;
        boolean temp = true;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);

            try {
                myDataBase.execSQL(sql);
            } catch (Exception e) {
                temp = false;
            }
        } catch (Exception e) {
        }
        if (myDataBase != null)
            myDataBase.close();
        return temp;
    }

    public static int getVersion(String dbpath, String dbname) {
        SQLiteDatabase myDataBase = null;
        Cursor c = null;
        int temp = -1;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READONLY);
            String SQL = "SELECT version FROM data";

            c = myDataBase.rawQuery(SQL, null);
            c.moveToFirst();
            temp = c.getInt(0);
        } catch (Exception e) {
        }
        if (c != null)
            c.close();
        if (myDataBase != null)
            myDataBase.close();
        return temp;
    }

    public static String getDate(String dbpath, String dbname) {
        SQLiteDatabase myDataBase = null;
        Cursor c = null;
        String temp = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READONLY);
            String SQL = "SELECT date FROM data";

            c = myDataBase.rawQuery(SQL, null);
            c.moveToFirst();
            temp = c.getString(0);
        } catch (Exception e) {
        }
        if (c != null)
            c.close();
        if (myDataBase != null)
            myDataBase.close();
        return temp;
    }

    public static String getUpdate(String dbpath, String dbname) {
        SQLiteDatabase myDataBase = null;
        Cursor c = null;
        String temp = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READONLY);
            String SQL = "SELECT update FROM data";

            c = myDataBase.rawQuery(SQL, null);
            c.moveToFirst();
            temp = c.getString(0);
        } catch (Exception e) {
        }
        if (c != null)
            c.close();
        if (myDataBase != null)
            myDataBase.close();
        return temp;
    }

    public static boolean setData(String dbpath, String dbname, int id,
                                  String tablename, String fieldname, String value) {
        boolean result = true;
        SQLiteDatabase myDataBase = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(fieldname, value);
            String[] where = {id + ""};
            myDataBase.update(tablename, dataToInsert, "_id=?", where);
        } catch (Exception e) {
            result = false;
        }
        if (myDataBase != null)
            myDataBase.close();
        return result;
    }

    public static boolean setData(String dbpath, String dbname, int id,
                                  String tablename, String fieldname, int value) {
        boolean result = true;
        SQLiteDatabase myDataBase = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(fieldname, value);
            String[] where = {id + ""};
            myDataBase.update(tablename, dataToInsert, "_id=?", where);
        } catch (Exception e) {
            result = false;
        }
        if (myDataBase != null)
            myDataBase.close();
        return result;
    }

    public static boolean setData(String dbpath, String dbname, int id,
                                  String tablename, String fieldname, double value) {
        boolean result = true;
        SQLiteDatabase myDataBase = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(fieldname, value);
            String[] where = {id + ""};
            myDataBase.update(tablename, dataToInsert, "_id=?", where);
        } catch (Exception e) {
            result = false;
        }
        if (myDataBase != null)
            myDataBase.close();
        return result;
    }

    public static boolean setData(String dbpath, String dbname, int id,
                                  String tablename, String fieldname, byte[] value) {
        boolean result = true;
        SQLiteDatabase myDataBase = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(fieldname, value);
            String[] where = {id + ""};
            myDataBase.update(tablename, dataToInsert, "_id=?", where);
        } catch (Exception e) {
            result = false;
        }
        if (myDataBase != null)
            myDataBase.close();
        return result;
    }

    public static boolean setData(String dbpath, String dbname, int id,
                                  String tablename, String fieldname, int[] value) {
        boolean result = true;
        SQLiteDatabase myDataBase = null;
        String s = ":";
        for (int i : value) {
            s = s + i + ":";
        }
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);
            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put(fieldname, s);
            String[] where = {id + ""};
            myDataBase.update(tablename, dataToInsert, "_id=?", where);
        } catch (Exception e) {
            result = false;
        }
        if (myDataBase != null)
            myDataBase.close();
        return result;
    }

    public static boolean delRow(String dbpath, String dbname, int id,
                                 String tablename) {
        boolean result = true;
        SQLiteDatabase myDataBase = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READWRITE);
            myDataBase.delete(tablename, "_id=?", new String[]{id + ""});
        } catch (Exception e) {
            result = false;
        }
        if (myDataBase != null)
            myDataBase.close();
        return result;
    }

    public static boolean existTable(String dbpath, String dbname,
                                     String tablename) {
        boolean tableExists = false;
        SQLiteDatabase myDataBase = null;
        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READONLY);
            myDataBase.query(tablename, null, null, null, null, null, null);
            tableExists = true;
        } catch (Exception e) {
        }
        if (myDataBase != null)
            myDataBase.close();
        return tableExists;
    }

    public static ArrayList<String> getTables(String dbpath, String dbname) {
        SQLiteDatabase myDataBase = null;
        Cursor c = null;
        ArrayList<String> temp = new ArrayList<String>();

        try {
            myDataBase = SQLiteDatabase.openDatabase(dbpath + dbname, null,
                    SQLiteDatabase.OPEN_READONLY);
            String SQL = "SELECT name FROM sqlite_master WHERE type = 'table'";

            c = myDataBase.rawQuery(SQL, null);
            while (c.moveToNext())
                temp.add(c.getString(c.getColumnIndex("name")));
        } catch (Exception e) {
        }
        if (c != null)
            c.close();
        if (myDataBase != null)
            myDataBase.close();
        return temp;
    }

    public static int[] getList(Cursor c) {
        if (c == null || c.getCount() == 0)
            return null;
        int[] results = new int[c.getCount()];
        c.moveToFirst();
        int i = 0;
        do {
            results[i++] = c.getInt(c.getColumnIndex("_id"));
        } while (c.moveToNext());

        return results;
    }
}
