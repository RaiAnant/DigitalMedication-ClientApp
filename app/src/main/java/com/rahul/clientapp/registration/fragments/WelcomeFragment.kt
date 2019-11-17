package com.rahul.clientapp.registration.fragments


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clinic.registration.fragments.SignUpHandlerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.rahul.clientapp.registration.fragments.listeners.LoginInterfaceListener
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.rahul.clientapp.MainActivity
import com.rahul.clientapp.R
import com.rahul.clientapp.models.Client


class WelcomeFragment : Fragment(), CoroutineScope {

    private val TAG = WelcomeFragment::class.java.simpleName
    private lateinit var loginInterfaceListener: LoginInterfaceListener
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

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
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginInterfaceListener = activity as LoginInterfaceListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDetails()
    }

    fun getDetails() {
        if(mAuth.currentUser !=null) {
            Log.v(TAG,"inside getDetails")
            saveDetails(mAuth.currentUser!!.uid, database.getReference("clients/" + mAuth.currentUser!!.uid))
            showButton()
        }else{
            Log.v(TAG,"inside failed to get details")
            SignUpHandlerFragment.viewPager.currentItem = 1
        }
    }

    private fun saveDetails(uid: String, reference: DatabaseReference) {
        val sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               val doc = dataSnapshot.getValue(Client::class.java)

                doc?.let { doctor ->
                    saveDetailsOfDoctor(doctor)
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // ...
            }
        })




    }

    private fun saveDetailsOfDoctor(client: Client) {
        val sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean(getString(R.string.pref_loggedIn), true)
            putString(getString(R.string.client_name), client.name)
            putString(getString(R.string.phno), client.phoneNo)
            putString(getString(R.string.client_id), client.cleintId)
            putString(getString(R.string.sex), client.sex)
            putString(getString(R.string.dob), client.dob)
            putInt(getString(R.string.weight), client.weight)
            putInt(getString(R.string.height), client.height)
            putBoolean(getString(R.string.pref_Verified),true)
            commit()
        }

        Log.d("Welcome", "Entered to firebase")
    }

    private fun showButton() {
        activity!!.runOnUiThread {
            val cx = button2.width / 2
            val cy = button2.height / 2

            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val animator = ViewAnimationUtils.createCircularReveal(button2, cx, cy, 0f, finalRadius)
            animator.duration = 250L

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    button2.visibility = View.VISIBLE
                    progressBar2.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                }
            })

            animator.start()
        }

    }

}
