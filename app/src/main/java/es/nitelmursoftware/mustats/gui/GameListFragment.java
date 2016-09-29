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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import es.nitelmursoftware.mustats.db.Game;
import es.nitelmursoftware.mustats.db.Player;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

public class GameListFragment extends ListFragment implements
        OnItemLongClickListener {
    List<Game> gamelist;
    List<Player> playerlist;
    List<Bitmap> pictures;
    long playerId = -1;
    private String dbname;
    DBMMus dbmmus;
    public FloatingActionButton fab;
    SharedPreferences prefs;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefs = getActivity().getSharedPreferences(
                getActivity().getPackageName() + "_preferences",
                Context.MODE_PRIVATE);

        dbname = prefs
                .getString(getActivity().getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File data = Environment.getDataDirectory();
        String currentDBPath = data + "/data/" + getActivity().getPackageName() + Settings.DB_PATH + dbname;
        dbmmus = new DBMMus(currentDBPath);

        try {
            if (savedInstanceState != null)
                playerId = savedInstanceState.getLong(Settings.ARG_PLAYER);
            else
                playerId = getArguments().getLong(Settings.ARG_PLAYER);
        } catch (Exception e) {
        }

        playerlist = dbmmus.getPlayerList(Settings.ORDER_RANKING);

        pictures = new ArrayList<Bitmap>();
        if (playerlist != null)
            for (Player p : playerlist) {
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

        gamelist = dbmmus.getGameList(0, playerId, "DESC");

        BaseAdapter adapter = new DataListAdapter();
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(this);

        if (Tools.checkWritableDB(getActivity(), prefs)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new GameFragment();
                    ((GameFragment) fragment).fab = fab;
                    fab.setVisibility(View.INVISIBLE);
                    Bundle args = new Bundle();
                    args.putLong(Settings.ARG_GAME, -1);
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
        Fragment fragment = new GameFragment();
        ((GameFragment) fragment).fab = fab;
        fab.setVisibility(View.INVISIBLE);
        Bundle args = new Bundle();
        args.putLong(Settings.ARG_GAME, gamelist.get(position).id);
        fragment.setArguments(args);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment).addToBackStack("tag");
        ft.commit();
    }

    private class DataListAdapter extends BaseAdapter {
        DataListAdapter() {
        }

        public int getCount() {
            try {
                return gamelist.size();
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
            row = inflater.inflate(R.layout.content_game_list_item, parent, false);

            Game game = gamelist.get(position);

            TextView result = (TextView) row.findViewById(R.id.tv_result);
            result.setText(game.result1 + " - " + game.result2);

            if (game.result1 > game.result2)
                ((TextView) row.findViewById(R.id.tv_winner2))
                        .setVisibility(View.INVISIBLE);
            else
                ((TextView) row.findViewById(R.id.tv_winner1))
                        .setVisibility(View.INVISIBLE);

            TextView date = (TextView) row.findViewById(R.id.tv_date);
            date.setText(game.date.subSequence(6, 8) + "/"
                    + game.date.subSequence(4, 6) + "/"
                    + game.date.subSequence(0, 4));

            int count = 0;
            if (playerlist != null)
                for (int i = 0; i < playerlist.size(); i++) {
                    ImageView picture;
                    Player p = playerlist.get(i);

                    if (p.id == game.player11) {
                        picture = (ImageView) row
                                .findViewById(R.id.iv_player11);
                        picture.setImageBitmap(pictures.get(i));
                        count++;
                    } else if (p.id == game.player12) {
                        picture = (ImageView) row
                                .findViewById(R.id.iv_player12);
                        picture.setImageBitmap(pictures.get(i));
                        count++;
                    } else if (p.id == game.player21) {
                        picture = (ImageView) row
                                .findViewById(R.id.iv_player21);
                        picture.setImageBitmap(pictures.get(i));
                        count++;
                    } else if (p.id == game.player22) {
                        picture = (ImageView) row
                                .findViewById(R.id.iv_player22);
                        picture.setImageBitmap(pictures.get(i));
                        count++;
                    }
                    if (count > 3)
                        break;
                }

            return (row);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(Settings.ARG_PLAYER, playerId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                   final int arg2, long arg3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_game_title)
                .setMessage(R.string.delete_game)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbmmus.deleteGame(gamelist.get(arg2).id);

                                ((BaseAdapter) getListAdapter())
                                        .notifyDataSetChanged();
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
