package es.nitelmursoftware.mustats;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.Game;
import es.nitelmursoftware.mustats.db.Player;

/**
 * Created by lferolm on 3/7/16.
 */
public class DataListAdapter extends BaseAdapter {
    Activity activity;
    List<Game> gamelist;
    List<Player> playerlist;
    List<Bitmap> pictures;

    DataListAdapter(Activity activity, List<Game> gamelist, List<Player> playerlist,
                    List<Bitmap> pictures) {
        this.activity = activity;
        this.gamelist = gamelist;
        this.playerlist=playerlist;
        this.pictures=pictures;
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
        LayoutInflater inflater = activity.getLayoutInflater();
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