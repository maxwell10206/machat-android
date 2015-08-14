package machat.machat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Admin on 6/21/2015.
 */
public class OptionsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    OnPreferenceChange listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnPreferenceChange) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        EditTextPreference namePref = (EditTextPreference) getPreferenceManager().findPreference(getString(R.string.pref_key_name));
        namePref.setOnPreferenceChangeListener(this);
        EmailPreference emailPref = (EmailPreference) getPreferenceManager().findPreference(getString(R.string.pref_key_email));
        emailPref.setOnPreferenceChangeListener(this);
        emailPref.setEmail(mSharedPref.getString((getString(R.string.pref_key_email)), ""));
        PasswordPreference passwordPref = (PasswordPreference) getPreferenceManager().findPreference(getString(R.string.pref_key_password));
        passwordPref.setOnPreferenceChangeListener(this);
        LogoutDialogPreference logoutPref = (LogoutDialogPreference) getPreferenceManager().findPreference(getString(R.string.pref_key_logout));
        logoutPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.pref_key_name))) {
            listener.changeName((String) newValue);
            return true;
        } else if (preference.getKey().equals(getString(R.string.pref_key_email))) {
            EmailPreference.EmailChange emailChange = (EmailPreference.EmailChange) newValue;
            listener.changeEmail(emailChange.getPassword(), emailChange.getEmail());
            return true;
        } else if (preference.getKey().equals(getString(R.string.pref_key_password))) {
            PasswordPreference.PasswordChange passwordChange = (PasswordPreference.PasswordChange) newValue;
            String newPassword = passwordChange.getNewPassword();
            if (newPassword.equals(passwordChange.getRetypePassword())) {
                listener.changePassword(passwordChange.getOldPassword(), newPassword);
            } else {
                Toast.makeText(getActivity(), "New Password and Retype Password do not match.", Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (preference.getKey().equals("logout")) {
            listener.logout();
        }
        return false;
    }

    public interface OnPreferenceChange {

        void changeName(String name);

        void changeEmail(String password, String email);

        void changePassword(String oldPassword, String newPassword);

        void logout();
    }
}
