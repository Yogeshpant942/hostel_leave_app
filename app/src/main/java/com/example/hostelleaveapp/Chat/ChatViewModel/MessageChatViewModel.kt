package com.example.hostelleaveapp.Chat.ChatViewModel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelleaveapp.Chat.ChatRepo.ChatRepositary
import com.example.hostelleaveapp.DataModel.Chat_all_fields
import com.example.hostelleaveapp.DataModel.HomeChatWarden
import com.example.hostelleaveapp.DataModel.message
import kotlinx.coroutines.launch

class MessageChatViewModel(private val repositary: ChatRepositary) : ViewModel() {
    val message_send = MutableLiveData<Boolean>()
    val recieved_message = MutableLiveData<List<message>>()
    val get_all_warden = MutableLiveData<List<HomeChatWarden>>()
    val fetch_home_message = MutableLiveData<List<Chat_all_fields>>()
    val checkUser = MutableLiveData<Boolean>()

    fun send_message(content: String, receiverId: String) {
        Log.d("MessageChatVM", "Sending message to $receiverId: $content")
        viewModelScope.launch {
            val result = repositary.send_message(content, receiverId)
            Log.d("MessageChatVM", "Message send result: $result")
            message_send.postValue(result)
        }
    }

    fun fetch_message(recieverId: String) {
        Log.d("MessageChatVM", "Fetching messages for $recieverId")
        repositary.fetchMessage(
            receiverId = recieverId,
            onMessagesChanged = {
                recieved_message.postValue(it)
            },
            onError = {
                Log.e("MessageChatVM", "Live fetch error: ${it.message}")
            }
        )
    }

    fun fetch_all_warden() {
        Log.d("MessageChatVM", "Fetching all wardens")
        viewModelScope.launch {
            val res = repositary.fetchWarden()
            Log.d("MessageChatVM", "Fetched ${res.size} wardens")
            get_all_warden.postValue(res)
        }
    }

    fun fetch_data() {
        Log.d("MessageChatVM", "Fetching home screen chat data")
        viewModelScope.launch {
            val res = repositary.fetch_message_dataHomeScreen()
            Log.d("MessageChatVM", "Fetched ${res.size} home chat items")
            fetch_home_message.postValue(res)
        }
    }
    fun checkUserTYPE(){
        viewModelScope.launch {
            val res = repositary.check_warden_or_student()
            checkUser.postValue(res)
        }
    }
}
