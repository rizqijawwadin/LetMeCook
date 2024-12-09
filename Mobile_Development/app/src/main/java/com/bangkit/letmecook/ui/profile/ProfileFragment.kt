package com.bangkit.letmecook.ui.profile
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bangkit.letmecook.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.bangkit.letmecook.databinding.FragmentProfileBinding
import com.bangkit.letmecook.ui.usersAuth.SignInActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val profileViewModel =
//            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
    
        binding.btnLogOut.setOnClickListener {
            showConfirmationDialog("Are you sure you want to log out?") {
                firebaseAuth.signOut()
                redirectToLogin()
            }
        }

        binding.btnDeleteAccount.setOnClickListener {
            showConfirmationDialog("Are you sure you want to delete your account?") {
                deleteUserAccount()
            }
        }

        loadUserProfile()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = binding.toggleDarkMode
        val pref = ProfilePreferences.getInstance(requireContext().dataStore)
        val profileViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(ProfileViewModel::class.java)

        profileViewModel.isDarkMode.observe(viewLifecycleOwner) { isDarkMode ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            switchTheme.isChecked = isDarkMode
        }

        switchTheme.setOnCheckedChangeListener() { _, isChecked ->
            profileViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun deleteUserAccount() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                        firestore.collection("users").document(user.uid).delete()
                        redirectToLogin()
                    } else {
                        handleDeleteAccountError(task.exception)
                    }
                }
        }
    }

    private fun handleDeleteAccountError(exception: Exception?) {
        if (exception != null) {
            if (exception.message?.contains("requires recent authentication") == true) {
                reAuthenticateUser()
            } else {
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reAuthenticateUser() {
        val user = firebaseAuth.currentUser ?: return
        val email = user.email ?: return

        // Minta password dari pengguna (dapat dimasukkan melalui dialog atau input text)
        val password = "user_password" // Ambil dari input pengguna
        val credential = EmailAuthProvider.getCredential(email, password)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUserAccount()
                } else {
                    Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadUserProfile() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "No Name"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        // Tampilkan username
                        binding.tableDesc.findViewById<TextView>(R.id.profile_name)?.text = username

                        // Tampilkan foto profil (gunakan Glide untuk memuat URL)
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_proicons_person_24dp)
                            .into(binding.profilePhoto)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("No", null)
            .show()
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
