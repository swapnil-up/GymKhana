package com.example.gymkhana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gymkhana.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.signupButton.setOnClickListener{
            val email = binding.signupEmail.text.toString()
            val userName = binding.userName.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && userName.isNotEmpty()){
                if (password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, "Insert valid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password does not matched", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            // Close the SignupActivity
            finish()

        }
    }
}