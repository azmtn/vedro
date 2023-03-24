package com.example.homework_2.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.databinding.ItemStreamBinding
import com.example.homework_2.model.StreamItem

internal class TopicsListAdapter(private val topicItemClickListener: TopicItemClickListener)
    : RecyclerView.Adapter<TopicsListAdapter.TopicsListViewHolder>() {

    var channels: List<StreamItem>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, TopicsListDiffCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicsListViewHolder {
        val itemStreamBinding = ItemStreamBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicsListViewHolder(itemStreamBinding)
    }

    override fun onBindViewHolder(holder: TopicsListViewHolder, position: Int) {
        val channel = channels[position]
        holder.initChannelListener(channel)
        holder.bind(channel)
    }

    override fun getItemCount(): Int = channels.size

    inner class TopicsListViewHolder(private val binding: ItemStreamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val channelName = binding.channelName
        private val arrowIcon = binding.arrowIcon
        private var isOpened = false

        fun bind(streamItem: StreamItem) {
            channelName.text =
                binding.root.resources.getString(R.string.stream_name, streamItem.name)
        }

        fun initChannelListener(streamItem: StreamItem) {
            binding.root.setOnClickListener {
                val itemAdapter = TopicItemAdapter(this@TopicsListAdapter.topicItemClickListener)
                if (!isOpened) {
                    itemAdapter.topics = streamItem.topics
                    arrowIcon.setImageResource(R.drawable.arrow_up)
                    isOpened = true

                } else {
                    itemAdapter.topics = listOf()
                    itemAdapter.notifyDataSetChanged()
                    arrowIcon.setImageResource(R.drawable.arrow_down)
                    isOpened = false

                }
                binding.streamsList.adapter = itemAdapter
            }
        }
    }

}
