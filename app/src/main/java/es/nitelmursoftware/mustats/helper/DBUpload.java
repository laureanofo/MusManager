package es.nitelmursoftware.mustats.helper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBInfo;

public class DBUpload {
    private boolean appversionold = false;
    private boolean test = false;
    private int checking = 1;
    private boolean running = true;
    private int progress = 0;
    private String ftp_user;
    private String ftp_pass;
    private String url;
    private String db_path;
    private char[] buffer;
    private DBInfo server_preferences;
    private DBInfo local_preferences;
    private Context context;
    SharedPreferences prefs;
    String db_name;
    ProgressDialog dialog;

    public DBUpload(Context context) {
        this.context = context;
    }

    public void upload() {
        prefs = context
                .getSharedPreferences(
                        context.getPackageName() + "_preferences",
                        Context.MODE_PRIVATE);
        ftp_user = prefs.getString(context.getString(R.string.pref_ftpuser_key), "");
        ftp_pass = prefs.getString(context.getString(R.string.pref_ftppassword_key), "");
        url = prefs.getString(context.getString(R.string.pref_url_key), "");
        db_name = prefs
                .getString(context.getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        new DownloadJson().execute(url + "/" + db_name + ".json");

        db_path = Environment.getDataDirectory() + "/data/" + context.getPackageName() + Settings.DB_PATH + db_name;

        try {
            FileInputStream fis = new FileInputStream(db_path + ".json");
            local_preferences = new DBInfo(Tools.convertStreamToString(fis));
        } catch (Exception e) {
            try {
                local_preferences = new DBInfo();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void check_version() {
        if (server_preferences.getDB_version() > local_preferences.getDB_version())
            Toast.makeText(context, "Base de datos local antigua", Toast.LENGTH_LONG).show();
        else {
//            Toast.makeText(context, "Base de datos servidor no actualizada", Toast.LENGTH_LONG).show();

            Toast.makeText(context, "Actualizando base de datos en el servidor. Espere...", Toast.LENGTH_LONG).show();

            dialog = new ProgressDialog(context);
            dialog.setTitle("Actualizando BBDD");
            dialog.setMessage("Espere mientras se sube la base de datos al servidor.");
            dialog.setMax(100);
            dialog.setProgress(0);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            new UploadFiles().execute();
        }
    }

    private class UploadFiles extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;
            result += upload_db_file();
            if (result == 0)
                result += upload_json_file();

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
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(context.getString(R.string.pref_db_modified), false);
                editor.commit();


                Toast.makeText(context, "Base de datos actualizada", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(context, "Error subiendo la Base de Datos. Intentelo de nuevo", Toast.LENGTH_LONG).show();
        }

        private int upload_db_file() {
            int result = -1;

            FTPClient ftpClient = new FTPClient();

            try {
                ftpClient.connect(InetAddress.getByName(url));
                ftpClient.setConnectTimeout(1000);
                boolean login = ftpClient.login(ftp_user, ftp_pass);

                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

                ftpClient.enterLocalPassiveMode();

                final File db_file = new File(db_path);
                InputStream input = new FileInputStream(db_file);

                CopyStreamAdapter streamListener = new CopyStreamAdapter() {
                    @Override
                    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                        int percent = (int) (totalBytesTransferred * 100 / db_file.length());
                        publishProgress(percent);
                    }

                };
                ftpClient.setCopyStreamListener(streamListener);

                if (ftpClient.storeFile(db_name, input))
                    result = 0;

                input.close();
                ftpClient.logout();
                ftpClient.disconnect();
                //          }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        private int upload_json_file() {
            int result = -1;

            FTPClient ftpClient = new FTPClient();

            try {
                ftpClient.connect(InetAddress.getByName(url));
                ftpClient.setConnectTimeout(1000);
                boolean login = ftpClient.login(ftp_user, ftp_pass);

                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

                ftpClient.enterLocalPassiveMode();
                InputStream input = new FileInputStream(db_path + ".json");

                if (ftpClient.storeFile(db_name + ".json", input))
                    result = 0;

                input.close();
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    private class DownloadJson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContentCharArray(params[0]);
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

        private String downloadContentCharArray(String myurl) throws IOException {
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
}

