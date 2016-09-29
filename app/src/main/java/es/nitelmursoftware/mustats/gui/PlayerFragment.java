package es.nitelmursoftware.mustats.gui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBMMus;
import es.nitelmursoftware.mustats.db.Game;
import es.nitelmursoftware.mustats.db.Player;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

public class PlayerFragment extends Fragment implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    private long playerId;
    Player player;
    EditText et_name;
    EditText et_alias;
    EditText et_level_current;
    EditText et_level;
    EditText et_coffee;
    EditText et_days;
    EditText et_periods;
    EditText et_positives_negatives;
    EditText et_mate;
    EditText et_matches;
    ImageView iv_picture;
    Bitmap picture;
    Button bt_edit;
    Button bt_games;
    Switch sw_abs_par;
    boolean editing = false;
    boolean adding = false;
    private String fragmentName = "";
    private String dbname;
    DBMMus dbmmus;
    public FloatingActionButton fab;
    SharedPreferences prefs;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_player, container, false);

        if (savedInstanceState != null)
            playerId = savedInstanceState.getLong(Settings.ARG_PLAYER);
        else
            playerId = getArguments().getLong(Settings.ARG_PLAYER);

        prefs = getActivity().getSharedPreferences(
                getActivity().getPackageName() + "_preferences",
                Context.MODE_PRIVATE);

        dbname = prefs
                .getString(getActivity().getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File data = Environment.getDataDirectory();
        String currentDBPath = data + "/data/" + getActivity().getPackageName() + Settings.DB_PATH + "mus.sqlite";
        dbmmus = new DBMMus(currentDBPath);

        player = dbmmus.getPlayer(playerId);

        view = rootView;

        init();

        if (savedInstanceState != null)
            setSaveData(savedInstanceState);

        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(null);

        return rootView;
    }

    private void init() {
        et_name = (EditText) view.findViewById(R.id.et_name);

        et_alias = (EditText) view.findViewById(R.id.et_alias);

        et_level_current = (EditText) view.findViewById(R.id.et_level_current);
        et_level = (EditText) view.findViewById(R.id.et_level);

        et_coffee = (EditText) view.findViewById(R.id.et_coffee);

        et_days = (EditText) view.findViewById(R.id.et_days);
        et_matches = (EditText) view.findViewById(R.id.et_matches);
        et_positives_negatives = (EditText) view.findViewById(R.id.et_positives_negatives);
        et_periods = (EditText) view.findViewById(R.id.et_periods);
        et_mate = (EditText) view.findViewById(R.id.et_mate);

        bt_edit = (Button) view.findViewById(R.id.bt_edit);
        if (!Tools.checkWritableDB(getActivity(), prefs))
            bt_edit.setEnabled(false);
        else
            bt_edit.setEnabled(true);
        bt_edit.setOnClickListener(this);

        bt_games = (Button) view.findViewById(R.id.bt_games);
        if (!Tools.checkWritableDB(getActivity(), prefs))
            bt_games.setEnabled(false);
        else
            bt_games.setEnabled(true);
        bt_games.setOnClickListener(this);

        sw_abs_par = (Switch) view.findViewById(R.id.sw_abs_par);
        sw_abs_par.setOnCheckedChangeListener(this);

        iv_picture = (ImageView) view.findViewById(R.id.iv_picture);

        if (player != null) {
            et_name.setText(player.name);

            et_alias.setText(player.alias);

            if (player.picture != null && player.picture.length > 1)
                iv_picture.setImageBitmap(BitmapFactory.decodeByteArray(
                        player.picture, 0, player.picture.length));

            //level current
            double lastranking = 0;
            try {
                lastranking = (double) player.lastwins / player.lastgames;
            } catch (Exception e) {
            }
            if (player.lastranking < lastranking)
                et_level_current.setTextColor(Color.RED);
            else
                et_level_current.setTextColor(Color.BLACK);

            int[] t_levelvalues;
            String[] t_level;
            t_levelvalues = getResources().getIntArray(R.array.level_value);
            t_level = getResources().getStringArray(R.array.level_name);
            int j;
            for (j = t_levelvalues.length - 1; j >= 0; j--) {
                if (player.lastranking * 100 > t_levelvalues[j])
                    break;
            }

            if (j < 0 && player.lastgames > 0)
                j = 0;

            String text = "";
            if (j < 0)
                text = "-- ";
            else
                text = t_level[j] + " ("
                        + String.format("%.2f", player.lastranking * 100)
                        + " %)";

            if (player.lastranking < lastranking) {
                String minimumgames = "";
                String minimumdays = "";
                try {
                    minimumgames = dbmmus.getParameter(Settings.PARAM_MINIMUMGAMES).map
                            .get(Settings.PARAM_MINIMUMGAMES);
                    minimumdays = dbmmus.getParameter(Settings.PARAM_MINIMUMPLAYINGDAYS).map
                            .get(Settings.PARAM_MINIMUMPLAYINGDAYS);
                } catch (Exception e) {
                }
                text = text
                        + "("
                        + minimumgames.substring(0,
                        minimumgames.indexOf(".") + 2) + "/" + minimumdays + ")";
            }

            et_level_current.setText(text);

            int i;
            for (i = t_levelvalues.length - 1; i >= 0; i--) {
                if (player.ranking * 100 > t_levelvalues[i])
                    break;
            }

            if (i < 0 && player.games > 0)
                i = 0;

            text = "";

            if (i < 0)
                text = text + "-- ";
            else
                text = text + t_level[i] + " ("
                        + String.format("%.2f", player.ranking * 100) + " %)";

            et_level.setText(text);

            double coffee_prize = 1;
            double lastPrice = coffee_prize * (1 - (2 * player.lastwins - player.lastgames) / (double) player.lastdays);
            double price = coffee_prize * (1 - (2 * player.wins - player.games) / (double) player.days);
            et_coffee.setText((player.lastgames > 0 ? String.format("%.2f", lastPrice) : "--") + " € / " + (player.games > 0 ? String.format("%.2f", price) : "--") + " €");

            et_days.setText(player.lastdays + " (" + Tools.df_1.format((double) player.lastgames / player.lastdays) + ") / " + player.days + " (" + Tools.df_1.format((double) player.games / player.days) + ")");

            et_matches.setText(player.lastwins + " (" + player.wins + ") / " + player.lastgames + " (" + player.games + ")");

            et_positives_negatives.setText(player.positives + " / "
                    + player.negatives);

            et_periods.setText(player.rafale_wins + " / " + player.rafale_losts
                    + " (" + player.rafale + ")");

            String mate = "";
            Player p = dbmmus.getPlayer(player.bestmate);
            try {
                mate += p.alias;
            } catch (Exception e) {
                mate += "--";
            }
            mate += " / ";
            p = dbmmus.getPlayer(player.worstmate);
            try {
                mate += p.alias;
            } catch (Exception e) {
                mate += "--";
            }
            et_mate.setText(mate);
        } else {
            if (playerId == -1) {
                adding = true;
                editing = false;
                edit();
            }
        }

        int daysBefore = 30;
        try {
            daysBefore = Integer.parseInt(dbmmus.getParameter(Settings.PARAM_DAYSPARTIAL).map.get(Settings.PARAM_DAYSPARTIAL));
        } catch (Exception e) {
        }

        drawGhraph(daysBefore);
    }

    private void enableEditing() {
        et_name.setEnabled(true);
        et_alias.setEnabled(true);
        iv_picture.setOnClickListener(this);
    }

    private void disableEditing() {
        et_name.setEnabled(false);
        et_alias.setEnabled(false);
        iv_picture.setOnClickListener(null);
    }

    private void edit() {
        enableEditing();

        bt_edit.setText(getString(R.string.save));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_picture) {
            takePicture();
        } else if (v.getId() == R.id.bt_edit) {
            if (editing)
                updatePlayer();
            else if (adding)
                addPlayer();
            else {
                editing = true;
                edit();
            }
        } else if (v.getId() == R.id.bt_games) {
            Fragment fragment = new GameListFragment();
            ((GameListFragment) fragment).fab = fab;
            fab.setVisibility(View.INVISIBLE);

            Bundle bundle = new Bundle();
            bundle.putLong(Settings.ARG_PLAYER, playerId);
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.contentPanel, fragment).addToBackStack("tag")
                    .commit();

        }
    }

    private void takePicture() {
        if (isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE)) {
            dispatchTakePictureIntent(0);
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
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

        // setPicRes();

        iv_picture.setImageBitmap(picture);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addPlayer() {
        Tools.setDBModified(getActivity(), prefs);

        byte[] pictureByteArray = null;
        try {
            picture = ((BitmapDrawable) iv_picture.getDrawable()).getBitmap();

            if (picture != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                pictureByteArray = stream.toByteArray();
            }
        } catch (Exception e) {
            picture = null;
        }
        long newPlayerId = dbmmus.addPlayer(et_name.getText().toString(),
                et_alias.getText().toString(), pictureByteArray);

        if (newPlayerId > 0) {
            bt_edit.setText(getString(R.string.edit));
            adding = false;
            disableEditing();
            playerId = newPlayerId;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.player_added_text))
                    .setTitle(getString(R.string.player_added_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();


            // Fragment fragment = new PlayerFragment();
            // Bundle args = new Bundle();
            // args.putLong(Settings.ARG_PLAYER, newPlayerId);
            // fragment.setArguments(args);
            // getActivity().getSupportFragmentManager().beginTransaction()
            // .replace(R.id.frame_main, fragment).commit();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.player_added_text_error))
                    .setTitle(getString(R.string.player_added_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();

            Fragment fragment = new PlayerFragment();
            Bundle args = new Bundle();
            args.putLong(Settings.ARG_PLAYER, newPlayerId);
            fragment.setArguments(args);
            //getActivity().getSupportFragmentManager().beginTransaction()
            //      .replace(R.id.contentPanel, fragment).commit();
        }
    }

    private void updatePlayer() {
        Tools.setDBModified(getActivity(), prefs);

        picture = ((BitmapDrawable) iv_picture.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] pictureByteArray = stream.toByteArray();

        ContentValues initialValues = new ContentValues();
        initialValues.put("name", et_name.getText().toString());
        initialValues.put("alias", et_alias.getText().toString());
        initialValues.put("picture", pictureByteArray);

        long newPlayerId = dbmmus.updatePlayer(initialValues, player.id);

        if (newPlayerId >= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.player_update_text))
                    .setTitle(getString(R.string.player_update_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    disableEditing();
                                    dialog.cancel();
                                }
                            }).show();

            // Fragment fragment = new PlayerFragment();
            // Bundle args = new Bundle();
            // args.putLong(Settings.ARG_PLAYER, player.id);
            // fragment.setArguments(args);
            // getActivity().getSupportFragmentManager().beginTransaction()
            // .replace(R.id.frame_main, fragment).commit();

            editing = false;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(getString(R.string.player_update_text_error))
                    .setTitle(getString(R.string.player_update_title))
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            }).show();

            Fragment fragment = new PlayerFragment();
            Bundle args = new Bundle();
            args.putLong(Settings.ARG_PLAYER, newPlayerId);
            fragment.setArguments(args);
            //getActivity().getSupportFragmentManager().beginTransaction()
            //      .replace(R.id.contentPanel, fragment).commit();
        }
    }

    private void setSaveData(Bundle data) {
        et_name.setText(data.getString("name"));
        et_alias.setText(data.getString("alias"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(Settings.ARG_PLAYER, playerId);
        outState.putString("name", et_name.getText().toString());
        outState.putString("alias", et_alias.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void drawGhraph(int daysBefore) {
        List<Game> games;

        Calendar lastday = Calendar.getInstance();
        lastday.add(Calendar.DATE, -daysBefore);
        games = dbmmus.getGameList(dbmmus.getLastday(daysBefore), playerId, "ASC");

        if (games == null || games.size() == 0)
            return;

        String firsDate = games.get(0).date.substring(6, 8) + "/" + games.get(0).date.substring(4, 6) + "/" + games.get(0).date.substring(0, 4);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(df.parse(firsDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (calendar.after(lastday))
            lastday = calendar;

        if (games == null) return;

        ArrayList<Entry> wins_entries = new ArrayList<>();
        ArrayList<Entry> looses_entries = new ArrayList<>();
        ArrayList<Entry> dif_entries = new ArrayList<>();
        ArrayList<Entry> cero_entries = new ArrayList<>();
        List<String> labels = new ArrayList<String>();

        int wins = 0;
        int looses = 0;
        int entryPos = 0;
        int totalWins = 0;

        while ((lastday.get(Calendar.YEAR) * 10000
                + (lastday.get(Calendar.MONTH) + 1) * 100
                + lastday.get(Calendar.DAY_OF_MONTH)) < Integer.parseInt(games.get(0).date)) {
            wins_entries.add(new Entry(0, entryPos));
            looses_entries.add(new Entry(0, entryPos));
            dif_entries.add(new Entry(0, entryPos));
            cero_entries.add(new Entry(0, entryPos++));

            String date = lastday.get(Calendar.DAY_OF_MONTH) + "/" + (lastday.get(Calendar.MONTH) + 1);
            labels.add(date);

            lastday.add(Calendar.DATE, 1);
        }

        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);

            if (checkVictory(game, player) > 0)
                wins++;
            else
                looses--;


            Game next = null;
            try {
                next = games.get(i + 1);
            } catch (Exception e) {
            }

            if (next == null || !game.date.equals(next.date)) {
                totalWins = totalWins + wins + looses;
                wins_entries.add(new Entry(wins, entryPos));
                looses_entries.add(new Entry(looses, entryPos));
                dif_entries.add(new Entry(totalWins, entryPos));
//                dif_entries.add(new Entry(wins + looses, entryPos));
                cero_entries.add(new Entry(0, entryPos++));

                String date = games.get(i).date.substring(6, 8) + "/" + games.get(i).date.substring(4, 6);
                labels.add(date);

                wins = 0;
                looses = 0;
                lastday.add(Calendar.DATE, 1);
            } else
                continue;

            try {
                while ((lastday.get(Calendar.YEAR) * 10000
                        + (lastday.get(Calendar.MONTH) + 1) * 100
                        + lastday.get(Calendar.DAY_OF_MONTH)) < Integer.parseInt(next.date)) {
                    wins_entries.add(new Entry(0, entryPos));
                    looses_entries.add(new Entry(0, entryPos));
                    dif_entries.add(new Entry(totalWins, entryPos));
//                    dif_entries.add(new Entry(0, entryPos));
                    cero_entries.add(new Entry(0, entryPos++));

                    String date = lastday.get(Calendar.DAY_OF_MONTH) + "/" + (lastday.get(Calendar.MONTH) + 1);
                    labels.add(date);

                    lastday.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
            }
        }

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.YEAR) * 10000
                + (cal.get(Calendar.MONTH) + 1) * 100
                + cal.get(Calendar.DAY_OF_MONTH);

        while ((lastday.get(Calendar.YEAR) * 10000
                + (lastday.get(Calendar.MONTH) + 1) * 100
                + lastday.get(Calendar.DAY_OF_MONTH)) < day) {
            wins_entries.add(new Entry(0, entryPos));
            looses_entries.add(new Entry(0, entryPos));
            dif_entries.add(new Entry(totalWins, entryPos));
//            dif_entries.add(new Entry(0, entryPos));
            cero_entries.add(new Entry(0, entryPos++));

            String date = lastday.get(Calendar.DAY_OF_MONTH) + "/" + (lastday.get(Calendar.MONTH) + 1);
            labels.add(date);

            lastday.add(Calendar.DATE, 1);
        }

        LineDataSet lDataSet1 = new LineDataSet(wins_entries, "Victorias");
        lDataSet1.setColor(Color.GREEN);
        lDataSet1.setCircleRadius(0);
        lDataSet1.setDrawValues(false);

        LineDataSet lDataSet2 = new LineDataSet(looses_entries, "Pérdidas");
        lDataSet2.setColor(Color.RED);
        lDataSet2.setCircleRadius(0);
        lDataSet2.setDrawValues(false);

        LineDataSet lDataSet3 = new LineDataSet(dif_entries, "Acumulado");
        lDataSet3.setColor(Color.BLUE);
        lDataSet3.setCircleRadius(0);
        lDataSet3.setDrawValues(false);

        LineDataSet lDataSet4 = new LineDataSet(cero_entries, "0");
        lDataSet4.setColor(Color.BLACK);
        lDataSet4.setCircleRadius(0);
        lDataSet4.setDrawValues(false);

        List<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        lines.add(lDataSet1);
        lines.add(lDataSet2);
        lines.add(lDataSet3);
        lines.add(lDataSet4);

        LineData ld = new LineData(labels, lines);

        LineChart lineChart = (LineChart) view.findViewById(R.id.lc_chart);
        lineChart.setDescription("Partidas diarias");
        lineChart.setData(ld);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private int checkVictory(Game game, Player player) {
        int result = 0;
        int couple = 0;

        try {
            if (game.player11 == player.id)
                couple = 1;
            else if (game.player12 == player.id)
                couple = 1;
            else if (game.player21 == player.id)
                couple = 2;
            else if (game.player22 == player.id)
                couple = 2;

            int winner = game.result1 > game.result2 ? 1 : 2;
            result = winner == couple ? 1 : -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int daysBefore = 30;

        try {
            if (!isChecked)
                daysBefore = Integer.parseInt(dbmmus.getParameter(Settings.PARAM_DAYSPARTIAL).map.get(Settings.PARAM_DAYSPARTIAL));
            else
                daysBefore = Integer.parseInt(dbmmus.getParameter(Settings.PARAM_DAYSABOSULTE).map.get(Settings.PARAM_DAYSABOSULTE));
        } catch (Exception e) {
        }

        drawGhraph(daysBefore);
    }
}
