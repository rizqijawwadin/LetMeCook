package com.bangkit.letmecook.ui.profile
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.bangkit.letmecook.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.letmecook.databinding.FragmentProfileBinding
import com.bangkit.letmecook.ui.usersAuth.SignInActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        val viewPager: ViewPager2 = requireView().findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = requireView().findViewById(R.id.tab_layout)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "No Name"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""
                        val email = user.email ?: "No email"

                        binding.tableDesc.findViewById<TextView>(R.id.profile_name)?.text = username
                        binding.profileEmail.text = "$email"

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

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.txt_favoriteRecipe,
            R.string.btn_setting
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
