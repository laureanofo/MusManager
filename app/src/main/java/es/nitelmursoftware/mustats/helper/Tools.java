package es.nitelmursoftware.mustats.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBInfo;
import es.nitelmursoftware.mustats.db.DBMMus;

public class Tools {
    public static DecimalFormat df_1 = new DecimalFormat("###.#");
    public static DecimalFormat df_2 = new DecimalFormat("###.##");

    public static boolean externalStorageExist() {
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
            mExternalStorageWriteable = true;
        return mExternalStorageWriteable;
    }

    public static boolean fileCheck(String fileUrl, String filePath) {
        File f = new File(filePath);
        URLConnection ucon;
        try {
            ucon = new URL(fileUrl).openConnection();
            ucon.setReadTimeout(Settings.DOWNLOADDELAY1);
            long totalUrl = ucon.getContentLength();
            if (totalUrl > 0) {
                long totalFile = f.length();
                if (totalFile != totalUrl)
                    return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

/*	public static void fileSave(String filePath, ByteArrayBuffer baf) {
        try {
			FileOutputStream fos;
			File f = new File(filePath);
			if (!f.exists())
				f.getParentFile().mkdirs();
			else
				f.delete();
			fos = new FileOutputStream(f);
			fos.write(baf.toByteArray());
			fos.close();
		} catch (Exception e) {
			e.toString();
		}
	}

	public static ByteArrayBuffer fileDownload(String fileUrl) {
		ByteArrayBuffer res = null;
		try {
			URLConnection ucon = new URL(fileUrl).openConnection();
			ucon.setConnectTimeout(Settings.DOWNLOADDELAY1);
			long total = ucon.getContentLength();
			if (total > 0) {
				BufferedInputStream bis = new BufferedInputStream(
						ucon.getInputStream());

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int count = 0;
				byte[] bytes = new byte[1024];
				while ((count = bis.read(bytes, 0, bytes.length)) != -1) {
					baf.append(bytes, 0, count);
					total -= count;
				}
				if (total == 0)
					res = baf;
			}
		} catch (Exception e) {
		}
		return res;
	}*/

    public static boolean openPDF(Context c, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(filePath);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            c.startActivity(intent);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void showToast(Context context, String text, int duration) {
        Toast t = Toast.makeText(context, text, duration);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    public static int OpposeColor(int ColorToInvert) {
        int RGBMAX = 255;
        float[] hsv = new float[3];
        float H;

        Color.RGBToHSV(Color.red(ColorToInvert),
                RGBMAX - Color.green(ColorToInvert), Color.blue(ColorToInvert),
                hsv);

        H = (float) (hsv[0] + 0.5);

        if (H > 1)
            H -= 1;

        return Color.HSVToColor(hsv);
    }

    public static boolean isOnLine(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                    break;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                    break;
                }
        }
        if (haveConnectedWifi == false && haveConnectedMobile == false) {
            return false;
        }
        return true;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static void setDBModified(Context context, SharedPreferences prefs) {
        if (!prefs.getBoolean(context.getString(R.string.pref_db_modified), false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(context.getString(R.string.pref_db_modified), true);
            editor.commit();

            String db_name = prefs
                    .getString(context.getString(R.string.pref_dbname_key),
                            "mus.sqlite");
            String db_path = Environment.getDataDirectory() + "/data/" + context.getPackageName() + Settings.DB_PATH + db_name;
            FileInputStream fis = null;
            BufferedOutputStream bos = null;
            try {
                fis = new FileInputStream(db_path + ".json");
                DBInfo local_preferences = new DBInfo(Tools.convertStreamToString(fis));
                local_preferences.setDB_version(local_preferences.getDB_version() + 1);

                File file = new File(db_path + ".json");
                if (!file.exists()) {
                    file.mkdirs();
                } else {
                    file.delete();
                }

                bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(local_preferences.getJson().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null)
                    try {
                        bos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
            }

        }
    }

    public static void recalculate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE);

        String dbname = prefs
                .getString(context.getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File data = Environment.getDataDirectory();
        String currentDBPath = data + "/data/" + context.getPackageName() + Settings.DB_PATH + dbname;
        DBMMus dbmmus = new DBMMus(currentDBPath);
        dbmmus.recalculate();
    }

    public static boolean checkWritableDB(Context context, SharedPreferences prefs) {
        boolean result = false;

        String user = prefs.getString(context.getString(R.string.pref_ftpuser_key), "");
        String pass = prefs.getString(context.getString(R.string.pref_ftppassword_key), "");

        if (user.length() > 0 && pass.length() > 0)
            result = true;

        return result;
    }

    public static boolean checkFTPUserPass(Context context, String url, String ftp_user, String ftp_pass) {
        boolean result = false;

        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(InetAddress.getByName(url));
            ftpClient.setConnectTimeout(1000);
            result = ftpClient.login(ftp_user, ftp_pass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
