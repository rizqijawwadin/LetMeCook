package com.bangkit.letmecook.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.letmecook.R
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.databinding.FragmentStockBinding
import com.bangkit.letmecook.local.entity.InventoryEntity
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentStockBinding? = null
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var stockViewModel: StockViewModel
    private lateinit var adapter: InventoryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val stockViewModel =
            ViewModelProvider(this).get(StockViewModel::class.java)

        _binding = FragmentStockBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnAddStock.setOnClickListener() {
            findNavController().navigate(R.id.action_navigation_stock_to_navigation_stock_add)
        }

        inventoryAdapter = InventoryAdapter(
            onEditClicked = { item -> navigateToEdit(item) },
            onDeleteClicked = { id -> deleteInventoryItem(id) }
        )

        binding.listItemStock.adapter = inventoryAdapter
        binding.listItemStock.layoutManager = LinearLayoutManager(requireContext())

        stockViewModel.searchResults.observe(viewLifecycleOwner, { stock ->
            adapter.submitList(stock)
        })

        binding.searchStock.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    stockViewModel.searchStock(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddStock.setOnClickListener() {
            findNavController().navigate(R.id.action_navigation_stock_to_navigation_stock_add)
        }

        inventoryAdapter = InventoryAdapter(
            onEditClicked = { item -> navigateToEdit(item) },
            onDeleteClicked = { id -> deleteInventoryItem(id) }
        )

        binding.listItemStock.adapter = inventoryAdapter
        binding.listItemStock.layoutManager = LinearLayoutManager(requireContext())

        setupRecyclerView()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_AddStock) {
            val stockFragment = StockFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.container, stockFragment, StockFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun setupRecyclerView() {
        val inventoryAdapter = InventoryAdapter(
            onEditClicked = { item -> navigateToEdit(item) },
            onDeleteClicked = { id -> deleteInventoryItem(id) }
        )

        binding.listItemStock.apply {
            adapter = inventoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        loadInventory(inventoryAdapter)
    }

    private fun loadInventory(inventoryAdapter: InventoryAdapter) {
        val apiService = ApiConfig.getApiService()
        val userId = 2

        apiService.getUserInventory(userId).enqueue(object : Callback<List<InventoryEntity>> {
            override fun onResponse(
                call: Call<List<InventoryEntity>>,
                response: Response<List<InventoryEntity>>
            ) {
                if (response.isSuccessful) {
                    inventoryAdapter.submitList(response.body())
                } else {
                    Toast.makeText(context, "Failed to load inventory", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<InventoryEntity>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToEdit(item: InventoryEntity) {
        Toast.makeText(context, "Edit ${item.ingredient_name}", Toast.LENGTH_SHORT).show()
    }

    private fun deleteInventoryItem(id: Int) {
        val apiService = ApiConfig.getApiService()
        apiService.deleteInventoryItem(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    loadInventory(inventoryAdapter)
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchInventory(query: String) {
        val apiService = ApiConfig.getApiService()
        apiService.searchInventory(query).enqueue(object : Callback<List<InventoryEntity>> {
            override fun onResponse(
                call: Call<List<InventoryEntity>>,
                response: Response<List<InventoryEntity>>
            ) {
                if (response.isSuccessful) {
                    val inventory = response.body()
                    inventoryAdapter.submitList(inventory)
                } else {
                    Toast.makeText(context, "Failed to search inventory", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<InventoryEntity>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}