package com.bangkit.letmecook.ui.stock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.viewbinding.ViewBinding
import com.bangkit.letmecook.R
import com.bangkit.letmecook.databinding.FragmentAddStockBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView

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

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_stock, container, false)

        setupDropdown()
    }

    private fun setupDropdown() {
        val categoryItems = resources.getStringArray(R.array.category_items)
        binding.itemAddStockCategory.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryItems))
        binding.itemAddStockCategory.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.layoutPackaged.visibility = View.VISIBLE
                    binding.layoutFresh.visibility = View.GONE
                }
                1 -> {
                    binding.layoutPackaged.visibility = View.GONE
                    binding.layoutFresh.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddStockFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddStockFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}