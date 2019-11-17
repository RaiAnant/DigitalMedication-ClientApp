package com.example.clinic.registration.fragments


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.rahul.clientapp.registration.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rahul.clientapp.R
import com.rahul.clientapp.models.Doctor
import com.rahul.clientapp.registration.fragments.listeners.LoginInterfaceListener
import kotlinx.android.synthetic.main.fragment_sign_up_handler.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

import com.rahul.clientapp.registration.fragments.SignUp2Fragment
import com.rahul.clientapp.registration.fragments.SignUpFragment
import com.rahul.clientapp.registration.fragments.SigningUpFragment


class SignUpHandlerFragment : Fragment(), CoroutineScope {

    private val TAG = SignUpHandlerFragment::class.java.simpleName
    private lateinit var loginInterfaceListener: LoginInterfaceListener
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    companion object {
        lateinit var viewPager : ViewPager
    }

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        return inflater.inflate(R.layout.fragment_sign_up_handler, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
       loginInterfaceListener = activity as LoginInterfaceListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = container
        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if(position == 2) {
                    startSignUp()
                }

            }

        })
        container.adapter = mSectionsPagerAdapter
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SignUpFragment()
                1 -> SignUp2Fragment()
//                2 -> SignUp3Fragment()
                else -> SigningUpFragment()
            }
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 3
        }
    }

    private fun startSignUp() {
        mAuth.createUserWithEmailAndPassword(LoginActivity.email, LoginActivity.password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "registration failed")
            }else{
                saveValuesToDatabase(database.getReference(mAuth.currentUser!!.uid))
                SigningUpFragment.showDone()
                Handler().postDelayed(Runnable {
                    loginInterfaceListener.switchToFragment(3)
                }, 200)
            }
        }

    }

    private fun saveValuesToDatabase(reference: DatabaseReference){
        Log.d(TAG, "Trying to push value")
        reference.setValue(Doctor(mAuth.currentUser!!.uid, LoginActivity.name, LoginActivity.specialization, LoginActivity.phNo, LoginActivity.location))
    }
}
