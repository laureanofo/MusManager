package es.nitelmursoftware.mustats.gui;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBMMus;
import es.nitelmursoftware.mustats.db.Game;
import es.nitelmursoftware.mustats.db.Player;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

public class BestPlayerFragment extends Fragment implements OnClickListener {
    private long playerId;
    Player player;
    TextView tv_alias;
    TextView tv_date;
    ImageView iv_picture;
    Bitmap picture;
    Button bt_left;
    Button bt_right;
    private String dbname;
    DBMMus dbmmus;
    public FloatingActionButton fab;
    SharedPreferences prefs;
    Calendar month;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_monthly_best_player, container, false);

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

        month = Calendar.getInstance();

        init(rootView);

        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(null);

        return rootView;
    }

    private void init(View view) {
        playerId = dbmmus.getBestPlayer(month);

        player = dbmmus.getPlayer(playerId);

        tv_alias = (TextView) view.findViewById(R.id.tv_alias);

        tv_date = (TextView) view.findViewById(R.id.tv_date);

        bt_left = (Button) view.findViewById(R.id.bt_left);
        bt_left.setOnClickListener(this);

        bt_right = (Button) view.findViewById(R.id.bt_right);
        bt_right.setOnClickListener(this);

        iv_picture = (ImageView) view.findViewById(R.id.iv_picture);

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(month.getTime());

        tv_date.setText(month_name + " " + month.get(Calendar.YEAR));

        if (player != null) {
            tv_alias.setText(player.alias);

            if (player.picture != null && player.picture.length > 1)
                iv_picture.setImageBitmap(BitmapFactory.decodeByteArray(
                        player.picture, 0, player.picture.length));
        } else {
            tv_alias.setText("");

            iv_picture.setImageResource(0);

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_left) {
            month.add(Calendar.MONTH, -1);
            init(rootView);
        } else if (v.getId() == R.id.bt_right) {
            month.add(Calendar.MONTH, 1);
            init(rootView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(Settings.ARG_PLAYER, playerId);

        super.onSaveInstanceState(outState);
    }
}
