package es.nitelmursoftware.mustats.helper;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBInfo;

public class DBDownload {
    private String url;
    private String db_path;
    private String db_name;
    private DBInfo server_preferences;
    private DBInfo local_preferences;
    private Context context;
    private long downloadReference;
    private DownloadManager downloadManager;
    ProgressDialog dialog;

    public DBDownload(Context context) {
        this.context = context;
    }

    public void download() {
        SharedPreferences prefs = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        url = prefs.getString(context.getString(R.string.pref_url_key), Settings.URL);
        db_name = prefs
                .getString(context.getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        db_path = Environment.getDataDirectory() + "/data/" + context.getPackageName() + Settings.DB_PATH;

        try {
            FileInputStream fis = new FileInputStream(db_path + db_name + ".json");
            local_preferences = new DBInfo(Tools.convertStreamToString(fis));
        } catch (Exception e) {
            try {
                local_preferences = new DBInfo();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        new DownloadJson().execute(url + "/" + db_name + ".json");
    }

    private void check_version() {
        File db = new File(db_path + db_name);
        if (server_preferences.getDB_version() > local_preferences.getDB_version() || !db.exists()) {
            Toast.makeText(context, "Actualizando Base de Datos. Espere unos instantes...", Toast.LENGTH_LONG).show();
            dialog = new ProgressDialog(context);
            dialog.setTitle("Actualizando BBDD");
            dialog.setMessage("Espere mientras se sube la base de datos al servidor.");
            dialog.setMax(100);
            dialog.setProgress(0);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            new DownloadDB().execute(url + "/" + db_name);
        } else if (server_preferences.getDB_version() < local_preferences.getDB_version())
            Toast.makeText(context, "Base de datos servidor no actualizada", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Base de datos actualizada", Toast.LENGTH_LONG).show();

    }

    private class DownloadJson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContentString(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            try {
                server_preferences = new DBInfo(result);
                check_version();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadContentString(String myurl) throws IOException {
            InputStream is = null;
            int length = 500;

            try {
                URL url = new URL("http://" + myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                char[] content = convertInputStreamToCharArray(is, length);
                return new String(content);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public char[] convertInputStreamToCharArray(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[length];
            reader.read(buffer);
            return buffer;
        }
    }


    private class DownloadDB extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;

            result += downloadContent(params[0]);

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();

            if (result == 0) {
                Toast.makeText(context, "Base de datos actualizada", Toast.LENGTH_LONG).show();
                Tools.recalculate(context);
            } else
                Toast.makeText(context, "Error descargando la Base de Datos. Intentelo reiniciando la aplicación", Toast.LENGTH_LONG).show();
        }

        private int downloadContent(String myurl) {
            int result = -1;

            InputStream is = null;
            FileOutputStream os = null;
            BufferedOutputStream bos=null;

            try {
                URL url = new URL("http://" + myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                File db = new File(db_path + db_name);
                if (!db.exists()) {
                    new File(db.getParent()).mkdirs();
                } else {
                    db.delete();
                }

                os = new FileOutputStream(db);

                byte[] b = new byte[1024];
                int count;

                int total = 0;
                while ((count = is.read(b)) > 0) {
                    os.write(b, 0, count);
                    total += count;
                    int percent = (int) (total * 100 / conn.getContentLength());
                    publishProgress(percent);
                }

                File json = new File(db_path + db_name + ".json");
                if (!json.exists()) {
                    new File(json.getParent()).mkdirs();
                } else {
                    json.delete();
                }
                bos = new BufferedOutputStream(new FileOutputStream(json));
                bos.write(server_preferences.getJson().getBytes());

                result = 0;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }
    }
}

