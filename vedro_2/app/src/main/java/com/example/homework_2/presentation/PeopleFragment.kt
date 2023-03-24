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
import com.example.homework_2.data.usersitems
import com.example.homework_2.databinding.FragmentPeopleBinding
import com.example.homework_2.model.UserItem
import com.example.homework_2.presentation.adapter.UserItemClickListener
import com.example.homework_2.presentation.adapter.PeopleAdapter

internal class PeopleFragment: Fragment(), UserItemClickListener {

    private lateinit var binding: FragmentPeopleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeopleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurePeopleListRecycler()
    }

    override fun userItemClickListener(topicItemView: View?, user: UserItem) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                ProfileFragment.USER_ID to user.id,
                ProfileFragment.USER_NAME to user.name,
                ProfileFragment.USER_STATUS_KEY to user.status,
                ProfileFragment.USER_ONLINE_STATUS_KEY to user.online
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_people_to_nav_profile, bundle)
        }
    }

    private fun configurePeopleListRecycler() {
        binding.peopleList.adapter = PeopleAdapter(this).apply { users = usersitems }
    }

}
