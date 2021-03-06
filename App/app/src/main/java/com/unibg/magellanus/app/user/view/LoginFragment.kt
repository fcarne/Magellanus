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
import com.unibg.magellanus.app.databinding.FragmentLoginBinding
import com.unibg.magellanus.app.auth.impl.FirebaseAuthenticationProvider
import com.unibg.magellanus.app.user.model.UserAccountAPI
import com.unibg.magellanus.app.user.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel> {
        val provider = FirebaseAuthenticationProvider()
        LoginViewModel.Factory(provider, UserAccountAPI.create(provider))
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

        // disabilita il drawer
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
        // riabilita il drawer
        (requireActivity() as ToggleableDrawer).setDrawerEnabled(true)
    }

    // i metodi seguenti sono simili al sample proposto da FirebaseAuth UI
    private fun launchSignInFlow() {
        signIn.launch(getSignInIntent())
    }

    // crea l'intent per il login
    private fun getSignInIntent(): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        return AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
    }

    // eseguito quando AuthUI restituisce il controllo all'app
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) viewModel.signIn()
        else {
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.log_error,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}