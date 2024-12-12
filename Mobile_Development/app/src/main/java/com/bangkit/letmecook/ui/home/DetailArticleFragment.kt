package com.bangkit.letmecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bangkit.letmecook.R
import com.bangkit.letmecook.data.response.Article
import com.bumptech.glide.Glide

class DetailArticleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_article, container, false)

        val article = arguments?.getParcelable<Article>("article")
        val title = view.findViewById<TextView>(R.id.titleArticle)
        val description = view.findViewById<TextView>(R.id.descriptionArticle)
        val image = view.findViewById<ImageView>(R.id.imageArticle)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        article?.let {
            title.text = it.title
            description.text = it.body
            Glide.with(requireContext()).load(it.image).into(image)
        }

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        return view
    }
}
