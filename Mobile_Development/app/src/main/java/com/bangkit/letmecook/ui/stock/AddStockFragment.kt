package com.bangkit.letmecook.ui.stock

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bangkit.letmecook.R
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.databinding.FragmentAddStockBinding
import com.bangkit.letmecook.local.entity.InventoryEntity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddStockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddStockFragment : Fragment() {

    private var _binding: FragmentAddStockBinding? = null
    private val binding get() = _binding!!
    private lateinit var autoCompleteTextView: MaterialAutoCompleteTextView

    private var currentImageUri: Uri? = null
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){
        uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private var currentCategory: String = "packaged"
    private var inventoryItem: InventoryEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            inventoryItem = it.getParcelable("inventoryItem")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_add_stock, container, false)
        _binding = FragmentAddStockBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav = requireActivity().findViewById<View>(R.id.nav_view)
        bottomNav.visibility = View.GONE

        setupDropdown()
        return root
    }

    private fun setupDropdown() {
        val categoryItems = resources.getStringArray(R.array.category_items)
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryItems)
        binding.itemAddStockCategory.setAdapter(categoryAdapter)

        val unitItems = resources.getStringArray(R.array.unit_items)
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, unitItems)
        binding.itemAddDropdownStockUnit.setAdapter(unitAdapter)

        binding.itemAddStockCategory.setText(categoryItems[0], false)
        binding.layoutPackaged.visibility = View.VISIBLE
        binding.layoutFresh.visibility = View.GONE

        binding.itemAddStockCategory.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    currentCategory = "packaged"
                    binding.layoutPackaged.visibility = View.VISIBLE
                    binding.layoutFresh.visibility = View.GONE
                }
                1 -> {
                    currentCategory = "fresh"
                    binding.layoutPackaged.visibility = View.GONE
                    binding.layoutFresh.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.itemAddStockImage.setImageURI(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inventoryItem?.let { item ->
            binding.itemAddStockName.setText(item.ingredient_name)
            binding.itemAddStockTotal.setText(item.stock.toString())
            binding.itemAddStockExpired.setText(item.expiry_date)
            binding.itemAddDropdownStockUnit.setText(item.unit, false)
            binding.txtStorageStock.setText(item.place)
            Glide.with(this).load(item.ingredients_pic).into(binding.itemAddStockImage)
        }

        binding.itemAddStockImage.setOnClickListener() {
            startGallery()
        }

        binding.itemAddStockExpired.setOnClickListener() {
            if (currentCategory == "packaged") {
                setCalendar()
            }
        }

        binding.itemAddStockPurchase.setOnClickListener() {
            if (currentCategory == "fresh") {
                setCalendar()
            }
        }

        binding.btnAddStockValid.setOnClickListener() {
            updateStock(inventoryItem)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            show()
            title = resources.getString(R.string.btn_addStock)
            setDisplayHomeAsUpEnabled(true)

        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    private fun setCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                if (currentCategory == "packaged") {
                    binding.itemAddStockExpired.setText(selectedDate)
                } else if (currentCategory == "fresh") {
                    binding.itemAddStockPurchase.setText(selectedDate)
                }
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun addStockToInventory() {
        val name = binding.itemAddStockName.text.toString()
        val quantityText = binding.itemAddStockTotal.text.toString()
        val expiryDate = binding.itemAddStockExpired.text.toString()
        val purchaseDate = binding.itemAddStockPurchase.text.toString()

        if (name.isEmpty() || quantityText.isEmpty() || expiryDate.isEmpty() || currentImageUri == null) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityText.toInt()
//        val userId = FirebaseAuth.getInstance().currentUser?.uid?.toIntOrNull() ?: return
        val userId = 2


        val ingredientId = FirebaseAuth.getInstance().currentUser?.uid?.toIntOrNull() ?: return // ini harusnya tidak seperti ini
        val inventoryId = FirebaseAuth.getInstance().currentUser?.uid?.toIntOrNull() ?: return // ini harusnya tidak seperti ini

        val ingredientsPic = currentImageUri?.toString()
        val unit = binding.itemAddDropdownStockUnit.text.toString()
        val place = binding.txtStorageStock.text.toString()

        val newStock = InventoryEntity(
            user_id_user = userId,
            ingredient_id_ingredient = ingredientId,
            id_inventory = inventoryId,
            ingredients_pic = ingredientsPic,
            buy_date = purchaseDate,
            stock = quantity,
            unit = unit,
            place = place,
            expiry_date = expiryDate,
            ingredient_name = name
        )

        val apiService = ApiConfig.getApiService()
        apiService.addInventory(newStock).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Stock added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to add stock", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStock(item: InventoryEntity?) {
        item?.let {
            val apiService = ApiConfig.getApiService()
            val updatedItem = it.copy(
                ingredient_name = binding.itemAddStockName.text.toString(),
                stock = binding.itemAddStockTotal.text.toString().toInt(),
                expiry_date = binding.itemAddStockExpired.text.toString(),
                unit = binding.itemAddDropdownStockUnit.text.toString(),
                place = binding.txtStorageStock.text.toString()
            )

            apiService.addInventory(updatedItem).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Stock updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Failed to update stock", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val bottomNav = requireActivity().findViewById<View>(R.id.nav_view)
        bottomNav.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(inventoryItem: InventoryEntity): AddStockFragment {
            val fragment = AddStockFragment()
            val args = Bundle().apply {
                putParcelable("inventoryItem", inventoryItem)
            }
            fragment.arguments = args
            return fragment
        }
    }
}