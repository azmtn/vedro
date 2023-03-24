package com.example.homework_2.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.homework_2.presentation.TopicsFragment
import com.example.homework_2.presentation.SubscribedFragment

internal class ChannelsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubscribedFragment()
            else -> TopicsFragment()
        }
    }

    override fun getItemCount(): Int = 2

}
