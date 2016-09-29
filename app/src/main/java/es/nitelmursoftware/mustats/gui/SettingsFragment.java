package es.nitelmursoftware.mustats.gui;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBMMus;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

/**
 * Created by lferolm on 10/7/16.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences prefs;
    private DBMMus dbmmus;
    private String dbname;
    public FloatingActionButton fab;
    EditText et_url;
    EditText et_user;
    EditText et_pass;
    EditText et_days_absolute;
    EditText et_days_partial;
    EditText et_minimum;
    EditText et_minimumdays;
    Button bt_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_settings, container, false);

        prefs = getActivity().getSharedPreferences(
                getActivity().getPackageName() + "_preferences",
                Context.MODE_PRIVATE);

        dbname = prefs
                .getString(getActivity().getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File data = Environment.getDataDirectory();
        String currentDBPath = data + "/data/" + getActivity().getPackageName() + es.nitelmursoftware.mustats.helper.Settings.DB_PATH + "mus.sqlite";
        dbmmus = new DBMMus(currentDBPath);

        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(null);

        init(rootView);

        return rootView;
    }

    private void init(View view) {
        et_url = (EditText) view.findViewById(R.id.et_url);
        String url = prefs.getString(getActivity().getString(R.string.pref_url_key), Settings.URL);
        et_url.setText(url);

        et_user = (EditText) view.findViewById(R.id.et_user);
        String user = prefs.getString(getActivity().getString(R.string.pref_ftpuser_key), "");
        et_user.setText(user);

        et_pass = (EditText) view.findViewById(R.id.et_password);
        String pass = prefs.getString(getActivity().getString(R.string.pref_ftppassword_key), "");
        et_pass.setText(pass);

        et_days_absolute = (EditText) view.findViewById(R.id.et_absoluto);
        String day_absolute = dbmmus.getParameter(Settings.PARAM_DAYSABOSULTE).map.get(Settings.PARAM_DAYSABOSULTE);
        et_days_absolute.setText(day_absolute);

        et_days_partial = (EditText) view.findViewById(R.id.et_parcial);
        String day_partial = dbmmus.getParameter(Settings.PARAM_DAYSPARTIAL).map.get(Settings.PARAM_DAYSPARTIAL);
        et_days_partial.setText(day_partial);

        et_minimum = (EditText) view.findViewById(R.id.et_porcentaje);
        String minimum = dbmmus.getParameter(Settings.PARAM_MINIMUMPERCENTAGE).map.get(Settings.PARAM_MINIMUMPERCENTAGE);
        et_minimum.setText(minimum);

        et_minimumdays = (EditText) view.findViewById(R.id.et_porcentajedays);
        String minimumdays = dbmmus.getParameter(Settings.PARAM_MINIMUMPLAYINGPERCENTAGEDAYS).map.get(Settings.PARAM_MINIMUMPLAYINGPERCENTAGEDAYS);
        et_minimumdays.setText(minimumdays);

        bt_save = (Button) view.findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_save) {
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(getActivity().getString(R.string.pref_url_key), et_url.getText().toString());

            editor.commit();

            dbmmus.updateParameters(Settings.PARAM_DAYSABOSULTE, et_days_absolute.getText().toString());
            dbmmus.updateParameters(Settings.PARAM_DAYSPARTIAL, et_days_partial.getText().toString());
            dbmmus.updateParameters(Settings.PARAM_MINIMUMPERCENTAGE, et_minimum.getText().toString());
            dbmmus.updateParameters(Settings.PARAM_MINIMUMPLAYINGPERCENTAGEDAYS, et_minimumdays.getText().toString());
            Tools.showToast(getActivity(), "BBDD actualizada", Toast.LENGTH_SHORT);

            Tools.setDBModified(getActivity(), prefs);

            dbmmus.recalculate();

            if (et_user.getText().toString().length() > 0 && et_pass.getText().toString().length() > 0)
                new CheckFTPUser().execute(new String[]{et_url.getText().toString(), et_user.getText().toString(), et_pass.getText().toString()});
        }
    }

    private class CheckFTPUser extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;

            if (!Tools.checkFTPUserPass(getActivity(), params[0], params[1], params[2]))
                result = -1;

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            SharedPreferences.Editor editor = prefs.edit();
            if (result == 0) {
                editor.putString(getActivity().getString(R.string.pref_ftpuser_key), et_user.getText().toString());
                editor.putString(getActivity().getString(R.string.pref_ftppassword_key), et_pass.getText().toString());

                Tools.showToast(getActivity(), "Usuario/contraseña actualizado", Toast.LENGTH_SHORT);
            } else {
                editor.putString(getActivity().getString(R.string.pref_ftpuser_key), "");
                editor.putString(getActivity().getString(R.string.pref_ftppassword_key), "");

                Tools.showToast(getActivity(), "Usuario/contraseña incorrecta", Toast.LENGTH_SHORT);
            }

            editor.commit();
        }
    }
}
