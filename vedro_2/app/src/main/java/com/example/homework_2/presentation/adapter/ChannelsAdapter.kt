package com.example.homework_2.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.homework_2.presentation.AllStreamsFragment
import com.example.homework_2.presentation.SubscribedFragment

internal class ChannelsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    lateinit var subscribedFragment: SubscribedFragment
    lateinit var allStreamsFragment: AllStreamsFragment

    fun isAllChannelsFragment() = ::allStreamsFragment.isInitialized

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                subscribedFragment = SubscribedFragment()
                subscribedFragment
            }
            else -> {
                allStreamsFragment = AllStreamsFragment()
                allStreamsFragment
            }
        }
    }

    override fun getItemCount(): Int = 2

}
