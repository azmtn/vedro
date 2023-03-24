package com.example.homework_2.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.databinding.ItemTopicBinding
import com.example.homework_2.model.TopicItem

internal class TopicItemAdapter(private val topicItemClickListener: TopicItemClickListener) :
    RecyclerView.Adapter<TopicItemAdapter.TopicItemViewHolder>() {

    var topics: List<TopicItem>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, TopicItemDiffCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicItemViewHolder {
        val topicItemBinding = ItemTopicBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return TopicItemViewHolder(topicItemBinding)
    }

    override fun onBindViewHolder(holder: TopicItemViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount() = topics.size

    inner class TopicItemViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topicItem: TopicItem) {
            binding.topicName.text = topicItem.topicName
            binding.messagesCount.text = topicItem.messages.size.toString()
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    topicItem.color
                )
            )
            this@TopicItemAdapter.topicItemClickListener.topicItemClickListener(
                binding.root,
                topicItem
            )
        }
    }
}
