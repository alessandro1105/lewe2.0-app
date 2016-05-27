package com.lewetechnologies.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.lewetechnologies.app.R;
import com.lewetechnologies.app.configs.Config;
import com.lewetechnologies.app.services.BluetoothSerialService;

/**
 * Created by alessandro on 22/05/16.
 */
public class SettingsBandPreferenceFragment extends PreferenceFragment {

    //preferenza status
    Preference status;

    //pereferenza bandName
    Preference bandName;

    //shared preference
    SharedPreferences preferences;


    public static SettingsBandPreferenceFragment newInstance() {

        //istanzio il fragment
        SettingsBandPreferenceFragment fragment = new SettingsBandPreferenceFragment();

        //ritorno il fragment
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_band);

        //prelevo le shared preference
        preferences = getActivity().getApplicationContext().getSharedPreferences(Config.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);

        //prelevo le preferenze della vista
        status = (Preference) findPreference("status");
        bandName = (Preference) findPreference("band_name");

        //setto il summary di status disconnesso di base
        status.setSummary(getString(R.string.activity_settings_band_status_summary_disconnected));

        //setto il nome del band associato
        if (!preferences.getString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, "").equals("")) {
            bandName.setSummary(preferences.getString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, ""));

        //nessun band associato
        } else {
            bandName.setSummary(getString(R.string.activity_settings_band_not_associated));
        }

        //on click preferenza dissocia
        Preference dissociate = (Preference) findPreference("dissociate");
        dissociate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //dissocio la band
                dissociateLeweBand();

                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //setto il nome del band associato
        if (!preferences.getString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, "").equals("")) {
            bandName.setSummary(preferences.getString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, ""));

            //nessun band associato
        } else {
            bandName.setSummary(getString(R.string.activity_settings_band_not_associated));
        }
    }

    private void sendExitRequest() {
        //ritorno la richiesta di chiusura dell'app
        Intent exitIntent = new Intent();
        getActivity().setResult(Config.RESULT_EXIT_CODE, exitIntent);
        getActivity().finish();
    }

    private void dissociateLeweBand() {
        //accedo alle shared preference
        SharedPreferences.Editor editor = preferences.edit();

        //cancello le preferenze di associazione
        editor.putString(Config.SHARED_PREFERENCE_KEY_DEVICE_MAC, ""); //elimino il mac
        editor.putString(Config.SHARED_PREFERENCE_KEY_BAND_NAME_ASSOCIATED, ""); //indico che nessun band è associato

        //salvo
        editor.commit();

        //cambio il summary della preferenza bandName
        bandName.setSummary(getString(R.string.activity_settings_band_not_associated));

        //cambio lo stato del band
        status.setSummary(getString(R.string.activity_settings_band_status_summary_disconnected));

        //toast che indica la dissociazione
        Toast.makeText(getActivity(), getString(R.string.activity_settings_band_not_associated_toast),Toast.LENGTH_LONG).show();

        //invio il comando di disconnessione
        sendDisconnectionCommand();
    }

    //invia il comando di disconnessione
    private void sendDisconnectionCommand() {
        Intent intent = new Intent(BluetoothSerialService.COMMAND_DISCONNECT);
        getActivity().sendBroadcast(intent);
    }

}
