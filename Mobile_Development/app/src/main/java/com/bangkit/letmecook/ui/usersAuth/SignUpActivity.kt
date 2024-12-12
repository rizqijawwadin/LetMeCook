package com.bangkit.letmecook.ui.usersAuth

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.letmecook.R
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.databinding.ActivitySignUpBinding
import com.bangkit.letmecook.local.entity.UserRequest
import com.bangkit.letmecook.local.entity.UserResponse
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        val fullText = "Already Have Account? Sign In"
        val spannableString = SpannableString(fullText)

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_400))
        spannableString.setSpan(colorSpan, 21, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(clickableSpan, 21, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textViewSignUp.text = spannableString
        binding.textViewSignUp.movementMethod = android.text.method.LinkMovementMethod.getInstance()
        binding.textViewSignUp.highlightColor = android.graphics.Color.TRANSPARENT

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val name = ""
                            val profilePicture = null
                            createUserInApi(name, email, pass, profilePicture)

                            val intent = Intent(this, CustomProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUserInApi(name: String, email: String, password: String, profilePicture: String?) {
        val apiService = ApiConfig.getApiService()
        val userRequest = UserRequest(name, email, password, profilePicture)

        apiService.createUser(userRequest).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    Toast.makeText(this@SignUpActivity, "User created in API: ID ${userResponse?.userId}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignUpActivity, "Failed to create user in API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
