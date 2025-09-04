package com.example.hostelleaveapp.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.HomeChatAdapter
import com.example.hostelleaveapp.Chat.ChatRepo.ChatRepositary
import com.example.hostelleaveapp.Chat.ChatViewModel.MessageChatViewModel
import com.example.hostelleaveapp.DataModel.Chat_all_fields
import com.example.hostelleaveapp.DataModel.HomeChatWarden
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.FragmentChatHomeBinding
import com.google.firebase.storage.storageMetadata

class ChatHomeFragment : Fragment() {

    lateinit var binding:FragmentChatHomeBinding
    lateinit var viewModel:MessageChatViewModel
    lateinit var adpater :HomeChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatHomeBinding.inflate(layoutInflater,container,false)

        val repo = ChatRepositary()
        val factory = MessageChatFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(MessageChatViewModel::class.java)
        binding.fabStartChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatHomeFragment_to_addPersonMessageFragment)
        }
        adpater = HomeChatAdapter{onClick->
            val bundle = Bundle().apply {
                putString("userId",onClick.receiverId)
            }
            findNavController().navigate(R.id.action_chatHomeFragment_to_chatFragment,bundle)
        }
        viewModel.checkUser.observe(viewLifecycleOwner, Observer { check->
            if(check){
                binding.fabStartChat.visibility = View.GONE
            }
            else
                binding.fabStartChat.visibility = View.VISIBLE
        })
        viewModel.checkUserTYPE()

        binding.chatUserRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatUserRecyclerView.adapter = adpater
        viewModel.fetch_home_message.observe(viewLifecycleOwner, Observer { list->
            adpater.submitList(list)

        })
        viewModel.fetch_data()
        return binding.root
    }

}