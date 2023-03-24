package com.example.homework_2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.example.homework_2.R
import com.example.homework_2.data.streamsItems
import com.example.homework_2.databinding.FragmentTopicsBinding
import com.example.homework_2.model.TopicItem
import com.example.homework_2.presentation.adapter.TopicItemClickListener
import com.example.homework_2.presentation.adapter.TopicsListAdapter

internal class TopicsFragment : Fragment(), TopicItemClickListener {

    private lateinit var binding: FragmentTopicsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureChannelListRecycler()
    }

    override fun topicItemClickListener(topicItemView: View?, topic: TopicItem) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                TopicActivity.CHANNEL_NAME to topic.streamName,
                TopicActivity.TOPIC_NAME to topic.topicName
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
        }
    }

    private fun configureChannelListRecycler() {
        binding.channelsList.adapter = TopicsListAdapter(this).apply { channels = streamsItems }
    }

}
