package com.bangkit.lokasee.ui.auth.register

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.databinding.FragmentRegisterBinding
import com.bangkit.lokasee.ui.ViewModelFactory

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
            val name = binding.inputName.text.toString()
            val email = binding.inputEmail.text.toString()
            val phoneNumber = binding.inputPhoneNumber.text.toString()
            val password = binding.inputPassword.text.toString()
            val address = binding.inputAddress.text.toString()
            when {
                name.isEmpty() -> {
                    with(binding) {
                        inputName.error = getString(R.string.error_input_name)
                        inputName.requestFocus()
                    }
                }
                email.isEmpty() -> {
                    with(binding) {
                        inputEmail.error = getString(R.string.error_input_email)
                        inputEmail.requestFocus()
                    }
                }
                phoneNumber.isEmpty()  -> {
                    with(binding) {
                        inputPhoneNumber.error = getString(R.string.error_input_phone_number)
                        inputPhoneNumber.requestFocus()
                    }
                }
                phoneNumber.length > 13  -> {
                    with(binding) {
                        inputPhoneNumber.error = "Phone number is invalid!"
                        inputPhoneNumber.requestFocus()
                    }
                }
                address.isEmpty()  -> {
                    with(binding) {
                        inputAddress.error = "Please insert your address!"
                        inputAddress.requestFocus()
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
                    register(name, email, phoneNumber, address, password)
                }
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun register(name: String, email: String, phoneNumber: String, address: String, password: String) {
        val pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Please Wait!"
        pDialog.setCancelable(false)
        pDialog.show()
        registerViewModel.register(name, email, phoneNumber, address, password)
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Success -> {
                            if (result.data.data!=null) {
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                pDialog.titleText = getString(R.string.title_alert_success)
                                pDialog.contentText = getString(R.string.message_alert_register_success)
                                pDialog.setConfirmClickListener {
                                    findNavController().navigateUp()
                                    pDialog.hide()
                                }
                                pDialog.show()

                            } else {
                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                pDialog.titleText = getString(R.string.title_alert_failed)
                                pDialog.contentText = result.data.message
                                pDialog.show()
                            }

                        }

                        is Result.Error -> {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            pDialog.titleText = getString(R.string.title_alert_failed)
                            pDialog.contentText = result.error
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