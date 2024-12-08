package com.bangkit.letmecook.ui.usersAuth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.letmecook.MainActivity
import com.bangkit.letmecook.R
import com.bangkit.letmecook.databinding.ActivityCustomProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class CustomProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomProfileBinding
    private var selectedImageUri: Uri? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.profileImageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCustomProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonChoosePhoto.setOnClickListener {
            openGallery()
        }

        binding.buttonSaveProfile.setOnClickListener {
            val username = binding.usernameEditText.text.toString()

            if (username.isNotEmpty()) {
                saveProfileToFirebase(username)
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonNext.setOnClickListener {
            val username = binding.usernameEditText.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username before proceeding", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun saveProfileToFirebase(username: String) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Simpan foto profil ke Firebase Storage jika tersedia
            val bitmap = binding.profileImageView.drawable?.toBitmap()
            if (bitmap != null) {
                uploadProfileImageToStorage(user.uid, bitmap) { imageUrl ->
                    // Simpan username dan URL foto profil ke Firestore
                    saveUserDataToFirestore(user.uid, username, imageUrl)
                }
            } else {
                // Jika tidak ada foto, hanya simpan username
                saveUserDataToFirestore(user.uid, username, null)
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadProfileImageToStorage(userId: String, bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = storage.reference.child("profileImages/$userId.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDataToFirestore(userId: String, username: String, imageUrl: String?) {
        val userMap = hashMapOf(
            "username" to username,
            "profileImageUrl" to (imageUrl ?: "")
        )

        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
