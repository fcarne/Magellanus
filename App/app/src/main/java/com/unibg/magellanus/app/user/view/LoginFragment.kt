package com.unibg.magellanus.app.user.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.unibg.magellanus.app.R
import com.unibg.magellanus.app.ToggleableDrawer
import com.unibg.magellanus.app.user.auth.impl.FirebaseAuthenticationProvider
import com.unibg.magellanus.app.databinding.FragmentLoginBinding
import com.unibg.magellanus.app.user.viewmodel.LoginViewModel
import com.unibg.magellanus.app.user.model.UserAccountAPI

class LoginFragment : Fragment() {

    private val provider = FirebaseAuthenticationProvider()

    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.Factory(UserAccountAPI.create(provider))
    }

    private lateinit var navController: NavController

    private val signIn = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment.
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater, R.layout.fragment_login, container, false
        )

        binding.signInBtn.setOnClickListener { launchSignInFlow() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as ToggleableDrawer).setDrawerEnabled(false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.mapFragment, false)
        }
        navController = findNavController()

        viewModel.successfullySigned.observe(viewLifecycleOwner) {
            if (it) navController.navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment())
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                it,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as ToggleableDrawer).setDrawerEnabled(true)
    }

    private fun launchSignInFlow() {
        signIn.launch(getSignInIntent())
    }

    private fun getSignInIntent(): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        return AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in (Firebase)
            val user = provider.currentUser
            viewModel.signIn(user!!)
        } else {
            if (provider.isUserLoggedIn()) {
                AuthUI.getInstance().signOut(requireContext())
            }
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.log_error,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}