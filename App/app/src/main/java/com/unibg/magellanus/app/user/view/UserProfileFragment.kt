package com.unibg.magellanus.app.user.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.databinding.FragmentUserProfileBinding
import com.unibg.magellanus.app.auth.impl.FirebaseAuthenticationProvider
import com.unibg.magellanus.app.user.model.UserAccountAPI
import com.unibg.magellanus.app.user.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class UserProfileFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.SummaryProvider<ListPreference> {

    private val provider = FirebaseAuthenticationProvider()
    private val api = UserAccountAPI.create(provider)

    private val viewModel by viewModels<UserProfileViewModel> {
        UserProfileViewModel.Factory(provider, api)
    }

    private lateinit var navController: NavController

    private lateinit var prefs: SharedPreferences
    private var wasSyncing by Delegates.notNull<Boolean>()

    private lateinit var syncPrefs: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.user_profile_settings, SettingsFragment())
            .commit() // inserisce la schermata dei settings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment.
        val binding = DataBindingUtil.inflate<FragmentUserProfileBinding>(
            inflater, R.layout.fragment_user_profile, container, false
        )

        binding.logoutBtn.setOnClickListener { signOut() }
        binding.deleteAccountBtn.setOnClickListener { deleteUserAccount() }

        binding.viewModel = viewModel
        binding.userProfileHeader.apply {
            lifecycleOwner = viewLifecycleOwner
            user = viewModel.currentUser
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
        navController = findNavController()
        prefs =
            PreferenceManager.getDefaultSharedPreferences(requireContext())

        syncPrefs = resources.getResourceEntryName(R.id.sync_prefs)

        wasSyncing = prefs.getBoolean(syncPrefs, false)

        viewModel.syncedPreferences.observe(viewLifecycleOwner) {
            val shouldSync = it?.get(syncPrefs) as Boolean?
            // legge e sincronizza le preferenze
            if (shouldSync == true) {
                it?.map { (key, value) ->
                    {
                        prefs.edit().apply {
                            when (value) {
                                is Int -> putInt(key, value)
                                is String -> putString(key, value)
                                is Boolean -> putBoolean(key, value)
                            }
                        }.commit()
                    }
                }
            }
        }

        viewModel.getPreferences()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val isSyncing = prefs.getBoolean(syncPrefs, false)
            val preferences = if (isSyncing) {
                getImportantPreferences(prefs)
            } else if (!isSyncing && wasSyncing) {
                emptyMap<String, Any>()
            } else null

            // sincronizza le preferenze
            requireActivity().lifecycleScope.launch {
                preferences?.let {
                    viewModel.savePreferences(it)
                }
            }

            navController.popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    // costruisce la mappa contenente le preferenze
    private fun getImportantPreferences(prefs: SharedPreferences): Map<String, Any?> {
        val darkMode = resources.getResourceEntryName(R.id.dark_mode_pref)
        return buildMap {
            put(syncPrefs, prefs.getBoolean(syncPrefs, false))
            put(darkMode, prefs.getString(darkMode, getString(R.string.dark_mode_def_value)))
        }
    }

    private fun deleteUserAccount() {
        viewModel.successfullyDeleted.observe(viewLifecycleOwner) {
            if (it) {
                AuthUI.getInstance().signOut(requireContext())
                navController.navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment())
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                it,
                Snackbar.LENGTH_LONG
            ).show()
        }
        viewModel.delete()
        AuthUI.getInstance().delete(requireContext())
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(requireContext())
        navController.navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment())
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val darkModeString = getString(R.string.dark_mode)
        key?.let {
            if (it == darkModeString) sharedPreferences?.let { pref ->
                val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
                when (pref.getString(darkModeString, darkModeValues[0])) {
                    darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    override fun provideSummary(preference: ListPreference): CharSequence? =
        if (preference.key == getString(R.string.dark_mode)) preference.entry
        else "Unknown Preference"


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}