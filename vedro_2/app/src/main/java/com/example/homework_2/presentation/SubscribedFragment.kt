package com.example.homework_2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.Utils.Companion.showSnackBarWithRetryAction
import com.example.homework_2.Utils.Companion.snackBarAction
import com.example.homework_2.data.model.StreamItem
import com.example.homework_2.databinding.FragmentSubscribedBinding
import com.example.homework_2.data.model.TopicItem
import com.example.homework_2.data.networking.Networking
import com.example.homework_2.presentation.adapter.StreamListAdapter
import com.example.homework_2.presentation.adapter.OnTopicItemClickListener
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class SubscribedFragment : CompositeDisposableFragment(), OnTopicItemClickListener {

    private lateinit var binding: FragmentSubscribedBinding
    private lateinit var channelsListRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscribedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTopiclListRecycler()
    }

    override fun topicItemClickListener(topicItem: TopicItem, channelName: String) {
            val bundle = bundleOf(
                TopicActivity.CHANNEL_NAME to channelName,
                TopicActivity.TOPIC_NAME to topicItem.name
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
        }


    private fun initTopiclListRecycler() {
//        channelsListRecyclerView = binding.channelsList
//        val layoutManager = LinearLayoutManager(this.context)
//        channelsListRecyclerView.layoutManager = layoutManager
//        var adapter = StreamListAdapter(this)
//        channelsListRecyclerView.adapter = adapter


        var adapter = StreamListAdapter(this)
        binding.channelsList.adapter = adapter
        channelsListRecyclerView = binding.channelsList
        val layoutManager = LinearLayoutManager(this.context)
        channelsListRecyclerView.layoutManager = layoutManager
        adapter = StreamListAdapter(this)
        getStreamForSubscribed(adapter)
        channelsListRecyclerView.adapter = adapter
    }

    private fun getStreamForSubscribed(adapter: StreamListAdapter) {
        Networking.getZulipApi().getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    adapter.apply {
                        initShimmer = false
                        channels = it.subscriptions
                        channels.forEach { channel ->
                            getTopicsInChannel(channel as StreamItem)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    adapter.apply {
                        initShimmer = false
                        channels = listOf()
                        notifyDataSetChanged()
                    }

                    binding.root.snackBarAction(
                        resources.getString(R.string.channels_error),
                        Snackbar.LENGTH_LONG
                    ) { initTopiclListRecycler() }
                }
            )

            .addTo(compositeDisposable)
    }

    private fun getTopicsInChannel(streamItem: StreamItem) {
        Networking.getZulipApi().getTopicsInStream(streamId = streamItem.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    streamItem.topics = it.topics
                },
                onError = {
                    streamItem.topics = listOf()

                    binding.root.showSnackBarWithRetryAction(
                        binding.root.resources.getString(R.string.topics_error),
                        Snackbar.LENGTH_LONG
                    ) { getTopicsInChannel(streamItem) }
                }
            )
            .addTo(compositeDisposable)
    }
}

