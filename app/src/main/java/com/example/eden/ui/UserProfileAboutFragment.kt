package com.example.eden.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.eden.R
import com.example.eden.databinding.FragmentUserAboutBinding
import com.example.eden.ui.viewmodels.ProfileViewModel
import com.example.eden.util.DateUtils

class UserProfileAboutFragment: Fragment() {
    private lateinit var binding: FragmentUserAboutBinding
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAboutBinding.inflate(
            inflater, container, false
        )
        viewModel = (activity as UserProfileActivity).viewModel

        viewModel.user.observe(requireActivity()){ user ->
            user?.let {
                binding.apply {
                    nameTextViewProfile.text = "${user.firstName} ${user.lastName}"
                    emailTextViewProfile.text = user.email
                    mobileTextViewProfile.text = user.mobileNo
                    dobTextViewProfile.text = DateUtils.toSimpleString(user.dob)
                    countryTextViewProfile.text = user.country.text
                }
            }
        }

        return binding.root
    }
}