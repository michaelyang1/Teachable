package navdrawerfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.teachable.R
import com.google.firebase.auth.FirebaseAuth
import registerlogin.LoginActivity
import registerlogin.RegisterActivity

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        Log.e("TAG", "settings fragment generated successfully")

        // sign out preference
        val logOutPref = findPreference<Preference>("LogOutPrefKey")
        logOutPref?.setOnPreferenceClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }
    }
}