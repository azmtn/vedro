package com.example.homework_2.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.databinding.ItemUserBinding
import com.example.homework_2.model.UserItem
import com.example.homework_2.presentation.ProfileFragment.Companion.OWN_USER_ID

internal class PeopleAdapter(private val userItemClickListener: UserItemClickListener) :
    RecyclerView.Adapter<PeopleAdapter.PeopleListViewHolder>() {

    var users: List<UserItem>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, PeopleDiffCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleListViewHolder {
        val userItemBinding = ItemUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleListViewHolder(userItemBinding)
    }

    override fun onBindViewHolder(holder: PeopleListViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class PeopleListViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val username = binding.userName
        private val email = binding.userEmail
        private val avatar = binding.avatar
        private val onlineStatusCard = binding.indicatorOnline

        fun bind(user: UserItem) {
            username.text = user.name
            email.text = user.email
            if (user.id == OWN_USER_ID) {
                avatar.setImageResource(R.drawable.avatar_profile)
            } else {
                avatar.setImageResource(R.drawable.avatar)
            }
            onlineStatusCard.visibility = if (!user.online) View.GONE else View.VISIBLE
            this@PeopleAdapter.userItemClickListener.userItemClickListener(
                binding.root,
                user
            )
        }
    }

}
