package com.example.hostelleaveapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hostelleaveapp.DataModel.message
import com.example.hostelleaveapp.databinding.ItemMessageReceivedBinding
import com.example.hostelleaveapp.databinding.ItemMessageSentBinding

class ChatAdapter(private val message_list:ArrayList<message>,
                  private val currentUserId: String
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_TYPE_SEND = 1
    private val VIEW_TYPE_RECEIVED = 2
    override fun getItemViewType(position: Int): Int {
        if(message_list[position].senderId == currentUserId)return VIEW_TYPE_SEND
        else
           return VIEW_TYPE_RECEIVED
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       when(viewType){
           VIEW_TYPE_SEND->{
               val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
               return SendViewHolder(binding)
           }

           VIEW_TYPE_RECEIVED->{
               val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
               return ReceiveViewHolder(binding)
           }
           else -> throw IllegalArgumentException("Invalid view type")
       }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SendViewHolder->holder.bind(position)
            is ReceiveViewHolder->holder.bind(position)
        }
    }
    override fun getItemCount(): Int {
        return message_list.size
    }
    inner class SendViewHolder( val binding :ItemMessageSentBinding):RecyclerView.ViewHolder(binding.root) {
       fun bind(position: Int){
         binding.apply {
             val message_data = message_list[position]
             binding.textMessage.text = message_data.content
         }
        }
    }

    inner class ReceiveViewHolder( val binding:ItemMessageReceivedBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.apply {
                val message_data = message_list[position]
                binding.textMessage.text = message_data.content
            }
        }
    }
    fun updateMessages(newMessages: List<message>) {
        message_list.clear()
        message_list.addAll(newMessages)
        notifyDataSetChanged()
    }


}