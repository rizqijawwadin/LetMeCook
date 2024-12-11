package com.bangkit.letmecook.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bangkit.letmecook.R
import com.bangkit.letmecook.databinding.FragmentProfileBinding
import com.bangkit.letmecook.databinding.FragmentProfileSettingBinding
import com.bangkit.letmecook.ui.usersAuth.SignInActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileSettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileSettingBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile_setting, container, false)
        _binding = FragmentProfileSettingBinding.inflate(inflater, container, false)
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = binding.toggleDarkMode
        val context = context ?: return
        val pref = ProfilePreferences.getInstance(context.dataStore)
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

        val password = "user_password"
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

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showConfirmationDialog(message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("No", null)
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileSettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                ProfileSettingFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}