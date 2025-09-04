package com.example.hostelleaveapp.StartingInterface

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.Ui.Student.StudnetSignUpFragment
import com.example.hostelleaveapp.ViewModelFactory.Warden.SIgnUpFactory
import com.example.hostelleaveapp.databinding.FragmentRoleSelectBinding


class RoleSelectFragment : Fragment() {
    lateinit var binding: FragmentRoleSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleSelectBinding.inflate(layoutInflater, container, false)


    binding.signUpText.setOnClickListener()
    {
      val bottomSheet =   SignupOptionBottomSheet{optionSelected->
            when(optionSelected){
                "Warden"->
                findNavController().navigate(R.id.action_roleSelectFragment_to_wardenSignUpFragment)

                "Student"->
                    findNavController().navigate(R.id.action_roleSelectFragment_to_studnetSignUpFragment)

                "Guard"->
                    findNavController().navigate(R.id.action_roleSelectFragment_to_guardSignUpFragment)

            }
        }
        bottomSheet.show(parentFragmentManager, "SignupOptionBottomSheet")

    }
        binding.loginGuardBtn.setOnClickListener {
            findNavController().navigate(R.id.action_roleSelectFragment_to_guardLoginFragment)
        }

        binding.loginStudentBtn.setOnClickListener {
            findNavController().navigate(R.id.action_roleSelectFragment_to_studentLogInFragment)
        }
        binding.loginWardenBtn.setOnClickListener {
            findNavController().navigate(R.id.action_roleSelectFragment_to_wardenLoginFragment)
        }

    return binding.root
}


}