package com.example.cloneinstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cloneinstagram.R
import com.example.cloneinstagram.databinding.ActivityMainBinding
import com.example.cloneinstagram.databinding.FragmentGridBinding
import com.example.cloneinstagram.databinding.FragmentUserBinding
import com.example.cloneinstagram.navigation.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore

class GridFragment:Fragment() {
    var firestore: FirebaseFirestore? = null
    var fragmentView : View? = null

    private var _binding: FragmentGridBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      //  fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_grid, container, false)
        _binding = FragmentGridBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        binding.grigfragmentRecyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        binding.grigfragmentRecyclerview?.layoutManager = GridLayoutManager(activity, 3)
        return binding.root
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.addSnapshotListener { value, error ->
                if(value == null) return@addSnapshotListener
                for (snapshot in value.documents) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels/3
            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(
                RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}