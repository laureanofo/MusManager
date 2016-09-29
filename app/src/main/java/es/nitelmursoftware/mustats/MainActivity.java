package es.nitelmursoftware.mustats;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.nitelmursoftware.musmanager.R;
import es.nitelmursoftware.mustats.db.DB;
import es.nitelmursoftware.mustats.db.DBMMus;
import es.nitelmursoftware.mustats.db.Game;
import es.nitelmursoftware.mustats.gui.BestPlayerFragment;
import es.nitelmursoftware.mustats.gui.GameListFragment;
import es.nitelmursoftware.mustats.gui.PlayerFragment;
import es.nitelmursoftware.mustats.gui.PlayerListFragment;
import es.nitelmursoftware.mustats.gui.SettingsFragment;
import es.nitelmursoftware.mustats.helper.DBDownload;
import es.nitelmursoftware.mustats.helper.DBUpload;
import es.nitelmursoftware.mustats.helper.Settings;
import es.nitelmursoftware.mustats.helper.Tools;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    List<Game> gamelist;
    FloatingActionButton fab;
    int window = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loadDB();
        new DBDownload(this).download();

        String db_path;
        String db_name;

        SharedPreferences prefs = getSharedPreferences(
                getPackageName() + "_preferences",
                Context.MODE_PRIVATE);
        db_path = Environment.getDataDirectory() + "/data/" + getPackageName() + Settings.DB_PATH;
        db_name = prefs
                .getString(getString(R.string.pref_dbname_key),
                        "mus.sqlite");

        File db = new File(db_path + db_name);
        if (db.exists())
            Tools.recalculate(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        showPlayerView(-1);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getFragmentManager();
            int a = fm.getBackStackEntryCount();
            if (a > 0)
                fm.popBackStack();
            else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (window) {
            case 1:
                // Inflate the menu; this adds items to the action bar if it is present.
                //getMenuInflater().inflate(R.menu.player, menu);
                break;
            case 2:
                // Inflate the menu; this adds items to the action bar if it is present.
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_player) {
            showPlayerView(-1);
        } else if (id == R.id.nav_games) {
            showGameListView();
        } else if (id == R.id.nav_players) {
            showPlayerListView();
        } else if (id == R.id.nav_best_player) {
            showBestPlayerView();
        } else if (id == R.id.nav_manage) {
            showPreferences();
            //            startActivity(new Intent(this, SettingsActivity.class));
            //recalculate();
        } else if (id == R.id.nav_upload) {
            new DBUpload(this).upload();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadDB() {
        try {
            SharedPreferences prefs = getSharedPreferences(
                    getPackageName() + "_preferences",
                    Context.MODE_PRIVATE);
            String db_name = prefs
                    .getString(getString(R.string.pref_dbname_key),
                            "mus.sqlite");

            DB.createDataBase(this, Settings.DB_PATH, db_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Show Views
    private void showPlayerView(long playerId) {
        Fragment fragment = new PlayerFragment();
        ((PlayerFragment) fragment).fab = fab;

        Bundle args = new Bundle();
        SharedPreferences pref = getSharedPreferences("muspref",
                Context.MODE_PRIVATE);
        if (playerId < 0)
            args.putLong(Settings.ARG_PLAYER, pref.getLong(Settings.ARG_ME, 0));
        else
            args.putLong(Settings.ARG_PLAYER, playerId);

        fragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment);
        ft.commit();
    }

    private void showBestPlayerView() {
        clearBackStack();

        Fragment fragment = new BestPlayerFragment();
        ((BestPlayerFragment) fragment).fab = fab;

        Bundle args = new Bundle();
        SharedPreferences pref = getSharedPreferences("muspref",
                Context.MODE_PRIVATE);

        fragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment);
        ft.commit();
    }

    private void showGameListView() {
        clearBackStack();

        Fragment fragment = new GameListFragment();
        ((GameListFragment) fragment).fab = fab;

        Bundle args = new Bundle();
        args.putLong(Settings.ARG_PLAYER, -1);

        SharedPreferences pref = getSharedPreferences("muspref",
                Context.MODE_PRIVATE);

        fragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment);
        ft.commit();
    }

    private void showPlayerListView() {
        clearBackStack();

        Fragment fragment = new PlayerListFragment();
        ((PlayerListFragment) fragment).fab = fab;

        Bundle args = new Bundle();

        fragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment);
        ft.commit();
    }

    private void showPreferences() {
        clearBackStack();

        Fragment fragment = new es.nitelmursoftware.mustats.gui.SettingsFragment();
        ((SettingsFragment) fragment).fab = fab;

        Bundle args = new Bundle();

        fragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentPanel, fragment);
        ft.commit();
    }

    private void clearBackStack() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fm.getBackStackEntryAt(0);
            fm.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
