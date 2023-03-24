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
import com.example.homework_2.databinding.FragmentSubscribedBinding
import com.example.homework_2.model.TopicItem
import com.example.homework_2.presentation.adapter.TopicItemClickListener
import com.example.homework_2.presentation.adapter.TopicsListAdapter

class SubscribedFragment: Fragment(), TopicItemClickListener {

    private lateinit var binding:FragmentSubscribedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscribedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTopiclListRecycler()
    }

    override fun topicItemClickListener(topicItemView: View?, topicItem: TopicItem) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                TopicActivity.CHANNEL_NAME to topicItem.streamName,
                TopicActivity.TOPIC_NAME to topicItem.topicName
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
        }
    }

    private fun initTopiclListRecycler() {
        binding.channelsList.adapter = TopicsListAdapter(this).apply { channels = streamsItems }
    }
}
