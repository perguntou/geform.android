package br.ufrj.del.geform.app;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import br.ufrj.del.geform.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public static final String KEY_USER = "user";

	/*
	 * (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.preferences );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		final SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
		sharedPreferences.registerOnSharedPreferenceChangeListener( this );
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onPause() {
		super.onPause();
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		final SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
		sharedPreferences.unregisterOnSharedPreferenceChangeListener( this );
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
		if( KEY_USER.equals( key ) ) {
			final Editor editor = sharedPreferences.edit();
			final String value = sharedPreferences.getString( key, "" );
			final String trimValue = value.trim();
			if( "".equals( trimValue ) ) {
				editor.remove( key );
			} else {
				editor.putString( key, trimValue );
			}
			editor.apply();
		}
	}

}
