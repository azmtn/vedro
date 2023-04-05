package com.example.homework_2.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework_2.R
import com.example.homework_2.databinding.ItemUserBinding
import com.example.homework_2.data.model.UserItem
import com.example.homework_2.presentation.ProfileFragment.Companion.ACTIVE_PRESENCE_COLOR
import com.example.homework_2.presentation.ProfileFragment.Companion.ACTIVE_PRESENCE_KEY
import com.example.homework_2.presentation.ProfileFragment.Companion.IDLE_PRESENCE_COLOR
import com.example.homework_2.presentation.ProfileFragment.Companion.IDLE_PRESENCE_KEY
import com.example.homework_2.presentation.ProfileFragment.Companion.OFFLINE_PRESENCE_COLOR
import com.example.homework_2.presentation.adapter.StreamListAdapter.Companion.SHIMMER_COUNT

internal class PeopleAdapter(private val userItemClickListener: OnUserItemClickListener) :
    RecyclerView.Adapter<PeopleAdapter.PeopleListViewHolder>() {

    var initShimmer = true

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
        if (initShimmer) {
            holder.shimmerFrameLayout.startShimmer()
        } else {
            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.setShimmer(null)
            holder.avatar.foreground = null
            holder.username.foreground = null
            holder.email.foreground = null
            holder.onlineStatusCard.foreground = null
            holder.bind(users[position])
        }
    }

    override fun getItemCount(): Int {
        return if (initShimmer) SHIMMER_COUNT else users.size
    }

    inner class PeopleListViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val username = binding.userName
        internal val email = binding.userEmail
        internal val avatar = binding.avatar
        internal val onlineStatusCard = binding.indicatorOnline
        internal val shimmerFrameLayout = binding.shimmerLayout

        fun bind(user: UserItem) {
            username.text = user.fullName
            email.text = user.email

            if (user.avatarUrl != null) {
                Glide.with(binding.root)
                    .asBitmap()
                    .load(user.avatarUrl)
                    .error(R.drawable.avatar)
                    .into(avatar)
                avatar.setImageResource(R.drawable.avatar_profile)
            } else {
                avatar.setImageResource(R.drawable.avatar)
            }
            onlineStatusCard.backgroundTintList = when (user.presence) {
                ACTIVE_PRESENCE_KEY -> ColorStateList.valueOf(
                    binding.root.context.getColor(
                        ACTIVE_PRESENCE_COLOR
                    )
                )
                IDLE_PRESENCE_KEY -> ColorStateList.valueOf(
                    binding.root.context.getColor(
                        IDLE_PRESENCE_COLOR
                    )
                )
                else -> ColorStateList.valueOf(binding.root.context.getColor(OFFLINE_PRESENCE_COLOR))

            }
            binding.root.setOnClickListener {
                this@PeopleAdapter.userItemClickListener.userItemClickListener(user)
            }
        }
    }
}
