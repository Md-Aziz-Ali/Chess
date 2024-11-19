package com.example.chess

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class UserAdapter(val context: Context, private val dataSet: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val imageView: ImageView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.textView3)
            imageView = view.findViewById(R.id.imageView6)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentUser = dataSet[position]
        viewHolder.textView.text = currentUser.name

        if(currentUser.profileImageUrl != null) {
            Picasso.get()
                .load(currentUser.profileImageUrl)
                .placeholder(R.drawable.person) // Optional placeholder
                .error(R.drawable.error_image)   // Optional error image
                .into(viewHolder.imageView)             // L
        }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, SelectTime::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("receiverId", currentUser.uid)
            intent.putExtra("profileURL", currentUser.profileImageUrl.toString())
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}