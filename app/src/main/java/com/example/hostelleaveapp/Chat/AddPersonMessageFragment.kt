package com.example.hostelleaveapp.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.AllPersonAdapter
import com.example.hostelleaveapp.Chat.ChatRepo.ChatRepositary
import com.example.hostelleaveapp.Chat.ChatViewModel.MessageChatViewModel
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.FragmentAddPersonMessageBinding
import com.google.api.Distribution.BucketOptions.Linear
class AddPersonMessageFragment : Fragment() {
    lateinit var binding:FragmentAddPersonMessageBinding
    lateinit var viewModel:MessageChatViewModel
    lateinit var adapter:AllPersonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPersonMessageBinding.inflate(layoutInflater,container,false)

        val repo = ChatRepositary()
        val factory = MessageChatFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(MessageChatViewModel::class.java)

        adapter = AllPersonAdapter { selected ->
            val bundle = Bundle().apply {
                putString("userId",selected.userId)
            }
            findNavController().navigate(R.id.action_addPersonMessageFragment_to_chatFragment,bundle)
        }
        binding.chatUserRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatUserRecyclerView.adapter = adapter

        viewModel.get_all_warden.observe(viewLifecycleOwner, Observer { list->
            adapter.updateList(list)
        })
        viewModel.fetch_all_warden()

        return binding.root
    }


}