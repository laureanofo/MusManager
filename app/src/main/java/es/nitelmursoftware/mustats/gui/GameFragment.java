package es.nitelmursoftware.mustats.gui;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBMMus;
import es.nitelmursoftware.mustats.db.Game;
import es.nitelmursoftware.mustats.db.Player;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

public class GameFragment extends Fragment implements OnClickListener,
        OnItemSelectedListener {
    private long gameId;
    Game game;
    EditText et_date;
    Spinner sp_player11;
    Spinner sp_player12;
    Spinner sp_player21;
    Spinner sp_player22;
    EditText et_result1;
    EditText et_result2;
    EditText et_forecast1;
    EditText et_forecast2;
    ImageView iv_player11;
    ImageView iv_player12;
    ImageView iv_player21;
    ImageView iv_player22;
    Button bt_edit;
    Bitmap picture;
    List<Player> players;
    private String fragmentName;
    String dbname;
    DBMMus dbmmus;
    int forecast = 0;

    boolean editing = false;
    boolean adding = false;
    View view;
    public FloatingActionButton fab;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fab.setOnClickListener(null);

        view = inflater.inflate(R.layout.content_game, container, false);

        if (savedInstanceState != null)
            gameId = savedInstanceState.getLong(Settings.ARG_GAME);
        else
            gameId = getArguments().getLong(Settings.ARG_GAME);

        prefs = getActivity().getSharedPreferences(
                getActivity().getPackageName() + "_preferences",
                Context.MODE_PRIVATE);

        dbname = prefs
                .getString(getActivity().getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File data = Environment.getDataDirectory();
        String currentDBPath = data + "/data/" + getActivity().getPackageName() + Settings.DB_PATH + dbname;
        dbmmus = new DBMMus(currentDBPath);

        game = dbmmus.getGame(gameId);

        init();

        if (savedInstanceState != null)
            setSaveData(savedInstanceState);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setSaveData(Bundle data) {
        et_date.setText(data.getString("date"));
        et_result1.setText(data.getString("result1"));
        et_result2.setText(data.getString("result2"));
        sp_player11.setSelection(data.getInt("player11"));
        sp_player12.setSelection(data.getInt("player12"));
        sp_player21.setSelection(data.getInt("player21"));
        sp_player22.setSelection(data.getInt("player22"));
    }

    private void init() {
        et_date = (EditText) view.findViewById(R.id.et_date);
        Calendar cal = Calendar.getInstance();
        et_date.setText(cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));

        et_result1 = (EditText) view.findViewById(R.id.et_result1);

        et_result2 = (EditText) view.findViewById(R.id.et_result2);

        et_forecast1 = (EditText) view.findViewById(R.id.et_forecast1);

        et_forecast2 = (EditText) view.findViewById(R.id.et_forecast2);

        sp_player11 = (Spinner) view.findViewById(R.id.sp_player11);

        sp_player12 = (Spinner) view.findViewById(R.id.sp_player12);

        sp_player21 = (Spinner) view.findViewById(R.id.sp_player21);

        sp_player22 = (Spinner) view.findViewById(R.id.sp_player22);

        iv_player11 = (ImageView) view.findViewById(R.id.iv_player11);

        iv_player12 = (ImageView) view.findViewById(R.id.iv_player12);

        iv_player21 = (ImageView) view.findViewById(R.id.iv_player21);

        iv_player22 = (ImageView) view.findViewById(R.id.iv_player22);

        bt_edit = (Button) view.findViewById(R.id.bt_edit);
        if (!Tools.checkWritableDB(getActivity(), prefs))
            bt_edit.setEnabled(false);
        else
            bt_edit.setEnabled(true);
        bt_edit.setOnClickListener(this);

        players = dbmmus.getPlayerList(Settings.ORDER_NAME);

        if (players != null) {
            List<String> list = Player.getPlayerAliasList(players);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.my_spinner_text, list);
            dataAdapter.setDropDownViewResource(R.layout.my_spinner_text);
            sp_player11.setAdapter(dataAdapter);
            sp_player12.setAdapter(dataAdapter);
            sp_player21.setAdapter(dataAdapter);
            sp_player22.setAdapter(dataAdapter);
            sp_player11.setOnItemSelectedListener(this);
            sp_player12.setOnItemSelectedListener(this);
            sp_player21.setOnItemSelectedListener(this);
            sp_player22.setOnItemSelectedListener(this);
            sp_player11.setEnabled(false);
            sp_player12.setEnabled(false);
            sp_player21.setEnabled(false);
            sp_player22.setEnabled(false);

            if (game != null) {
                et_result1.setText(game.result1 + "");

                et_result2.setText(game.result2 + "");

                int p = game.forecast / 10;
                et_forecast1.setText(p + "");
                p = game.forecast - p * 10;
                et_forecast2.setText(p + "");

                String date = game.date + "";
                date = date.substring(6, 8) + "/" + date.substring(4, 6) + "/"
                        + date.substring(0, 4);
                et_date.setText(date);

                long playerId = game.player11;
                for (Player player : players)
                    if (playerId == player.id) {
                        sp_player11.setSelection(players.indexOf(player));
                        break;
                    }

                playerId = game.player12;
                for (Player player : players)
                    if (playerId == player.id) {
                        sp_player12.setSelection(players.indexOf(player));
                        break;
                    }

                playerId = game.player21;
                for (Player player : players)
                    if (playerId == player.id) {
                        sp_player21.setSelection(players.indexOf(player));
                        break;
                    }

                playerId = game.player22;
                for (Player player : players)
                    if (playerId == player.id) {
                        sp_player22.setSelection(players.indexOf(player));
                        break;
                    }

            } else {
                if (gameId == -1) {
                    edit();
                    adding = true;
                    editing = false;
                }
            }
        }
    }

    private void enableEditing() {
        sp_player11.setEnabled(true);
        sp_player12.setEnabled(true);
        sp_player21.setEnabled(true);
        sp_player22.setEnabled(true);
        et_date.setEnabled(true);
        et_result1.setEnabled(true);
        et_result2.setEnabled(true);
    }

    private void disableEditing() {
        sp_player11.setEnabled(false);
        sp_player12.setEnabled(false);
        sp_player21.setEnabled(false);
        sp_player22.setEnabled(false);
        et_date.setEnabled(false);
        et_result1.setEnabled(false);
        et_result2.setEnabled(false);
    }

    private void edit() {
        enableEditing();

        bt_edit.setText(getString(R.string.save));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_edit) {
            if (editing)
                update();
            else if (adding)
                add();
            else {
                editing = true;
                edit();
            }
        }
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        picture = (Bitmap) extras.get("data");

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkGame() {
        int p11 = sp_player11.getSelectedItemPosition();
        int p12 = sp_player12.getSelectedItemPosition();
        int p21 = sp_player21.getSelectedItemPosition();
        int p22 = sp_player22.getSelectedItemPosition();

        // Duplicated Player
        if (p11 == p12 || p11 == p21 || p11 == p22 || p12 == p21 || p12 == p22
                || p21 == p22) {
            Toast.makeText(getActivity(), getString(R.string.duplicatedplayer),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Result error
        int result1 = -1;
        try {
            result1 = Integer.parseInt(et_result1.getText().toString());
        } catch (Exception e) {
        }
        int result2 = -1;
        try {
            result2 = Integer.parseInt(et_result2.getText().toString());
        } catch (Exception e) {
        }

        if (result1 < 0 || result2 < 0 || result1 == result2) {
            Toast.makeText(getActivity(), getString(R.string.resulterror),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int date;
        String s_date = et_date.getText().toString();
        String dates[] = s_date.split("/");
        if (dates.length != 3) {
            Toast.makeText(getActivity(), getString(R.string.dateerror),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            date = Integer.parseInt(dates[0]) + Integer.parseInt(dates[1])
                    * 100 + Integer.parseInt(dates[2]) * 10000;
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.dateerror),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void add() {
        if (!checkGame())
            return;

        Tools.setDBModified(getActivity(), prefs);

        int p11 = sp_player11.getSelectedItemPosition();
        int p12 = sp_player12.getSelectedItemPosition();
        int p21 = sp_player21.getSelectedItemPosition();
        int p22 = sp_player22.getSelectedItemPosition();

        int result1 = -1;

        try {
            result1 = Integer.parseInt(et_result1.getText().toString());
        } catch (Exception e) {
        }
        int result2 = -1;

        try {
            result2 = Integer.parseInt(et_result2.getText().toString());
        } catch (Exception e) {
        }

        int date;
        String s_date = et_date.getText().toString();
        String dates[] = s_date.split("/");
        date = Integer.parseInt(dates[0]) + Integer.parseInt(dates[1]) * 100
                + Integer.parseInt(dates[2]) * 10000;

        long newId = 0;
        ContentValues initialValues = new ContentValues();
        initialValues.put("player11", players.get(p11).id + "");
        initialValues.put("player12", players.get(p12).id + "");
        initialValues.put("player21", players.get(p21).id + "");
        initialValues.put("player22", players.get(p22).id + "");
        initialValues.put("result1", result1);
        initialValues.put("result2", result2);
        initialValues.put("date", date);
        initialValues.put("forecast", forecast);

        newId = dbmmus.addGame(initialValues);

        if (newId > 0) {
            bt_edit.setText(getString(R.string.edit));
            adding = false;
            disableEditing();
            gameId = newId;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.game_added_text))
                    .setTitle(getString(R.string.game_added_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.game_added_text_error))
                    .setTitle(getString(R.string.game_added_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();
        }
    }

    private void update() {
        if (!checkGame())
            return;

        Tools.setDBModified(getActivity(), prefs);

        int p11 = sp_player11.getSelectedItemPosition();
        int p12 = sp_player12.getSelectedItemPosition();
        int p21 = sp_player21.getSelectedItemPosition();
        int p22 = sp_player22.getSelectedItemPosition();

        int result1 = -1;
        try {
            result1 = Integer.parseInt(et_result1.getText().toString());
        } catch (Exception e) {
        }
        int result2 = -1;
        try {
            result2 = Integer.parseInt(et_result2.getText().toString());
        } catch (Exception e) {
        }

        int date;
        String s_date = et_date.getText().toString();
        String dates[] = s_date.split("/");
        date = Integer.parseInt(dates[0]) + Integer.parseInt(dates[1]) * 100
                + Integer.parseInt(dates[2]) * 10000;

        long newId = 0;
        newId = dbmmus.updateGame(game.id, players.get(p11).id,
                players.get(p12).id, players.get(p21).id, players.get(p22).id,
                result1, result2, date, forecast);

        if (newId >= 0) {
            disableEditing();
            editing = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.game_update_text))
                    .setTitle(getString(R.string.game_update_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.game_update_text_error))
                    .setTitle(getString(R.string.game_update_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        switch (arg0.getId()) {
            case R.id.sp_player11:
                if (players.get(arg2).picture != null
                        && players.get(arg2).picture.length > 1)
                    iv_player11.setImageBitmap(BitmapFactory.decodeByteArray(
                            players.get(arg2).picture, 0,
                            players.get(arg2).picture.length));
                setForecast();
                break;
            case R.id.sp_player12:
                if (players.get(arg2).picture != null
                        && players.get(arg2).picture.length > 1)
                    iv_player12.setImageBitmap(BitmapFactory.decodeByteArray(
                            players.get(arg2).picture, 0,
                            players.get(arg2).picture.length));
                setForecast();
                break;
            case R.id.sp_player21:
                if (players.get(arg2).picture != null
                        && players.get(arg2).picture.length > 1)
                    iv_player21.setImageBitmap(BitmapFactory.decodeByteArray(
                            players.get(arg2).picture, 0,
                            players.get(arg2).picture.length));
                setForecast();
                break;
            case R.id.sp_player22:
                if (players.get(arg2).picture != null
                        && players.get(arg2).picture.length > 1)
                    iv_player22.setImageBitmap(BitmapFactory.decodeByteArray(
                            players.get(arg2).picture, 0,
                            players.get(arg2).picture.length));
                setForecast();
                break;

            default:
                break;
        }
    }

    private void setForecast() {
        if (!editing && !adding)
            return;

        int c1 = 0;
        int c2 = 0;

        et_forecast1.setText("");
        et_forecast2.setText("");

        try {
            int games = players.get(sp_player11.getSelectedItemPosition()).games +
                    players.get(sp_player12.getSelectedItemPosition()).games +
                    players.get(sp_player21.getSelectedItemPosition()).games +
                    players.get(sp_player22.getSelectedItemPosition()).games;

            int pl11 = sp_player11.getSelectedItemPosition();
            int pl12 = sp_player12.getSelectedItemPosition();
            int pl21 = sp_player21.getSelectedItemPosition();
            int pl22 = sp_player22.getSelectedItemPosition();

            // Duplicated Player
            if (pl11 == pl12 || pl11 == pl21 || pl11 == pl22 || pl12 == pl21 || pl12 == pl22
                    || pl21 == pl22) {
                return;
            }

            double lastRate11 = players.get(sp_player11.getSelectedItemPosition()).lastranking;
            double lastRate12 = players.get(sp_player12.getSelectedItemPosition()).lastranking;
            double lastRate21 = players.get(sp_player21.getSelectedItemPosition()).lastranking;
            double lastRate22 = players.get(sp_player22.getSelectedItemPosition()).lastranking;
            double rate11 = players.get(sp_player11.getSelectedItemPosition()).ranking;
            double rate12 = players.get(sp_player12.getSelectedItemPosition()).ranking;
            double rate21 = players.get(sp_player21.getSelectedItemPosition()).ranking;
            double rate22 = players.get(sp_player22.getSelectedItemPosition()).ranking;

            double p11 = lastRate11 + rate11;
            double p12 = lastRate12 + rate12;
            double p21 = lastRate21 + rate21;
            double p22 = lastRate22 + rate22;

            double p1 = p11 * p12;
            double p2 = p21 * p22;

            for (int i = 0; i < 3; i++) {
                if (p1 > p2) {
                    c1++;
                    p1 /= 2;
                } else {
                    c2++;
                    p2 /= 2;
                }

                if (c1 == 2 || c2 == 2)
                    break;
            }

            et_forecast1.setText(c1 + "");
            et_forecast2.setText(c2 + "");

            forecast = c1 * 10 + c2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("date", et_date.getText().toString());
        outState.putString("result1", et_result1.getText().toString());
        outState.putString("result2", et_result2.getText().toString());
        outState.putInt("player11", sp_player11.getSelectedItemPosition());
        outState.putInt("player12", sp_player12.getSelectedItemPosition());
        outState.putInt("player21", sp_player21.getSelectedItemPosition());
        outState.putInt("player22", sp_player22.getSelectedItemPosition());
        outState.putLong(Settings.ARG_GAME, gameId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        super.onPause();
    }

}
