package com.example.homework_2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.example.homework_2.R
import com.example.homework_2.Utils.Companion.snackBarAction
import com.example.homework_2.databinding.FragmentPeopleBinding
import com.example.homework_2.data.model.UserItem
import com.example.homework_2.data.networking.Networking
import com.example.homework_2.presentation.adapter.OnUserItemClickListener
import com.example.homework_2.presentation.adapter.PeopleAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

internal class PeopleFragment: CompositeDisposableFragment(), OnUserItemClickListener {

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

    override fun userItemClickListener(user: UserItem) {
            val bundle = bundleOf(
                ProfileFragment.USER_ID to user.userId,
                ProfileFragment.USER_NAME to user.fullName,
                ProfileFragment.EMAIL_KEY to user.email,
                ProfileFragment.AVATAR_KEY to user.avatarUrl,
                ProfileFragment.USER_PRESENCE_KEY to user.presence
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_people_to_nav_profile, bundle)
        }

    private fun configurePeopleListRecycler() {
        val adapter = PeopleAdapter(this)

        Networking.getZulipApi().getAllUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    adapter.apply {
                        initShimmer = false
                        users = it.members
                        users.forEach { user ->
                            getUserPresence(user)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    adapter.apply {
                        initShimmer = false
                        users = listOf()
                        notifyDataSetChanged()
                    }

                    binding.root.snackBarAction(
                        resources.getString(R.string.people_error),
                        Snackbar.LENGTH_LONG
                    ) { configurePeopleListRecycler() }
                }
            ).addTo(compositeDisposable)

        binding.peopleList.adapter =  adapter
    }

    private fun getUserPresence(user: UserItem) {
        Networking.getZulipApi().getUserPresence(userIdOrEmail = user.userId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    user.presence = it.presence.aggregated?.status ?: NOT_FOUND_PRESENCE_KEY
                },
                onError = {
                    user.presence = NOT_FOUND_PRESENCE_KEY
                }
            )
            .addTo(compositeDisposable)
    }

    companion object {

        const val NOT_FOUND_PRESENCE_KEY = "not found"
    }
}
