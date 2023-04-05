package com.example.homework_2.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.databinding.ItemStreamBinding
import com.example.homework_2.databinding.ItemTopicBinding
import com.example.homework_2.data.model.StreamItem
import com.example.homework_2.data.model.TopicItem
import io.reactivex.disposables.CompositeDisposable

internal class StreamListAdapter(private val topicItemClickListener: OnTopicItemClickListener) :
    RecyclerView.Adapter<StreamListAdapter.BaseViewHolder>() {

    var initShimmer = true
    private var compositeDisposable = CompositeDisposable()
    var channelName = ""


    private val differ = AsyncListDiffer(this, StreamDiffCallback())
    internal var channels: List<Any>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun getItemCount(): Int {
        return if (initShimmer) SHIMMER_COUNT else channels.size
    }

    internal sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        val stream = channels[position]
        return when {
            stream is StreamItem -> VIEW_TYPE_STREAM
            stream is TopicItem -> VIEW_TYPE_TOPIC
            else -> throw RuntimeException("Unknown view type: $position")
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable.dispose()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_STREAM -> {
                val itemStreamView = ItemStreamBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                StreamViewHolder(itemStreamView)
            }
            VIEW_TYPE_TOPIC -> {
                val topicItemBinding = ItemTopicBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                TopicViewHolder(topicItemBinding)
            }
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        when (viewHolder) {
            is StreamViewHolder -> {
                if (initShimmer) {
                    viewHolder.shimmedText.visibility = View.VISIBLE
                    viewHolder.shimmerFrameLayout.startShimmer()
                } else {
                    viewHolder.shimmerFrameLayout.stopShimmer()
                    viewHolder.shimmerFrameLayout.setShimmer(null)
                    viewHolder.shimmedText.visibility = View.GONE
                    viewHolder.channelName.visibility = View.VISIBLE
                    val channel = channels[position] as StreamItem
                    viewHolder.initChannelListener(channel)
                    viewHolder.bind(channel)
                }
            }
            is TopicViewHolder -> {
                if (initShimmer) {
                    viewHolder.shimmerFrameLayout.startShimmer()
                } else {
                    viewHolder.shimmerFrameLayout.stopShimmer()
                    viewHolder.shimmerFrameLayout.setShimmer(null)
                    viewHolder.topicItem.foreground = null

                    viewHolder.bind(channels[position] as TopicItem)
                }
            }
        }
    }


    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        BaseViewHolder(binding.root) {
        internal val shimmerFrameLayout = binding.shimmerLayout
        internal val topicItem = binding.topicItem

        fun bind(topicItem: TopicItem) {
            binding.topicName.text = topicItem.name
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    TOPIC_BACKGROUND_COLOR
                )
            )
            binding.root.setOnClickListener {
                this@StreamListAdapter.topicItemClickListener.topicItemClickListener(
                    topicItem, channelName)
            }
        }
    }

    inner class StreamViewHolder(private val binding: ItemStreamBinding) :
        BaseViewHolder(binding.root) {
        val channelName = binding.channelName
        private val arrowIcon = binding.arrowIcon
        private var isOpened = false
        internal val shimmedText = binding.shimmedText
        internal val shimmerFrameLayout = binding.shimmerLayout

        fun bind(streamItem: StreamItem) {
            channelName.text =
                binding.root.resources.getString(R.string.stream_name, streamItem.name)
        }

        fun initChannelListener(streamItem: StreamItem) {
            binding.root.setOnClickListener {
                if (!isOpened) {
                    initShimmer = false
                    setTopics(streamItem.topics, position)
                    arrowIcon.setImageResource(R.drawable.arrow_up)
                    isOpened = true
                } else {
                    initShimmer = false
                    deleteTopics(position, streamItem.topics.size)
                    arrowIcon.setImageResource(R.drawable.arrow_down)
                    isOpened = false
                }
            }
        }
    }

    fun deleteTopics(position: Int, size: Int) {
        val listChannels: MutableList<Any> = channels.toMutableList()
        for (i in 0 until size) {
            listChannels.removeAt(position + 1)
        }
        channels = listChannels
    }

    fun setTopics(list: List<Any>, position: Int) {
        val listChannels: MutableList<Any> = channels.toMutableList()
        listChannels.addAll(position + 1, list)
        Log.d("channel", "${channels.size}")
        channels = listChannels
    }

    companion object {
        const val VIEW_TYPE_STREAM = 0
        const val VIEW_TYPE_TOPIC = 1
        const val SHIMMER_COUNT = 3
        const val TOPIC_BACKGROUND_COLOR = R.color.teal_700
    }
}