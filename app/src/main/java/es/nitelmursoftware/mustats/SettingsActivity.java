package es.nitelmursoftware.mustats;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import es.nitelmursoftware.musmanager.R;

public class SettingsActivity extends PreferenceFragment implements
		OnPreferenceChangeListener {
	// ListPreference itemList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.settings);

		// SharedPreferences pref = getSharedPreferences(getPackageName()
		// + "_preferences", Context.MODE_PRIVATE);

		// itemList = (ListPreference)
		// findPreference(getString(R.string.pref_connection_key));
		// itemList.setOnPreferenceChangeListener(this);
		// ((EditTextPreference)
		// findPreference(getString(R.string.pref_days_last_key)))
		// .setOnPreferenceChangeListener(this);

		// String connection = pref.getString(
		// getString(R.string.pref_connection_key), "");
		// init(connection);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// init(newValue.toString());
int a =1;

		return true;
	}

	// private void init(String val) {
	// int index = itemList.findIndexOfValue(val);
	//
	// if (index == 0) {// DROPBOX
	// findPreference("ftpuser").setEnabled(false);
	// findPreference("ftppassword").setEnabled(false);
	// findPreference("url").setEnabled(false);
	// findPreference("dbname").setEnabled(true);
	// findPreference("dropboxfile").setEnabled(true);
	// } else if (index == 1) {// FTP
	// findPreference("ftpuser").setEnabled(true);
	// findPreference("ftppassword").setEnabled(true);
	// findPreference("url").setEnabled(true);
	// findPreference("dbname").setEnabled(true);
	// findPreference("dropboxfile").setEnabled(false);
	// } else if (index == 2) {// HTTP
	// findPreference("ftpuser").setEnabled(false);
	// findPreference("ftppassword").setEnabled(false);
	// findPreference("url").setEnabled(true);
	// findPreference("dbname").setEnabled(true);
	// findPreference("dropboxfile").setEnabled(false);
	// }
	//
	// }
}
