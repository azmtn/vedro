package com.example.homework_2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.homework_2.R
import com.example.homework_2.data.getUserById
import com.example.homework_2.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileOnline.visibility = View.GONE
        binding.toolbar.visibility = View.VISIBLE
        binding.logout.visibility = View.GONE

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (arguments == null) {
            val user = getUserById(OWN_USER_ID)
            if (user != null) {
                binding.userName.text = user.name
                binding.profileStatus.text = user.status
                binding.profileAvatar.setImageResource(R.drawable.avatar_profile)
                if (user.online) {
                    binding.profileStatus.visibility = View.VISIBLE
                }
                binding.toolbar.visibility = View.GONE
                binding.logout.visibility = View.VISIBLE
            }
        } else {
            binding.userName.text = arguments?.getString(USER_NAME)
            binding.profileStatus.text = arguments?.getString(USER_STATUS_KEY)
            if (arguments?.getBoolean(USER_ONLINE_STATUS_KEY) == true) {
                binding.profileOnline.visibility = View.VISIBLE
            }
            if (arguments?.getInt(USER_ID) == OWN_USER_ID) {
                binding.toolbar.visibility = View.GONE
                binding.logout.visibility = View.VISIBLE
                binding.profileAvatar.setImageResource(R.drawable.avatar_profile)
            } else {
                binding.profileAvatar.setImageResource(R.drawable.avatar)
            }
        }
    }

    companion object {
        const val OWN_USER_ID = 1
        const val USER_ID = "id"
        const val USER_NAME = "username"
        const val USER_STATUS_KEY = "status"
        const val USER_ONLINE_STATUS_KEY = "onlineStatus"
    }
}
