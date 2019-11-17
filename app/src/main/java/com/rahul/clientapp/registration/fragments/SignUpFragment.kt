package com.rahul.clientapp.registration.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rahul.clientapp.registration.LoginActivity
import com.example.clinic.registration.fragments.SignUpHandlerFragment
import com.rahul.clientapp.R
import kotlinx.android.synthetic.main.fragment_sign_up.*


class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rollNoEditText.setText(LoginActivity.email)

        button.setOnClickListener {
            val password1 = passwordEditText.text.toString()
            val password2 = password2EditText.text.toString()

            if(password1.length < 5 || password1.length > 12) {
                passwordInputLayout.error = "Password must be between 5 to 12 characters"
                return@setOnClickListener
            } else {
                passwordInputLayout.error = null
            }

            if(password1 != password2) {
                password2InputLayout.error = "Password do not match"
                return@setOnClickListener
            } else {
                password2InputLayout.error = null
            }

            LoginActivity.password = password1

            SignUpHandlerFragment.viewPager.currentItem = 1
        }
    }
}
