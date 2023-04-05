package com.example.homework_2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.homework_2.R
import com.example.homework_2.Utils.Companion.showSnackBarWithRetryAction
import com.example.homework_2.data.model.UserItem
import com.example.homework_2.data.networking.Networking
import com.example.homework_2.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

internal class ProfileFragment : CompositeDisposableFragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.profileOnline.visibility = View.GONE
//        binding.toolbar.visibility = View.VISIBLE
//        binding.logout.visibility = View.GONE
//        binding.profileText.visibility = View.VISIBLE
//        binding.backIcon.visibility = View.GONE
//        binding.appBar.visibility = View.VISIBLE

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val user: UserItem

        if (arguments == null) {
            getOwnUser ()
        } else {
            user = UserItem(
                userId = requireArguments().getLong(USER_ID),
                fullName = requireArguments().getString(USER_NAME),
                email = requireArguments().getString(EMAIL_KEY),
                avatarUrl = requireArguments().getString(AVATAR_KEY),
                presence = requireArguments().getString(USER_PRESENCE_KEY)
            )
            fillViewsWithUserData(user)
        }
    }

    private fun fillViewsWithUserData(user: UserItem) {
        binding.userName.text = user.fullName
        binding.userPresence.text = user.presence
        when (user.presence) {
            ACTIVE_PRESENCE_KEY -> binding.userPresence.setTextColor(
                binding.root.context.getColor(ACTIVE_PRESENCE_COLOR)
            )
            IDLE_PRESENCE_KEY -> binding.userPresence.setTextColor(
                binding.root.context.getColor(IDLE_PRESENCE_COLOR)
            )
            else -> binding.userPresence.setTextColor(
                binding.root.context.getColor(OFFLINE_PRESENCE_COLOR)
            )
        }

        if (user.avatarUrl != null) {
            Glide.with(binding.root)
                .asBitmap()
                .load(user.avatarUrl)
                .error(R.drawable.avatar)
                .into(binding.profileAvatar)
        } else {
            binding.profileAvatar.setImageResource(R.drawable.avatar)
        }
    }

    private fun getOwnUser() {
        Networking.getZulipApi().getOwnUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    getUserPresence(it)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.people_error),
                        Snackbar.LENGTH_LONG
                    ) { getOwnUser() }
                }
            )
            .addTo(compositeDisposable)
    }


    private fun getUserPresence(user: UserItem) {
        Networking.getZulipApi().getUserPresence(
            userIdOrEmail = user.userId.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    user.presence = it?.presence?.aggregated?.status
                    fillViewsWithUserData(user)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.people_error),
                        Snackbar.LENGTH_LONG
                    ) { getUserPresence(user) }
                }
            )
            .addTo(compositeDisposable)
    }

    companion object {
        const val OWN_USER_ID = 1
        const val USER_ID = "id"
        const val USER_NAME = "username"
        const val USER_STATUS_KEY = "status"
        const val USER_ONLINE_STATUS_KEY = "onlineStatus"


        const val EMAIL_KEY = "email"
        const val USER_PRESENCE_KEY = "presence"
        const val AVATAR_KEY = "avatar"


        const val ACTIVE_PRESENCE_KEY = "active"
        const val IDLE_PRESENCE_KEY = "idle"

        const val ACTIVE_PRESENCE_COLOR = R.color.green_light
        const val IDLE_PRESENCE_COLOR = R.color.orange
        const val OFFLINE_PRESENCE_COLOR = R.color.offline
    }
}
