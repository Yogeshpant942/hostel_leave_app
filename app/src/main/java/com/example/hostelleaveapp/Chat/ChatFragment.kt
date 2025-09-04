package com.example.hostelleaveapp.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hostelleaveapp.Adapter.ChatAdapter
import com.example.hostelleaveapp.Chat.ChatRepo.ChatRepositary
import com.example.hostelleaveapp.Chat.ChatViewModel.MessageChatViewModel
import com.example.hostelleaveapp.R
import com.example.hostelleaveapp.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
    lateinit var binding:FragmentChatBinding
    lateinit var viewModel:MessageChatViewModel
    lateinit var adapter: ChatAdapter
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentChatBinding.inflate(layoutInflater,container,false)
       val receiverId = arguments?.getString("userId")
       adapter = ChatAdapter(arrayListOf(), receiverId!!)

       binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
       binding.chatRecyclerView.adapter = adapter
       val repo = ChatRepositary()
       val factory = MessageChatFactory(repo)
       viewModel = ViewModelProvider(this,factory).get(MessageChatViewModel::class.java)
       binding.sendButton.setOnClickListener{
           val content = binding.messageEditText.text.toString()
           viewModel.send_message(content,receiverId!!)
           binding.messageEditText.text.clear()
       }
       viewModel.recieved_message.observe(viewLifecycleOwner, Observer { list->
           adapter.updateMessages(list)
       })
       if (receiverId != null) {
           viewModel.fetch_message(receiverId)
       }
        return binding.root
    }
}