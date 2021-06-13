package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.database.entity.DatabaseAsteroid
import com.udacity.asteroidradar.databinding.ItemAsteroidBinding

class AsteroidAdapter(private val onClickListener: OnClickListener) : ListAdapter<DatabaseAsteroid, AsteroidAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), onClickListener)

    class OnClickListener(private val onClickListener: (DatabaseAsteroid) -> Unit) {
        fun onClick(asteroid: DatabaseAsteroid) = onClickListener(asteroid)
    }

    class ViewHolder(private val binding: ItemAsteroidBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DatabaseAsteroid, onClickListener: OnClickListener) {
            binding.model = item
            binding.rootLayout.setOnClickListener { onClickListener.onClick(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder =
                ViewHolder(ItemAsteroidBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DatabaseAsteroid>() {
        override fun areItemsTheSame(oldItem: DatabaseAsteroid, newItem: DatabaseAsteroid): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DatabaseAsteroid, newItem: DatabaseAsteroid): Boolean = oldItem == newItem
    }

}