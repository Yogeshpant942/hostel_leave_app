package com.example.hostelleaveapp.Chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hostelleaveapp.Chat.ChatRepo.ChatRepositary
import com.example.hostelleaveapp.Chat.ChatViewModel.MessageChatViewModel

class MessageChatFactory(private val repository: ChatRepositary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
