package com.bangkit.lokasee.ui.auth.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.databinding.FragmentRegisterBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        registerViewModel = factory.create(RegisterViewModel::class.java)
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            binding.progressBar.gone()
            val name = binding.inputName.text.toString()
            val email = binding.inputEmail.text.toString()
            val phoneNumber = binding.inputPhoneNumber.text.toString()
            val password = binding.inputPassword.text.toString()
            when {
                name.isEmpty() -> {
                    with(binding) {
                        inputName.error = getString(R.string.error_input_name)
                        inputName.requestFocus()
                        binding.progressBar.gone()
                    }
                }
                email.isEmpty() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_input_email)
                        inputEmail.requestFocus()
                        progressBar.gone()
                    }
                }
                phoneNumber.isEmpty() -> {
                    with(binding) {
                        inputPhoneNumber.error = getString(R.string.error_input_phone_number)
                        inputPhoneNumber.requestFocus()
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
                    saveUser(name, email, phoneNumber, password)
                }
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun saveUser(name: String, email: String, phoneNumber: String, password: String) {
        registerViewModel.register(name, email, phoneNumber, password)
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visible()
                        }

                        is Result.Success -> {
                            binding.progressBar.gone()
                            if (result.data.data!=null){
                                AlertDialog.Builder(requireContext()).apply {
                                    setTitle(getString(R.string.title_alert_success))
                                    setMessage(getString(R.string.message_alert_register_success))
                                    setPositiveButton(getString(R.string.login)) { _, _ ->
                                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                        // TODO CLEAR ALL BACKSTACK
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                AlertDialog.Builder(requireContext()).apply {
                                    setTitle(getString(R.string.title_alert_failed))
                                    setMessage(result.data.message)
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
                                setMessage(getString(R.string.message_alert_register_failed))
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