package es.nitelmursoftware.mustats.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DBMMus;
import es.nitelmursoftware.mustats.db.Player;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

public class PlayerListFragment extends ListFragment implements
        OnItemLongClickListener {
    List<Player> list;
    List<Bitmap> pictures;
    private String fragmentName = "";
    private String dbname;
    DBMMus dbmmus;
    public FloatingActionButton fab;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                getActivity().getPackageName() + "_preferences",
                Context.MODE_PRIVATE);

        dbname = prefs
                .getString(getActivity().getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File data = Environment.getDataDirectory();
        String currentDBPath = data + "/data/" + getActivity().getPackageName() + Settings.DB_PATH + dbname;
        dbmmus = new DBMMus(currentDBPath);

        list = dbmmus.getPlayerList(Settings.ORDER_RANKING);

        pictures = new ArrayList<Bitmap>();
        if (list != null)
            for (Player p : list) {
                try {
                    Bitmap b = BitmapFactory.decodeByteArray(p.picture, 0,
                            p.picture.length);
                    pictures.add(b);
                } catch (Exception e) {
                    BitmapDrawable bd = ((BitmapDrawable) getResources()
                            .getDrawable(R.drawable.ic_launcher));
                    pictures.add(bd.getBitmap());
                }
            }

        BaseAdapter adapter = new DataListAdapter();
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(this);

        if (Tools.checkWritableDB(getActivity(), prefs)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new PlayerFragment();
                    ((PlayerFragment) fragment).fab = fab;
                    fab.setVisibility(View.INVISIBLE);
                    Bundle args = new Bundle();
                    args.putLong(Settings.ARG_PLAYER, -1);
                    fragment.setArguments(args);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.contentPanel, fragment).addToBackStack("tag");
                    ft.commit();
                }
            });
        } else {
            fab.setVisibility(View.INVISIBLE);
            fab.setOnClickListener(null);
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment fragment = new PlayerFragment();
        ((PlayerFragment) fragment).fab = fab;
        fab.setVisibility(View.INVISIBLE);

        Bundle args = new Bundle();
        args.putLong(Settings.ARG_PLAYER, list.get(position).id);
        fragment.setArguments(args);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment).addToBackStack("tag");
        ft.commit();
    }

    class DataListAdapter extends BaseAdapter {
        DataListAdapter() {
        }

        public int getCount() {
            try {
                return list.size();
            } catch (Exception e) {
                return 0;
            }
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.content_player_list_item, parent, false);

            Player player = list.get(position);

            TextView name = (TextView) row.findViewById(R.id.tv_name);

            double lastranking = 0;
            try {
                lastranking = (double) player.lastwins / player.lastgames;
            } catch (Exception e) {
            }

            if (player.lastranking < lastranking) {
                name.setTextColor(Color.RED);
            } else
                name.setTextColor(Color.BLACK);

            int[] t_levelvalues;
            String[] t_level;
            t_levelvalues = getResources().getIntArray(R.array.level_value);
            t_level = getResources().getStringArray(R.array.level_name);

            int i;
            for (i = t_levelvalues.length - 1; i >= 0; i--) {
                if (player.ranking * 100 > t_levelvalues[i])
                    break;
            }

            int j;
            for (j = t_levelvalues.length - 1; j >= 0; j--) {
                if (player.lastranking * 100 > t_levelvalues[j])
                    break;
            }

            if (i < 0 && player.games > 0)
                i = 0;

            if (j < 0 && player.lastgames > 0)
                j = 0;

            String text = "";
            if (j < 0)
                text = "-- ";
            else
                text = t_level[j] + " ("
                        + String.format("%.2f", player.lastranking * 100)
                        + " %)";

            text = text + " / ";

            if (i < 0)
                text = text + "-- ";
            else
                text = text + t_level[i] + " ("
                        + String.format("%.2f", player.ranking * 100) + " %)";

            name.setText(player.name + "\n" + player.alias + "\n" + text);

            ImageView picture = (ImageView) row.findViewById(R.id.iv_picture);
            picture.setImageBitmap(pictures.get(position));

            return (row);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                   final int arg2, long arg3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_player_title)
                .setMessage(R.string.set_player)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences pref = getActivity()
                                        .getSharedPreferences("muspref",
                                                Context.MODE_PRIVATE);
                                Editor editor = pref.edit();
                                editor.putLong(Settings.ARG_ME,
                                        list.get(arg2).id);
                                editor.commit();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
        builder.show();

        return true;
    }

}
