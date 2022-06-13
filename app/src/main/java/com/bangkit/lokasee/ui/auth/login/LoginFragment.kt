package com.bangkit.lokasee.ui.auth.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentLoginBinding
import com.bangkit.lokasee.ui.main.MainActivity
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import com.bangkit.lokasee.di.Injection
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.auth.AuthActivity
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        loginViewModel = factory.create(LoginViewModel::class.java)
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            when {
                email.isEmpty() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_input_email)
                        inputEmail.requestFocus()
                    }
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_email_not_valid)
                        inputEmail.requestFocus()
                    }
                }
                password.isEmpty() -> {
                    with(binding) {
                        inputPassword.error = getString(R.string.error_input_password)
                        inputPassword.requestFocus()
                    }
                }
                password.length < 8 -> {
                    with(binding) {
                        inputPassword.error =
                            getString(R.string.error_password_less_than_8_char)
                        inputPassword.requestFocus()
                    }
                }
                else -> {
                    loginUser(email, password)
                }
            }
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Logging In"
        pDialog.setCancelable(false)
        pDialog.show()

        loginViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        val resultResponse = result.data.data
                        if (resultResponse!=null){
                            val token = result.data.accessToken
                            if (token != null) {
                                Log.e("Ini Sebelum saveuser", resultResponse.toString())
                                loginViewModel.saveUser(
                                    resultResponse.id,
                                    resultResponse.name,
                                    resultResponse.email,
                                    resultResponse.phoneNumber,
                                    resultResponse.address,
                                    resultResponse.avatarUrl,
                                    token,
                                    )
                            }
                            Log.e("Ini Habis saveuser", currentUser.toString())
                            pDialog.hide()
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            pDialog.titleText = getString(R.string.title_alert_failed)
                            pDialog.contentText = getString(R.string.message_alert_login_failed)
                            pDialog.show()
                        }
                    }

                    is Result.Error -> {
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        pDialog.titleText = getString(R.string.title_alert_failed)
                        pDialog.contentText = getString(R.string.message_alert_login_failed)
                        pDialog.show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}