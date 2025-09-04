package com.example.hostelleaveapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hostelleaveapp.DataModel.Chat_all_fields
import com.example.hostelleaveapp.databinding.ItemUserChatBinding

class HomeChatAdapter(private val onClick:(Chat_all_fields)->Unit):RecyclerView.Adapter<HomeChatAdapter.AddItemViewHolder>() {

  private val list = ArrayList<Chat_all_fields>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemUserChatBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
      return  list.size
    }

    inner class AddItemViewHolder(private val binding:ItemUserChatBinding):RecyclerView.ViewHolder(binding.root) {
         fun bind(position: Int){
             binding.apply {
                 val data = list[position]
                 userName.text = data.name
                 lastMessage.text = data.content
                 messageTime.text = data.timestamp.toString()

                 binding.root.setOnClickListener {
                     onClick(data)
                 }
             }
         }
    }
    fun submitList(newList: List<Chat_all_fields>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }


}