package com.example.hostelleaveapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hostelleaveapp.DataModel.HomeChatWarden
import com.example.hostelleaveapp.databinding.AddPersonLtemBinding

class AllPersonAdapter(private  val onClick:(HomeChatWarden)->Unit) : RecyclerView.Adapter<AllPersonAdapter.PersonViewHolder>() {
    private val personList= ArrayList<HomeChatWarden>()
    inner class PersonViewHolder(val binding: AddPersonLtemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: HomeChatWarden) {
            binding.userName.text = person.name
            Glide.with(binding.profileImage.context)
                .load(person.image)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(binding.profileImage)
            binding.root.setOnClickListener {
                onClick(person)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = AddPersonLtemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonViewHolder(binding)
    }
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(personList[position])
    }

    override fun getItemCount(): Int = personList.size

    fun updateList(list: List<HomeChatWarden>) {
        personList.clear()
        personList.addAll(list)
        notifyDataSetChanged()
    }

}
