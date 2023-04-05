package com.example.homework_2.presentation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.homework_2.R
import com.example.homework_2.data.networking.Networking
import com.example.homework_2.databinding.FragmentChannelsBinding
import com.example.homework_2.presentation.adapter.ChannelsAdapter
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal class ChannelsFragment: CompositeDisposableFragment() {

    private lateinit var binding: FragmentChannelsBinding
    private val queryEvents: PublishSubject<String> = PublishSubject.create()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChannelsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViewPager()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.searchEditText.doAfterTextChanged { text ->
                val allChannelsTab = binding.tabLayout.getTabAt(1)
                allChannelsTab?.select()
                val query = text?.toString().orEmpty()
                queryEvents.onNext(query)
            }
        }, 100)

        binding.searchIcon.setOnClickListener {
            binding.searchEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }
        subscribeOnSearchChannelsEvents()
    }

    private fun subscribeOnSearchChannelsEvents() {
        queryEvents
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(500L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .map { query ->
                searchChannelsByQuery(query)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun searchChannelsByQuery(query: String) {
        Networking.getZulipApi().getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val channelsAdapter = (binding.pager.adapter as ChannelsAdapter)
                    if (channelsAdapter.isAllChannelsFragment()) {
                        channelsAdapter.allStreamsFragment.updateChannels(
                            it.streams.filter { channel ->
                                channel.name.lowercase().contains(query.lowercase())
                            }
                        )
                    }
                },
                onError = {
                    (binding.pager.adapter as ChannelsAdapter)
                        .allStreamsFragment.updateChannels(listOf())

                    subscribeOnSearchChannelsEvents()

                    Toast.makeText(
                        context,
                        resources.getString(R.string.search_channels_error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            )
            .addTo(compositeDisposable)
    }

    private fun configureViewPager() {
        val viewPager = binding.pager
        val tabLayout = binding.tabLayout

        val pagerAdapter = ChannelsAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf(
                resources.getString(R.string.subscribed),
                resources.getString(R.string.all_streams)
            )
            tab.text = tabNames[position]
        }.attach()
    }
}
