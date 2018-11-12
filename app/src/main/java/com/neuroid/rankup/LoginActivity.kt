package com.neuroid.rankup

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this," "+ p0.errorMessage ,Toast.LENGTH_SHORT).show()
    }

    companion object {
      private val PERMISSION_CODE=999
    }

    val RC_SIGN_IN:Int =1
    lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        configureGoogleClient()
        firebaseAuth = FirebaseAuth.getInstance()

        google_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val intent =Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PERMISSION_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                val account=result.signInAccount
                val idToken=account!!.idToken

                val credential = GoogleAuthProvider.getCredential(idToken,null)
                firebaseAuthWithGoogle(credential)

            }
            else{
                Log.d("Error","Login Failed")
                Toast.makeText(this,"Login UnSuccessfull " ,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential?) {

        firebaseAuth.signInWithCredential(credential!!).addOnSuccessListener { authResult ->
            var logged_email = authResult.user.email
            var logged_activity= Intent(this@LoginActivity,HomeActivity::class.java)
            logged_activity.putExtra("email",logged_email)
            startActivity(logged_activity)
            finish()
            }
            .addOnFailureListener{
                e ->  Toast.makeText(this," "+ e.message ,Toast.LENGTH_SHORT).show()
            }
    }

    private fun configureGoogleClient() {
        // Configure Google Sign In
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleApiClient =GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,options)
                .build()
        mGoogleApiClient.connect()
        
    }


}


