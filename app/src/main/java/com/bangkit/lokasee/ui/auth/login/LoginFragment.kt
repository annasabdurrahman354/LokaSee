package com.bangkit.lokasee.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.FragmentLoginBinding
import com.bangkit.lokasee.ui.main.MainActivity
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.ui.ViewModelFactory


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
            binding.progressBar.visible()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            when {
                email.isEmpty() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_input_email)
                        inputEmail.requestFocus()
                        progressBar.gone()
                    }
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_email_not_valid)
                        inputEmail.requestFocus()
                        progressBar.gone()
                    }
                }
                password.isEmpty() -> {
                    with(binding) {
                        inputPassword.error = getString(R.string.error_input_password)
                        inputPassword.requestFocus()
                        progressBar.gone()
                    }
                }
                password.length < 6 -> {
                    with(binding) {
                        inputPassword.error =
                            getString(R.string.error_password_less_than_6_char)
                        inputPassword.requestFocus()
                        progressBar.gone()
                    }
                }
                else -> {
                    loadUser(email, password)
                }
            }
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loadUser(email: String, password: String) {
        loginViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visible()
                    }

                    is Result.Success -> {
                        binding.progressBar.gone()
                        val resultResponse = result.data.data
                        if (resultResponse!=null){
                            val token = result.data.accessToken
                            resultResponse.avatarUrl?.let {
                                if (token != null) {
                                    loginViewModel.saveUser(
                                        resultResponse.id,
                                        resultResponse.name,
                                        resultResponse.email,
                                        resultResponse.phoneNumber,
                                        it,
                                        token,
                                    )
                                }
                            }

                            AlertDialog.Builder(requireContext()).apply {
                                setTitle(getString(R.string.title_alert_success))
                                setMessage(getString(R.string.message_alert_login_success))
                                setPositiveButton(getString(R.string.next)) { _, _ ->
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    activity?.finish()
                                }
                                create()
                                show()
                            }
                        } else {
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle(getString(R.string.title_alert_failed))
                                setMessage(getString(R.string.message_alert_login_failed))
                                setPositiveButton(getString(R.string.back)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                create()
                                show()
                            }
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.gone()
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle(getString(R.string.title_alert_failed))
                            setMessage(getString(R.string.message_alert_login_failed))
                            setPositiveButton(getString(R.string.back)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
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